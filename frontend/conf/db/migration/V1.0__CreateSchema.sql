CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
  email VARCHAR(191) NOT NULL UNIQUE,
  created_at DOUBLE PRECISION NOT NULL DEFAULT extract(epoch FROM current_timestamp),
  pomodoro_seconds INTEGER DEFAULT (25 * 60) NOT NULL,
  break_seconds INTEGER DEFAULT (5 * 60) NOT NULL,
  continue_pomodoro BOOLEAN DEFAULT TRUE NOT NULL,
  continue_break BOOLEAN DEFAULT FALSE NOT NULL
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

CREATE TABLE running_pomodoro (
 id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
 started DOUBLE PRECISION NOT NULL,
 kind VARCHAR(16) NOT NULL DEFAULT 'pomodoro',
 users_id uuid REFERENCES users(id)
);

CREATE TABLE pomodoro (
 id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
 started DOUBLE PRECISION NOT NULL,
 finished DOUBLE PRECISION NOT NULL,
 kind VARCHAR(16) NOT NULL,
 users_id uuid REFERENCES users(id)
);


INSERT INTO users (email, pomodoro_seconds, break_seconds)
VALUES
 ('dev@mail.com', 5, 15),
 ('dev2@mail.com', 25*60, 5*60);

INSERT INTO generated_password (users_id, password)
VALUES
((SELECT id from users where email ='dev@mail.com'),  '1234'),
((SELECT id from users where email ='dev2@mail.com'), 'asdf');