var User = require('../models/user');
var Book = require('../models/book');
var Transaction = require('../models/transaction');
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
            newUser.profilePicture = req.body.picture;
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

module.exports.update = async (req, res) => {
    try {
        const user = req.user;
        const newPassword = req.body.newPassword;
        const oldPassword = req.body.oldPassword;
        if (oldPassword && newPassword) {
            if (!user.validPassword(oldPassword)) {
                throw new Error('Incorrect password');
            }
            user.password = user.generateHash(newPassword);
        }
        const profilePicture = req.body.profilePicture;
        user.profilePicture = profilePicture ? profilePicture
                                             : user.profilePicture;
        await user.save();
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({message: 'Profile updated'}));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
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

module.exports.getList = async (req, res) => {
    var user = req.user;
    try {
        var promises = req.user[req.path.substring(1)].map(async isbn => {
            return await books.getBookCheckTransaction(isbn, user.username);
        });
        var results = await Promise.all(promises);
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({books: results}));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

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

module.exports.editBookForSale = async (req, res) => {
    try {
        await Book.update(
            {title: req.body.title, 'items.seller': req.user.username},
            {$set: {
                'items.$.price': req.body.price,
                'items.$.condition': req.body.condition
            }}
        );
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({message: 'Update successful'}));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

async function getSellerRating(username) {
    const sellerRatings = await
        Transaction.find({seller: username}, {sellerRating: 1});
    const validRatings =
        sellerRatings.map(transaction => transaction.sellerRating)
                     .filter(rating => rating !== -1);
    if (validRatings.length === 0) {
        return -1;
    }
    return validRatings.reduce((x, y) => x + y, 0)/validRatings.length;
}

async function getBuyerRating(username) {
    const buyerRatings = await
        Transaction.find({buyer: username}, {buyerRating: 1});
    const validRatings =
        buyerRatings.map(transaction => transaction.buyerRating)
                    .filter(rating => rating !== -1);
    if (validRatings.length === 0) {
        return -1;
    }
    return validRatings.reduce((x, y) => x + y, 0)/validRatings.length;
}

module.exports.getProfile = async (req, res) => {
    const username = req.params.username;
    try {
        const user = await User.findOne({username: username});
        const [wishList, inventory, sellerRating, buyerRating] =
          await Promise.all([
            books.isbnsToTitles(user.wishList),
            books.isbnsToTitles(user.myBooks),
            getSellerRating(username),
            getBuyerRating(username),
        ]);
        const value = {
            profilePicture: user.profilePicture,
            wishList: wishList,
            inventory: inventory,
            sellerRating: sellerRating,
            buyerRating: buyerRating,
        };
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify(value));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

module.exports.getTransactionHistory = async (req, res) => {
    const username = req.user.username;
    try {
        const transactions = await Transaction.find({
            $or: [{seller: username}, {buyer: username}]
        });
        const values = await Promise.all(transactions.map(async transaction => {
            const book = await Book.findById(transaction.book, {title: 1});
            return {
                id: transaction._id.toString(),
                isSeller: transaction.seller === username,
                tradedWith: transaction.getTradedWith(username),
                bookTitle: book.title,
                requestMessage: transaction.getRequestMessage(username),
                approvalMessage: transaction.getApprovalMessage(username),
            };
        }));
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({transactions: values}));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

module.exports.getTransaction = async (req, res) => {
    const username = req.user.username;
    try {
        const transaction = await Transaction.findById(req.params.id);
        const book = await Book.findById(
            transaction.book, {_id: 0, thumbnail: 1, author: 1, publisher: 1}
        );
        const value = {
            tradedWithRating: transaction.getTradedWithRating(username),
            tradedWith: transaction.getTradedWith(username),
            book: book,
            dateRequested: transaction.getDateRequestedString(),
            dateApproved: transaction.getDateApprovedString(),
            approved: transaction.approved,
            canApprove: transaction.canApprove(username),
        };
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify(value));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

module.exports.requestTransaction = async (req, res) => {
    try {
        const book = await Book.findOne({isbn10: req.body.isbn10}, {_id: 1});
        const transaction = new Transaction({
            seller: req.body.seller,
            buyer: req.user.username,
            book: book._id,
            sellerRating: -1,
            buyerRating: -1,
            approved: false,
            dateRequested: new Date(),
        });
        await transaction.save();
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({message: 'Transaction requested'}));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

module.exports.approveTransaction = async (req, res) => {
    try {
        await Transaction.update(
            {_id: req.body.id},
            {$set: {approved: true, dateApproved: new Date()}}
        );
        const transaction = await Transaction.findById(req.body.id);
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({
            message: 'Transaction approved',
            dateApproved: transaction.getDateApprovedString(),
        }));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

module.exports.rateUser = async (req, res) => {
    const user = req.user;
    try {
        var property;
        var ratedPerson;
        const transaction = await Transaction.findById(req.body.id);
        if (req.user.username === transaction.seller) {
            updatedRating = user.buyer;
            property = 'buyerRating';
        }
        else {
            updatedRating = user.seller;
            property = 'sellerRating';
        }
        transaction[property] = req.body.rating;
        await transaction.save();
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({message: 'Rating updated'}));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};
