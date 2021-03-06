CREATE TABLE USER (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    FACEBOOKID BIGINT UNSIGNED UNIQUE,
    USERNAME VARCHAR(30) NOT NULL UNIQUE,
    EMAIL VARCHAR(50) NOT NULL UNIQUE,
    SIGNUPDATE timestamp NOT NULL DEFAULT NOW(),
    SCORE INTEGER DEFAULT 0
);

CREATE TABLE SIMPLEPOST (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    DESCRIPTION VARCHAR(255),
    POSTTIME timestamp NOT NULL DEFAULT NOW(),
    POSTEDBY_ID INTEGER NOT NULL,
    PHOTO_IDS VARCHAR(255),
    FOREIGN KEY(POSTEDBY_ID) REFERENCES USER(ID)
);

CREATE TABLE USERSEEPOST (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    POST_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    FOREIGN KEY(POST_ID) REFERENCES SIMPLEPOST(ID),
    FOREIGN KEY(USER_ID) REFERENCES USER(ID)
);

CREATE TABLE FRIENDSHIP (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    FRIENDSSINCE timestamp NOT NULL DEFAULT NOW(),
    USER1_ID INTEGER NOT NULL,
    USER2_ID INTEGER NOT NULL,
    FOREIGN KEY(USER1_ID) REFERENCES USER(ID),
    FOREIGN KEY(USER2_ID) REFERENCES USER(ID)
);

CREATE TABLE FRIENDREQUEST (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    USERFROM_ID INTEGER NOT NULL,
    USERTO_ID INTEGER NOT NULL,
    FOREIGN KEY(USERFROM_ID) REFERENCES USER(ID),
    FOREIGN KEY(USERTO_ID) REFERENCES USER(ID)
);

CREATE TABLE PHOTO (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    SIMPLEPOST_ID INTEGER NOT NULL,
    DESCRIPTION VARCHAR(255),
    PATH VARCHAR(255),
    FOREIGN KEY(SIMPLEPOST_ID) REFERENCES SIMPLEPOST(ID)
);

CREATE TABLE LIKES (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    USER_ID INTEGER NOT NULL,
    PHOTO_ID INTEGER NOT NULL,
    FOREIGN KEY(USER_ID) REFERENCES USER(ID),
    FOREIGN KEY(PHOTO_ID) REFERENCES PHOTO(ID)
);

CREATE TABLE DISLIKES (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    USER_ID INTEGER NOT NULL,
    PHOTO_ID INTEGER NOT NULL,
    FOREIGN KEY(USER_ID) REFERENCES USER(ID),
    FOREIGN KEY(PHOTO_ID) REFERENCES PHOTO(ID)
);

CREATE TABLE COMMENT (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    CONTENT VARCHAR(255),
    COMMENTTIME BIGINT,
    USER_ID INTEGER NOT NULL,
    PHOTO_ID INTEGER NOT NULL,
    FOREIGN KEY(USER_ID) REFERENCES USER(ID),
    FOREIGN KEY(PHOTO_ID) REFERENCES PHOTO(ID)
);