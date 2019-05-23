CREATE TABLE Person(
	name varchar(100) NOT NULL,
	DoB varchar(100) NULL,
	PRIMARY KEy (name)
);

CREATE TABLE Knows(
	person1 varchar(100) NOT NULL,
	person2 varchar(100) NOT NULL,
	PRIMARY KEY(person1, person2),
	FOREIGN KEY(person1) REFERENCES Person(name),
	FOREIGN KEY(person2) REFERENCES Person(name)
);

CREATE TABLE Location(
	placename varchar(100) NOT NULL,
	size varchar(100) NULL,
	country varchar(100) NOT NULL,
	PRIMARY KEY (placename, country)
);

CREATE TABLE LivesIn(
	name varchar(100) NOT NULL,
	placename varchar(100) NOT NULL,
	country varchar(100) NOT NULL,
	PRIMARY KEY(name,placename,country),
	FOREIGN KEY(name) REFERENCES Person(name),
	FOREIGN KEY(placename, country) REFERENCES Location(placename, country)
);

-- add tuples as well