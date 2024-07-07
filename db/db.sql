CREATE DATABASE quarkus-social;

CREATE TABLE USERS (
	id bigserial NOT NULL primary key,
	name varchar(100) not null,
	age integer not null
);

CREATE TABLE POSTS (
    id bigserial NOT NULL primary key,
    post_text varchar(150) not null,
    dateTime timestamp not null,
    user_id bigint not null references USERS(id)
);

CREATE TABLE FOLLOWERS (
	ID BIGSERIAL NOT NULL PRIMARY KEY,
	USER_ID BIGINT NOT NULL REFERENCES USERS(ID),
	FOLLOWER_ID BIGINT NOT NULL REFERENCES USERS(ID)
);