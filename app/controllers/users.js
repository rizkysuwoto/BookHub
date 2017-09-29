var User = require('../models/user');
var BookCache = require('../models/bookCache');
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
    User.findById(req.params.id, function(err, user) {
        if (user) {
            if (user.username != req.user.username) {
                return res.status(401).end('Modifying other user');
            } else {
                user.email = req.body.email ? req.body.email : user.emaile;
                user.username = req.body.username ? req.body.username : user.username;
                user.password = req.body.password ? user.generateHash(req.body.password) : user.password;
                user.save();

                res.writeHead(200, {"Content-Type": "application/json"});
                user = user.toObject();
                delete user.password;
                res.end(JSON.stringify(user));
            }
        } else {
            return res.status(400).end('User not found');
        }
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

module.exports.getWishList = function(req, res) {
    var tasks = req.user.wishList.map(function(isbn) {
        return function(callback) {
            BookCache.findOne({isbn10: isbn}, function(err, book) {
                if (err) {
                    callback(err);
                }
                else if (!book) {
                    console.log('Caching new book with ISBN', isbn);
                    var url = 'https://openlibrary.org/api/books?'
                            + 'bibkeys=ISBN:' + isbn
                            + '&format=json&jscmd=data';
                    request(url, function(err, res1, body) {
                        if (!err && res1.statusCode == 200) {
                            var book = JSON.parse(body)['ISBN:' + isbn];
                            newBookCache = new BookCache()
                            newBookCache.title = book.title;
                            newBookCache.author = book.authors[0].name;
                            newBookCache.isbn10 = isbn;
                            newBookCache.save();
                            callback(null, {
                                title: book.title,
                                author: book.authors[0].name,
                                isbn10: isbn,
                            });
                        }
                        else {
                            callback(err);
                        }
                    });
                }
                else {
                    console.log('Book with ISBN', isbn, 'found in cache');
                    callback(null, book);
                }
            });

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
                message: 'Successcully removed '
                       + String(removedCount) + ' '
                       + bookWord + ' from your wish list'
            }));
        }
    );
};
