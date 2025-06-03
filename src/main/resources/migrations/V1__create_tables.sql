CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS midias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    media_type VARCHAR(50) NOT NULL,
    release_year INT,
    director VARCHAR(100),
    synopsis VARCHAR(500),
    genre VARCHAR(100),
    poster_image_url VARCHAR(500),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS actors (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS evaluations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    midia_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT,
    comment VARCHAR(500),
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