var User = require('../models/user');
var Book = require('../models/book');
var books = require('./books.js');
var async = require('async');
var request = require('request');

module.exports = {};

module.exports.create = function(req, res) {
    if (!req.body.email || !req.body.username || !req.body.password)
        return res.status(400).end('Invalid input');

    User.findOne({ username:  req.body.username }, function(err, user) {
        if (user) {
            return res.status(400).end('User already exists');
        } else {

            var newUser = new User();
            newUser.email = req.body.email;
            newUser.username = req.body.username;
            newUser.password = newUser.generateHash(req.body.password);
            newUser.picture = req.body.picture;
            newUser.wishList = [];
            newUser.myBooks = [];

            newUser.save();

            res.writeHead(200, {"Content-Type": "application/json"});

            newUser = newUser.toObject();
            delete newUser.password;
            res.end(JSON.stringify(newUser));
        }
    });
};

module.exports.read = function(req, res) {
    User.findById(req.params.id, function(err, user) {
        if (user) {
            res.writeHead(200, {"Content-Type": "application/json"});
            user = user.toObject();
            delete user.password;
            res.end(JSON.stringify(user));
        } else {
            return res.status(400).end('User not found');
        }
    });
};

module.exports.update = function(req, res) {
    var user = req.user;
    user.email = req.body.email ? req.body.email : user.email;
    user.username = req.body.username ? req.body.username : user.username;
    user.password = req.body.password ? user.generateHash(req.body.password) : user.password;
    user.picture = req.body.picture ? req.body.picture : user.picture;
    user.save(function(err) {
        if (err) {
            return res.writeHead(400).end('Cannot update user');
        }
        res.writeHead(200, {"Content-Type": "application/json"});
        user = user.toObject();
        delete user.password;
        res.end(JSON.stringify(user));
    });
};

module.exports.delete = function(req, res) {
    User.remove({_id: req.params.id}, function(err) {
        res.end('Deleted')
    });
};

module.exports.addToWishList = function(req, res) {
    if (!req.body.isbn10) {
        return res.status(400).end('Invalid input');
    }

    var isbn10 = req.body.isbn10;
    if (req.user.wishList.indexOf(isbn10) !== -1) {
        return res.status(400).end('Already in your wish list');
    }
    req.user.wishList.push(isbn10);
    req.user.save();
    res.writeHead(200, {'Content-Type': 'application/json'});
    res.end(JSON.stringify({
        message: 'Book added to your wish list'
    }));
};

module.exports.getList = function(req, res) {
    var tasks = req.user[req.path.substring(1)].map(function(isbn) {
        return function(callback) {
            books.getBook(isbn, callback);
        };
    });

    async.parallel(tasks, function(err, results) {
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({
            books: results
        }));
    });
}

module.exports.removeFromWishList = function(req, res) {
    var isbnsToRemove = req.body.isbnsToRemove;
    User.update(
        {_id: req.user._id},
        {$pullAll: {wishList: isbnsToRemove}},
        function(err, users) {
            var removedCount = isbnsToRemove.length;
            var bookWord = 'book' + (removedCount === 1 ? '' : 's');
            if (err) {
                return res.status(400).end('Failed to remove ' + bookWord
                                         + ' from your wish list');
            }
            res.writeHead(200, {'Content-Type': 'application/json'});
            res.end(JSON.stringify({
                message: 'Successfully removed '
                       + String(removedCount) + ' '
                       + bookWord + ' from your wish list'
            }));
        }
    );
};

module.exports.addToMyBooks = function(req, res) {
    var isbn10 = req.body.isbn10;
    if (!isbn10) {
        return res.status(400).end('Invalid input');
    }

    var user = req.user;
    async.series([
        function(callback) {
            if (user.myBooks.indexOf(isbn10) !== -1) {
                return callback(new Error('Already in My Books'));
            }
            user.myBooks.push(isbn10);
            user.save(function(err) {
                if (err) {
                    return callback(err);
                }
                callback(null);
            });
        },
        function(callback) {
            Book.findOne({'isbn10': isbn10}, function(err, book) {
                if (err) {
                    return callback(err);
                }
                if (book) {
                    book.items.push({
                        seller: user.username,
                        condition: '',
                        price: 0,
                    });
                    book.save(function(err) {
                        if (err) {
                            return callback(err);
                        }
                        callback(null);
                    });
                }
                else {
                    callback(new Error('Book has not been cached'));
                }
            });
        },
    ],
    function(err) {
        if (err) {
            return res.status(400).end(err.toString());
        }
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({message: 'Book added to My Books'}));
    });
};

module.exports.removeFromMyBooks = function(req, res) {
    var isbnsToRemove = req.body.isbnsToRemove;
    User.update(
        {_id: req.user._id},
        {$pullAll: {myBooks: isbnsToRemove}},
        function(err, users) {
            var removedCount = isbnsToRemove.length;
            var bookWord = 'book' + (removedCount === 1 ? '' : 's');
            if (err) {
                return res.status(400).end('Failed to remove ' + bookWord
                                         + ' from My Books');
            }

            // Now remove the seller, price, condition information from the list
            // of items for these books
            var tasks = isbnsToRemove.map(function(isbnToRemove) {
                return function(callback) {
                    Book.update(
                        {isbn10: isbnToRemove},
                        {$pull: {items: {seller: req.user.username}}},
                        function(err, book) {
                            if (err) {
                                return callback(err);
                            }
                            callback(null, book);
                        }
                    );
                }
            });

            async.parallel(tasks, function(err) {
                if (err) {
                    return res.status(400).end(err.toString());
                }
                res.writeHead(200, {'Content-Type': 'application/json'});
                res.end(JSON.stringify({
                    message: 'Successfully removed '
                           + String(removedCount) + ' '
                           + bookWord + ' from My Books'
                }));
            });
        }
    );
};

