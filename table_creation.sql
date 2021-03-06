CREATE TABLE Addresses(
street VARCHAR(50),
city VARCHAR(30) NOT NULL,
state VARCHAR(30) NOT NULL,
zip INTEGER,
PRIMARY KEY (zip,street));

CREATE TABLE Plans(
pname VARCHAR(30) PRIMARY KEY,
quota INTEGER);

CREATE TABLE Members(
email VARCHAR(50) PRIMARY KEY,
name VARCHAR(50),
login VARCHAR(30),
phone INTEGER,
is_admin INTEGER,
street VARCHAR (50),
zip INTEGER,
pname VARCHAR(30),
FOREIGN KEY(zip,street) REFERENCES Addresses(zip,street),
FOREIGN KEY(pname) REFERENCES Plans(pname));

CREATE TABLE Titles(
tid INTEGER PRIMARY KEY,
title VARCHAR(100) NOT NULL,
release_date VARCHAR(30),
genre VARCHAR(30),
num_copies INTEGER NOT NULL CHECK (num_copies >=0));

CREATE TABLE Rentals(
rid INTEGER PRIMARY KEY,
rent_date VARCHAR(30) NOT NULL,
return_date VARCHAR(30) NOT NULL,
email VARCHAR(30),
tid INTEGER,
FOREIGN KEY (email) REFERENCES Members(email),
FOREIGN KEY (tid) REFERENCES Titles(tid));

CREATE TABLE Games(
game_id INTEGER,
FOREIGN KEY (game_id) REFERENCES Titles(tid),
PRIMARY KEY(game_id),
platform VARCHAR(30) NOT NULL,
version VARCHAR(30));

CREATE TABLE Awards(
aname VARCHAR(30) PRIMARY KEY);

CREATE TABLE Wins(
tid INTEGER,
aname VARCHAR(30),
FOREIGN KEY (tid) REFERENCES Titles(tid),
FOREIGN KEY (aname) REFERENCES Awards(aname),
PRIMARY KEY(tid,aname),
year INTEGER);

CREATE TABLE Stars(
sid INTEGER PRIMARY KEY,
name VARCHAR(30) NOT NULL,
address VARCHAR(50));

CREATE TABLE Directors(
director_id INTEGER,
FOREIGN KEY (director_id) REFERENCES Stars(sid),
PRIMARY KEY(director_id));

CREATE TABLE Movies(
movie_id INTEGER,
director_id INTEGER,
FOREIGN KEY (movie_id) REFERENCES Titles(tid),
PRIMARY KEY (movie_id),
FOREIGN KEY (director_id) REFERENCES Directors(director_id));

CREATE TABLE Sequel_To(
original_id INTEGER,
sequel_id INTEGER,
FOREIGN KEY(original_id) REFERENCES Movies(movie_id),
FOREIGN KEY(sequel_id) REFERENCES Movies(movie_id),
PRIMARY KEY(original_id, sequel_id));

CREATE TABLE Actors(
actor_id INTEGER,
FOREIGN KEY (actor_id) REFERENCES Stars(sid),
PRIMARY KEY(actor_id));

CREATE TABLE Stars_In(
actor_id INTEGER,
movie_id INTEGER,
FOREIGN KEY(actor_id) REFERENCES Actors(actor_id),
FOREIGN KEY(movie_id) REFERENCES Movies(movie_id),
PRIMARY KEY(actor_id, movie_id));