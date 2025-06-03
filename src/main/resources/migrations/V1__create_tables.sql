CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR NOT NULL,
    password VARCHAR,
    email VARCHAR NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS midias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR NOT NULL,
    media_type VARCHAR NOT NULL,
    release_year INT,
    director VARCHAR,
    synopsis VARCHAR,
    genre VARCHAR,
    poster_image_url VARCHAR,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS actors (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR NOT NULL,
    birth_date DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS evaluations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    midia_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT,
    comment VARCHAR,
    evaluation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (midia_id) REFERENCES midias(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS midia_actors (
    midia_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    PRIMARY KEY (midia_id, actor_id),
    FOREIGN KEY (midia_id) REFERENCES midias(id),
    FOREIGN KEY (actor_id) REFERENCES actors(id)
);