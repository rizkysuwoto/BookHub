# BookHub

Welcome to BookHub! BookHub is an Android application that helps to student to gain an easy access to find, to sell, and to trade textbooks amongst students.

<a href="http://www.youtube.com/watch?feature=player_embedded&v=d8N53gT1uEM
" target="_blank"><img src="http://img.youtube.com/vi/d8N53gT1uEM/0.jpg" 
alt="BookHub Demo" width="240" height="180" border="10" /></a>
## Release Note

Current Version: Ver 1.0

New Features
* Sign Up: A user can sign up for BookHub. Required credentials are username, password, and e-mail.
* Login: A user can login to the BookHub system by username and password
* Book Search: A user can search a book by its ISBN, title and the name of the author
* Wish List: A user can keep the list of books that he or she wants to buy in Wish List
* My Books: A user can keep the list of books that he or she wants to sell in My Books
* Profile: A user can change his or her profile pictures and password
* Transaction: Users can make transaction through system
* Chat: Users can communicate the detail of transaction through the chat system

## Install Information

Please follow this guide to get a development environment running.

* Clone the source code for the front-end from our master branch
```
git clone https://github.com/thomasquinn/BookHub
```

* Clone the source code for the backend from our backend-master branch
```
git clone https://github.com/thomasquinn/BookHub -b backend-master
```

* Install [Node.js](https://nodejs.org/en/) and [MongoDB](https://www.mongodb.com/download-center#community) on your server

* Set up the Firebase token: 
1) Go to [Firebase](https://console.firebase.google.com) → Click Add Project → Create a project  → Click the gear button on the top left corner → Click Cloud Messaging tab → Copy the Server key

2) Open (your backend directory)/app/controllers/users.js and replace the line 
```
const gcmSender = new gcm.Sender();
```
with
```
const gcmSender = new gcm.Sender(/*Your Server Key*/);
```
