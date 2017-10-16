var Book = require('../models/book');
var request = require('request');
var async = require('async');

module.exports = {};

function cacheBook(isbn, callback) {
    var tasks = [
        // Look up the book
        function(callback) {
            var url = 'https://openlibrary.org/api/books?bibkeys=ISBN:'
                    + isbn + '&format=json&jscmd=data';
            request(url, function(err, res, body) {
                if (err || res.statusCode != 200) {
                    return callback(new Error('Failed to look up book'));
                }
                try {
                    var book = JSON.parse(body)['ISBN:' + isbn];
                }
                catch (err) {
                    return callback(err);
                }
                var newBook = new Book()
                newBook.title = book.title ? book.title : '';
                newBook.publisher = book.publisher ? book.publisher : '';
                try {
                    newBook.author = book.authors[0].name;
                }
                catch (err) {
                    newBook.author = '';
                }
                try {
                    newBook.isbn10 = book.identifiers.isbn_10[0];
                }
                catch (err) {
                    newBook.isbn10 = isbn;
                }
                try {
                    newBook.isbn13 = book.identifiers.isbn_13[0];
                }
                catch (err) {
                    newBook.isbn13 = '';
                }
                var imageURL;
                try {
                    imageURL = book.cover.large;
                }
                catch (err) {
                    imageURL = '';
                }
                callback(null, newBook, imageURL);
            });
        },
        // Download the thumbnail, then cache the book
        function(newBook, imageURL, callback) {
            request.defaults({encoding: null})(
              imageURL, function(err, res, body) {
                if (!err && res.statusCode == 200) {
                    console.log('Body: ', body);
                    var buffer = new Buffer(body);
                    newBook.thumbnail = buffer.toString('base64');
                }
                else {
                    newBook.thumbnail = '';
                }
                newBook.save(function(err) {
                    if (err) {
                        return callback(err);
                    }
                    console.log('New book with ISBN', isbn, 'cached');
                    callback(null, newBook);
                });
            });
        },
    ];
    async.waterfall(tasks, callback);
}

function getBook(isbn, callback) {
    Book.findOne({$or: [{isbn10: isbn}, {isbn13: isbn}]},
      function(err, book) {
        if (err) {
            return callback(err, null);
        }
        if (!book) {
            return cacheBook(isbn, callback);
        }
        callback(null, book);
    });
}

module.exports.getBook = getBook;

module.exports.searchBook = function(req, res) {
    getBook(req.params.isbn, function(err, book) {
        if (err) {
            return res.status(400).end(err.toString());
        }
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(book));
    });
};
