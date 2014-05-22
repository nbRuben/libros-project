drop database if exists realmlibrosdb;
create database realmlibrosdb;

use realmlibrosdb;

create table users (
	username	varchar(255) not null primary key,
	password	char(255) not null,
	name		varchar(255) not null,
	email		varchar(255) not null
);

create table roles (
	username	varchar(255) not null,
	rol			varchar(255) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rol)
);