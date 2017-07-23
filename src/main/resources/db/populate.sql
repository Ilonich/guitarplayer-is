DELETE FROM market_items;
DELETE FROM items;
DELETE FROM comments_votes;
DELETE FROM posts_votes;
DELETE FROM comments;
DELETE FROM posts;
DELETE FROM messages;
DELETE FROM dialogs;
DELETE FROM dialog_users;
DELETE FROM secret_keys_store;
DELETE FROM password_reset_verifications;
DELETE FROM registration_verifications;
DELETE FROM user_roles;
DELETE FROM users;

ALTER SEQUENCE users_seq RESTART WITH 100000;
ALTER SEQUENCE dialogs_seq RESTART WITH 10000;
ALTER SEQUENCE messages_seq RESTART WITH 10000;
ALTER SEQUENCE posts_seq RESTART WITH 1;
ALTER SEQUENCE comments_seq RESTART WITH 10000;
ALTER SEQUENCE items_seq RESTART WITH 100000;

INSERT INTO users (name, email, password, enabled, bio, authority, registered) VALUES
  ('Админ', 'admin@igps.ru', '$2a$10$Zj7Perk/znZzHmk8.07ByuJOMIGpt/2K7fjZ52HbX0BCOgDJ7ixtC', TRUE, 'Я хороший админ, у меня есть кот', 'MEMBER', 1494840045000),
  ('Модератор', 'mod@igps.ru', '$2a$10$QSbul4JCPb/pSCrZ8E7dQuZIWWMHs4WFYHW9kSAt9UUUcuca7gt7m', TRUE, 'Я хороший модератор, у меня есть кофе', 'MEMBER', 1495011510000),
  ('Представитель', 'friend@igps.ru', '$2a$10$D1cWOPMmYN4Qf67pY/LM7elrwEG.3am1SFP2zjlvx7Lk88egoUYBK', TRUE, 'Я хороший знакомый админа, у меня есть сани', 'MEMBER', 1495116562000),
  ('Пользователь', 'voter@yandex.ru', '$2a$10$GE5HKnDO8gyjfAez8LaJ0eOHntisGgBnUHFFr10tAtI86Cyz0F1HS', FALSE, 'Я хороший парень, у меня есть ружье', 'REGULAR', 1495205506000);

INSERT INTO user_roles (user_id, role) VALUES
  (100000, 'ADMIN'),
  (100000, 'MODERATOR'),
  (100000, 'CONFIDANT'),
  (100000, 'USER'),
  (100001, 'MODERATOR'),
  (100001, 'USER'),
  (100002, 'CONFIDANT'),
  (100002, 'USER'),
  (100003, 'USER');

INSERT INTO dialogs (id) VALUES
  (DEFAULT),
  (DEFAULT),
  (DEFAULT);

INSERT INTO dialog_users (dialog_id, user_id) VALUES
  (10000, 100000),
  (10000, 100001),
  (10001, 100001),
  (10001, 100003);

INSERT INTO messages (from_user, text, sended, dialog_id) VALUES
  (100000, 'Текстовый текст', 1495267224000, 10000),
  (100001, 'Что?', 1495267560000, 10000),
  (100000, 'Йа креведко', 1495268247000, 10000),
  (100001, 'Спасайся, админ креведко', 1495268304000, 10001),
  (100003, 'ок', 1495269065000, 10001);

INSERT INTO posts (title, content, tags, user_id) VALUES
  ('Креведочная эпидемия', 'Делайте прививки', 'проблемы учения креведки советы_бывалых', 100001),
  ('Креведочная 2', 'Делайте грустное лицо', 'проблемы учения креведки советы_бывалых', 100001),
  ('Креведочная 3', 'Делайте сайты, а не войну', 'кг/ам учения креведки советы_бывалых', 100000);


INSERT INTO comments (text, user_id, post_id) VALUES
  ('Странный пост', 100003, 1),
  ('Странный коммент', 100003, 1),
  ('Сраный сайт', 100003, 1);

INSERT INTO posts_votes (liked, user_id, post_id) VALUES
  (FALSE, 100001, 3),
  (FALSE, 100002, 3);

INSERT INTO comments_votes (liked, user_id, comment_id) VALUES
  (TRUE, 100000, 10000),
  (TRUE, 100001, 10000),
  (TRUE, 100002, 10000),
  (TRUE, 100003, 10000);

INSERT INTO items (name, description, type, user_id) VALUES
  ('Ibanez SR305 Iron Pewter', 'ну тип крутая))', 'AMP', 100003);

INSERT INTO market_items (name, description, type, cost, contacts, user_id) VALUES
  ('Gitarka', 'Prostaya gitarka', 'GUITAR', 100500, 'ул. Хухуран', 100000);

INSERT INTO registration_verifications (id, token, expiries) VALUES
  (100003, 'YzZhZDk5YjAtYWM1ZC00YjgxLWEyNTAtYWY3MmFiNmFmZGIz', '2017-06-30 18:38:24.995000');

INSERT INTO password_reset_verifications (id, token, expiries) VALUES
  (100003, 'ZjQ2YTc3NDYtODc5MC00Yjc0LWFiMjYtMzVlODYzN2ZhNTE1', '2017-07-24 04:06:39.801000');