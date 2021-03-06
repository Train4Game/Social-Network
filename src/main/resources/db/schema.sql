DROP TABLE IF EXISTS user_avatars;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS tokens;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq AS INTEGER START WITH 100;

CREATE TABLE users
(
    id               INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name             VARCHAR                 NOT NULL,
    surname          VARCHAR                 NOT NULL,
    email            VARCHAR                 NOT NULL,
    password         VARCHAR                 NOT NULL,
    locale           VARCHAR DEFAULT 'en'    NOT NULL,
    registered       TIMESTAMP DEFAULT now() NOT NULL,
    enabled          BOOL DEFAULT FALSE      NOT NULL,
    google_id        VARCHAR,
    facebook_id      VARCHAR,
    vk_id            INTEGER
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
    user_id         INTEGER NOT NULL,
    role            VARCHAR,
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_avatars
(
    id              INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    user_id         INTEGER                 NOT NULL,
    is_current      BOOL DEFAULT TRUE       NOT NULL,
    CONSTRAINT user_avatar_idx UNIQUE (user_id, is_current),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE tokens
(
    id              INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    token           CHAR(36)                                          NOT NULL,
    type            VARCHAR                                           NOT NULL,
    user_id         INTEGER                                           NOT NULL,
    creation_date   TIMESTAMP DEFAULT now()                           NOT NULL,
    expiration_date TIMESTAMP CHECK (creation_date < expiration_date) NOT NULL,
    resend_number   INTEGER   DEFAULT 0                               NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX tokens_unique_token_idx ON tokens (token, type);