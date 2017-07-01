DROP TRIGGER IF EXISTS article_fts_update ON posts;
DROP TRIGGER IF EXISTS set_comment_and_user_rating ON comments_votes;
DROP TRIGGER IF EXISTS set_post_and_user_rating ON posts_votes;
DROP TRIGGER IF EXISTS trigger_dialogs_set_last_message ON messages;
DROP FUNCTION IF EXISTS dialogs_set_last_message();
DROP FUNCTION IF EXISTS posts_vector_update();
DROP FUNCTION IF EXISTS trigger_user_votes_for_comment();
DROP FUNCTION IF EXISTS trigger_user_votes_for_post();

DROP TABLE IF EXISTS market_items;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS comments_votes;
DROP TABLE IF EXISTS posts_votes;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS dialog_users;
DROP TABLE IF EXISTS dialogs;
DROP TABLE IF EXISTS registration_verifications;
DROP TABLE IF EXISTS password_reset_verifications;
DROP TABLE IF EXISTS secret_keys_store;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

DROP SEQUENCE IF EXISTS users_seq;
DROP SEQUENCE IF EXISTS dialogs_seq;
DROP SEQUENCE IF EXISTS messages_seq;
DROP SEQUENCE IF EXISTS posts_seq;
DROP SEQUENCE IF EXISTS comments_seq;
DROP SEQUENCE IF EXISTS items_seq;

CREATE SEQUENCE users_seq START 100000;
CREATE SEQUENCE dialogs_seq START 10000;
CREATE SEQUENCE messages_seq START 10000;
CREATE SEQUENCE posts_seq START 1;
CREATE SEQUENCE comments_seq START 10000;
CREATE SEQUENCE items_seq START 100000;

CREATE TABLE users
(
  id         INTEGER PRIMARY KEY DEFAULT nextval('users_seq'),
  name       VARCHAR(30) NOT NULL,
  email      VARCHAR(80) NOT NULL,
  password   VARCHAR(100) NOT NULL,
  registered BIGINT NOT NULL default (extract(epoch from now()) * 1000),
  enabled    BOOL NOT NULL DEFAULT FALSE,
  decent     BOOL NOT NULL DEFAULT TRUE,
  rating     SMALLINT DEFAULT 0,
  bio        VARCHAR(1000) DEFAULT '',
  authority  VARCHAR(15) NOT NULL CHECK (authority IN ('REGULAR', 'MASTER', 'PARTNER', 'MEMBER')) DEFAULT 'REGULAR',
  location   VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN'
);
CREATE UNIQUE INDEX users_unique_name_idx ON users (name);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);
CREATE INDEX users_location_idx ON users USING BTREE (location);

