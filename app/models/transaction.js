var mongoose = require('mongoose');
var Book = require('../models/book');

var transactionSchema = mongoose.Schema({
    seller: String,
    buyer: String,
    book: {type: mongoose.Schema.Types.ObjectId, ref: 'Book'},
    sellerRating: Number,
    buyerRating: Number,
    approved: Boolean,
    dateRequested: Date,
    dateApproved: Date,
});

transactionSchema.methods.getRequestMessage = function(username) {
    return 'Requested by ' + (username === this.buyer ? 'you' : this.buyer);
}

transactionSchema.methods.getApprovalMessage = function(username) {
    if (this.approved) {
        return 'Approved by ' + this.seller;
    }
    if (username === this.seller) {
        return 'Pending your approval';
    }
    return 'Pending ' + this.seller + '\'s approval';
};

transactionSchema.methods.isSeller = function(username) {
    return username === this.seller;
};

transactionSchema.methods.getTradedWith = function(username) {
    return username === this.seller ? this.buyer : this.seller;
};

transactionSchema.methods.getTradedWithRating = function(username) {
    return username === this.seller ? this.buyerRating : this.sellerRating;
};

transactionSchema.methods.getShortDateString = function() {
    if (this.dateApproved) {
        return 'Approved ' + this.dateApproved.toLocaleDateString('en-US');
    }
    return 'Requested ' + this.dateRequested.toLocaleDateString('en-US');
};

const dateOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
};

transactionSchema.methods.getDateApprovedString = function() {
    if (this.dateApproved) {
        return ' on '
             + this.dateApproved.toLocaleDateString('en-US', dateOptions);
    }
    return '';
};

transactionSchema.methods.getDateRequestedString = function() {
    if (this.dateRequested) {
        return ' on '
             + this.dateRequested.toLocaleDateString('en-US', dateOptions);
    }
    return '';
};

transactionSchema.methods.canApprove = function(username) {
    return username === this.seller && !this.approved;
};

module.exports = mongoose.model('Transaction', transactionSchema);
