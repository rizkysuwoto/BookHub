var users  = require('./controllers/users');
var ensureLoggedIn = require('connect-ensure-login').ensureLoggedIn();

module.exports = function(app, passport) {

    app.post('/login', passport.authenticate('local'), function(req, res) {
        res.redirect('/user/' + req.user._id);
    });

    app.get('/user/:id', isLoggedIn, users.read);
    app.get('/wishList', ensureLoggedIn, users.getList);
    app.get('/myBooks', ensureLoggedIn, users.getList);
    app.put('/user', users.create);
    app.post('/user/:id', isLoggedIn, users.update);
    app.post('/wishList', ensureLoggedIn, users.addToWishList);
    app.post('/wishList/remove', ensureLoggedIn, users.removeFromWishList);
    app.post('/myBooks', ensureLoggedIn, users.addToMyBooks);
    app.post('/myBooks/remove', ensureLoggedIn, users.removeFromMyBooks);
    app.delete('/user/:id', isLoggedIn, users.delete);

    app.post('/logout', function(req, res) {
        req.logout();
        res.end('Logged out')
    });

};

function isLoggedIn(req, res, next) {
    if (req.isAuthenticated())
        return next();

    res.end('Not logged in');
}