CREATE TABLE registration_verifications
(
  id INTEGER PRIMARY KEY,
  token VARCHAR NOT NULL,
  expiries TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE password_reset_verifications
(
  id INTEGER PRIMARY KEY,
  token VARCHAR NOT NULL,
  expiries TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE secret_keys_store
(
  email_as_login VARCHAR PRIMARY KEY,
  public_secret VARCHAR,
  private_secret VARCHAR,
  expiries TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE user_roles
(
  user_id INTEGER NOT NULL,
  role    VARCHAR CHECK (role IN ('ADMIN', 'USER', 'MODERATOR', 'CONFIDANT')),
  CONSTRAINT user_roles_idx UNIQUE (user_id, role),
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE dialogs
(
  id              INTEGER PRIMARY KEY DEFAULT nextval('dialogs_seq'),
  last_message    INTEGER DEFAULT NULL
);

CREATE TABLE dialog_users
(
  dialog_id   INTEGER NOT NULL,
  user_id     INTEGER NOT NULL,
  CONSTRAINT user_in_dialog_unique_idx UNIQUE (dialog_id, user_id),
  FOREIGN KEY (dialog_id) REFERENCES dialogs (id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- поиск диалога по юзерам:
-- select u2.dialog_id from dialog_users u1 join dialog_users u2 on u1.dialog_id=u2.dialog_id and u2.user_id = XXX where u1.user_id = XXX;
-- select dialog_id from dialog_users u1 join dialog_users u2 on u1.dialog_id=u2.dialog_id where u1.user_id = xxx and u2.user_id = yyy
CREATE TABLE messages
(
  id          INTEGER PRIMARY KEY DEFAULT nextval('messages_seq'),
  from_user   INTEGER NOT NULL,
  text        TEXT NOT NULL,
  sended      BIGINT NOT NULL default (extract(epoch from now()) * 1000),
  readed      BOOL NOT NULL DEFAULT FALSE,
  dialog_id   INTEGER NOT NULL,
  FOREIGN KEY (dialog_id) REFERENCES dialogs (id) ON DELETE CASCADE,
  FOREIGN KEY (from_user) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX messages_sended_idx ON messages USING BTREE (sended DESC);

CREATE OR REPLACE FUNCTION dialogs_set_last_message() RETURNS TRIGGER AS '
DECLARE
  message_id INTEGER;
  dialog_id INTEGER;
BEGIN
  IF (TG_OP = ''INSERT'') THEN
    message_id = NEW.id;
    dialog_id = NEW.dialog_id;
    UPDATE dialogs SET last_message = message_id WHERE id = dialog_id;
  END IF;
  RETURN NULL;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER trigger_dialogs_set_last_message AFTER INSERT ON messages
FOR EACH ROW EXECUTE PROCEDURE dialogs_set_last_message();

CREATE TABLE posts
(
  id        INTEGER PRIMARY KEY DEFAULT nextval('posts_seq'),
  title     VARCHAR(150) NOT NULL,
  content   TEXT NOT NULL,
  tags      VARCHAR(300),
  created   BIGINT NOT NULL default (extract(epoch from now()) * 1000),
  rating    SMALLINT DEFAULT 0,
  fts       TSVECTOR,
  user_id   INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX posts_unique_title_idx ON posts (title);
CREATE INDEX posts_created_idx ON posts USING BTREE (created DESC);

UPDATE posts SET fts=
setweight( coalesce( to_tsvector('russian_hunspell', title),''),'A') || ' ' ||
setweight( coalesce( to_tsvector('russian_hunspell', tags),''),'B') || ' ' ||
setweight( coalesce( to_tsvector('russian_hunspell', content),''),'D');
CREATE INDEX fts_index ON posts USING GIN (fts);

CREATE OR REPLACE FUNCTION posts_vector_update() RETURNS TRIGGER AS '
BEGIN
  IF (TG_OP = ''UPDATE'') THEN
    IF ( OLD.tags <> NEW.tags or OLD.title <> NEW.title or OLD.content <> NEW.content) THEN
      NEW.fts=setweight( coalesce( to_tsvector(''russian_hunspell'', NEW.title),''''),''A'') || '' '' ||
              setweight( coalesce( to_tsvector(''russian_hunspell'', NEW.tags),''''),''B'') || '' '' ||
              setweight( coalesce( to_tsvector(''russian_hunspell'', NEW.content),''''),''D'');
      RETURN NEW;
    ELSE
      RETURN NEW;
    END IF;
  ELSIF (TG_OP = ''INSERT'') THEN
    NEW.fts=setweight( coalesce( to_tsvector(''russian_hunspell'', NEW.title),''''),''A'') || '' '' ||
            setweight( coalesce( to_tsvector(''russian_hunspell'', NEW.tags),''''),''B'') || '' '' ||
            setweight( coalesce( to_tsvector(''russian_hunspell'', NEW.content),''''),''D'');
    RETURN NEW;
  END IF;
  RETURN NULL;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER article_fts_update BEFORE INSERT OR UPDATE ON posts
FOR EACH ROW EXECUTE PROCEDURE posts_vector_update();

CREATE TABLE comments
(
  id        INTEGER PRIMARY KEY DEFAULT nextval('comments_seq'),
  text      TEXT NOT NULL,
  created   BIGINT NOT NULL default (extract(epoch from now()) * 1000),
  rating    SMALLINT DEFAULT 0,
  user_id   INTEGER NOT NULL,
  post_id   INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);
CREATE INDEX comments_created_idx ON comments USING BTREE (created);

CREATE TABLE posts_votes
(
  liked     BOOL NOT NULL,
  user_id   INTEGER NOT NULL,
  post_id   INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (post_id) REFERENCES posts (id),
  CONSTRAINT user_post_vote_pk PRIMARY KEY (user_id, post_id)
);

CREATE OR REPLACE FUNCTION trigger_user_votes_for_post() RETURNS TRIGGER AS '
DECLARE
  liked BOOL;
  author_id INTEGER;
  post_id INTEGER;
BEGIN
  IF TG_OP = ''INSERT'' THEN
    liked = NEW.liked;
    post_id = NEW.post_id;
    SELECT user_id INTO author_id FROM posts WHERE id = post_id LIMIT 1;
    IF liked = TRUE
    THEN
      UPDATE posts SET rating = rating + 1 WHERE id = post_id;
      UPDATE users SET rating = rating + 1 WHERE id = author_id;
    ELSE
      UPDATE posts SET rating = rating - 1 WHERE id = post_id;
      UPDATE users SET rating = rating - 1 WHERE id = author_id;
    END IF;
  ELSIF TG_OP = ''UPDATE'' THEN
    liked = NEW.liked;
    post_id = NEW.post_id;
    SELECT user_id INTO author_id FROM posts WHERE id = post_id LIMIT 1;
    IF liked = TRUE
    THEN
      UPDATE posts SET rating = rating + 2 WHERE id = post_id;
      UPDATE users SET rating = rating + 2 WHERE id = author_id;
    ELSE
      UPDATE posts SET rating = rating - 2 WHERE id = post_id;
      UPDATE users SET rating = rating - 2 WHERE id = author_id;
    END IF;
  ELSIF TG_OP = ''DELETE'' THEN
    liked = OLD.liked;
    post_id = OLD.post_id;
    SELECT user_id INTO author_id FROM posts WHERE id = post_id LIMIT 1;
    IF liked = TRUE
    THEN
      UPDATE posts SET rating = rating - 1 WHERE id = post_id;
      UPDATE users SET rating = rating - 1 WHERE id = author_id;
    ELSE
      UPDATE posts SET rating = rating + 1 WHERE id = post_id;
      UPDATE users SET rating = rating + 1 WHERE id = author_id;
    END IF;
  END IF;
  RETURN NULL;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER set_post_and_user_rating AFTER INSERT OR UPDATE OR DELETE ON posts_votes
FOR EACH ROW EXECUTE PROCEDURE trigger_user_votes_for_post();

CREATE TABLE comments_votes
(
  liked       BOOL NOT NULL,
  user_id     INTEGER NOT NULL,
  comment_id  INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (comment_id) REFERENCES comments (id),
  CONSTRAINT user_comment_vote_pk PRIMARY KEY (user_id, comment_id)
);

CREATE OR REPLACE FUNCTION trigger_user_votes_for_comment() RETURNS TRIGGER AS '
DECLARE
  liked BOOL;
  author_id INTEGER;
  comment_id INTEGER;
  comment_rating_before SMALLINT;
  comment_rating_after SMALLINT;
  user_rating_diff SMALLINT;
BEGIN
  IF TG_OP = ''INSERT'' THEN
    liked = NEW.liked;
    comment_id = NEW.comment_id;
    SELECT user_id INTO author_id FROM comments WHERE id = comment_id LIMIT 1;
    SELECT rating INTO comment_rating_before FROM comments WHERE id = comment_id LIMIT 1;
    IF liked = TRUE
    THEN
      UPDATE comments SET rating = rating + 1 WHERE id = comment_id;
      comment_rating_after := comment_rating_before + 1;
    ELSE
      UPDATE comments SET rating = rating - 1 WHERE id = comment_id;
      comment_rating_after := comment_rating_before - 1;
    END IF;
    user_rating_diff := comment_rating_after / 3 - comment_rating_before / 3;
    IF user_rating_diff <> 0 THEN
      UPDATE users SET rating = rating + user_rating_diff WHERE id = author_id;
    END IF;
  ELSIF TG_OP = ''UPDATE'' THEN
    liked = NEW.liked;
    comment_id = NEW.comment_id;
    SELECT user_id INTO author_id FROM comments WHERE id = comment_id LIMIT 1;
    SELECT rating INTO comment_rating_before FROM comments WHERE id = comment_id LIMIT 1;
    IF liked = TRUE
    THEN
      UPDATE comments SET rating = rating + 2 WHERE id = comment_id;
      comment_rating_after := comment_rating_before + 2;
    ELSE
      UPDATE comments SET rating = rating - 2 WHERE id = comment_id;
      comment_rating_after := comment_rating_before - 2;
    END IF;
    user_rating_diff := comment_rating_after / 3 - comment_rating_before / 3;
    IF user_rating_diff <> 0 THEN
      UPDATE users SET rating = rating + user_rating_diff WHERE id = author_id;
    END IF;
  ELSIF TG_OP = ''DELETE'' THEN
    liked = OLD.liked;
    comment_id = OLD.comment_id;
    SELECT user_id INTO author_id FROM comments WHERE id = comment_id LIMIT 1;
    SELECT rating INTO comment_rating_before FROM comments WHERE id = comment_id LIMIT 1;
    IF liked = TRUE
    THEN
      UPDATE comments SET rating = rating - 1 WHERE id = comment_id;
      comment_rating_after := comment_rating_before - 1;
    ELSE
      UPDATE comments SET rating = rating + 1 WHERE id = comment_id;
      comment_rating_after := comment_rating_before + 1;
    END IF;
    user_rating_diff := comment_rating_after / 3 - comment_rating_before / 3;
    IF user_rating_diff <> 0 THEN
      UPDATE users SET rating = rating + user_rating_diff WHERE id = author_id;
    END IF;
  END IF;
  RETURN NULL;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER set_comment_and_user_rating AFTER INSERT OR UPDATE OR DELETE ON comments_votes
FOR EACH ROW EXECUTE PROCEDURE trigger_user_votes_for_comment();

CREATE TABLE items
(
  id          INTEGER PRIMARY KEY DEFAULT nextval('items_seq'),
  name        VARCHAR(60) NOT NULL,
  description TEXT NOT NULL,
  type        VARCHAR(20) NOT NULL,
  published   BIGINT NOT NULL default (extract(epoch from now()) * 1000),
  user_id     INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX items_type_published_idx ON items USING BTREE (type, published DESC);

CREATE TABLE market_items
(
  id          INTEGER PRIMARY KEY DEFAULT nextval('items_seq'),
  name        VARCHAR(60) NOT NULL,
  description TEXT NOT NULL,
  type        VARCHAR(20) NOT NULL,
  published   BIGINT NOT NULL default (extract(epoch from now()) * 1000),
  cost        INTEGER NOT NULL CHECK (cost > 0),
  contacts    TEXT NOT NULL,
  location    VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN',
  user_id     INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX market_items_cost_idx ON market_items USING BTREE (type, cost ASC, published DESC);
CREATE INDEX market_items_location_idx ON market_items USING BTREE (location);

