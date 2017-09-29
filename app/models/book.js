var mongoose = require('mongoose');

var bookSchema = mongoose.Schema({
    isbn10: String,

    // List of usernames of users selling this book
    sellers: Array,
});

module.exports = mongoose.model('Book', bookSchema);
