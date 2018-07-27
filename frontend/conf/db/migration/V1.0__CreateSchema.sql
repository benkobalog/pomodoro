CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
  email VARCHAR(191) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE pomodoro (
 id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
 started TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 finished TIMESTAMP,
 users_id uuid REFERENCES users(id)
);


INSERT INTO users (id, email)
VALUES ('7c0325b7-7ead-4c24-abfe-5a7b4e0fc60f', 'dev@mail.com')