CREATE TABLE players (
	player_id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT UNIQUE,
	points INTEGER
);

INSERT INTO players (name, points) 
VALUES ('Max', 100);

INSERT INTO players (name, points) 
VALUES ('Sophie', 50);