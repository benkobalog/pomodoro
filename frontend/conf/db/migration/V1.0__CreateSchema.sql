CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
  email VARCHAR(191) NOT NULL UNIQUE,
  created_at DOUBLE PRECISION NOT NULL DEFAULT extract(epoch FROM current_timestamp),
  pomodoro_seconds INTEGER DEFAULT (25 * 60) NOT NULL,
  break_seconds INTEGER DEFAULT (5 * 60) NOT NULL
);

CREATE TABLE generated_password (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
  users_id uuid REFERENCES users(id),
  password VARCHAR(191) NOT NULL
);

CREATE TABLE oauth_token (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
  users_id uuid REFERENCES users(id),
  token VARCHAR(191) NOT NULL
);

CREATE TABLE pomodoro (
 id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
 started DOUBLE PRECISION NOT NULL,
 finished DOUBLE PRECISION,
 kind VARCHAR(16) NOT NULL DEFAULT 'pomodoro',
 users_id uuid REFERENCES users(id)
);


INSERT INTO users (email)
VALUES ('dev@mail.com'), ('dev2@mail.com');

INSERT INTO generated_password (users_id, password)
VALUES
((SELECT id from users where email ='dev@mail.com'),  '1234'),
((SELECT id from users where email ='dev2@mail.com'), 'asdf');