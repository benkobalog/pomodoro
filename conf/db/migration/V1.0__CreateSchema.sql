CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
  email VARCHAR(191) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE pomodoro (
 id uuid PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
 started TIMESTAMP NOT NULL,
 finished TIMESTAMP,
 users_id uuid REFERENCES users(id)
);

-- Create test data
INSERT INTO users (email)
VALUES ('test1@test.com'), ('test2@test.com');

INSERT INTO pomodoro (started, finished, users_id)
values
 (current_timestamp - (25 * interval '1 minute'), current_timestamp, (select id from users where email = 'test1@test.com')),
 (current_timestamp - (25 * interval '1 minute'), current_timestamp, (select id from users where email = 'test2@test.com'));