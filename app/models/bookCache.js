var mongoose = require('mongoose');

var bookCacheSchema = mongoose.Schema({
    isbn10: String,
    author: String,
    title: String,
});

module.exports = mongoose.model('BookCache', bookCacheSchema);
