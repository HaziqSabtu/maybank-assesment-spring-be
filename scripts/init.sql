-- TESTDB.dbo.users
CREATE TABLE TESTDB.dbo.users (
    id uniqueidentifier DEFAULT newid() NOT NULL,
    username varchar(255) NOT NULL,
    created_at datetimeoffset DEFAULT sysdatetimeoffset() NULL,
    updated_at datetimeoffset DEFAULT sysdatetimeoffset() NULL,
    country_code varchar(2) DEFAULT 'MY' NOT NULL,
    password varchar(100) DEFAULT 'password123' NOT NULL,
    CONSTRAINT PK_users PRIMARY KEY (id)
);

-- TESTDB.dbo.follows
CREATE TABLE TESTDB.dbo.follows (
    follower_id uniqueidentifier NOT NULL,
    followee_id uniqueidentifier NOT NULL,
    followed_at datetimeoffset DEFAULT sysdatetimeoffset() NULL,
    CONSTRAINT PK_follows PRIMARY KEY (follower_id, followee_id),
    CONSTRAINT FK_followee FOREIGN KEY (followee_id) REFERENCES TESTDB.dbo.users(id),
    CONSTRAINT FK_follower FOREIGN KEY (follower_id) REFERENCES TESTDB.dbo.users(id),
    CONSTRAINT CK_no_self_follow CHECK (follower_id <> followee_id)
);

-- TESTDB.dbo.places
CREATE TABLE TESTDB.dbo.places (
    user_id uniqueidentifier NOT NULL,
    place_id varchar(500) NOT NULL,
    name varchar(MAX) NOT NULL,
    address varchar(MAX),
    category varchar(MAX),
    created_at datetimeoffset DEFAULT sysdatetimeoffset() NULL,
    CONSTRAINT PK_places PRIMARY KEY (user_id, place_id),
    CONSTRAINT FK_places_user FOREIGN KEY (user_id) REFERENCES TESTDB.dbo.users(id)
);
