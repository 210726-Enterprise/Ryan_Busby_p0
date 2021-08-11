CREATE TABLE customer (
  id SERIAL PRIMARY KEY,
  first TEXT NOT NULL,
  last TEXT NOT NULL,
  username TEXT NOT NULL UNIQUE,
  pword TEXT NOT NULL
)
;
CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  nickname TEXT,
  type SMALLINT NOT NULL,
  balance NUMERIC NOT NULL CONSTRAINT positive_balance CHECK (balance > 0)
)
;
CREATE TABLE transaction (
  id SERIAL PRIMARY KEY,
  account_id SERIAL NOT NULL REFERENCES account ON DELETE CASCADE,
  ts TIMESTAMP DEFAULT now(),
  amount NUMERIC NOT NULL
)
;
CREATE TABLE customer_account (
  customer_id SERIAL NOT NULL REFERENCES customer ON DELETE CASCADE ON UPDATE CASCADE,
  account_id SERIAL NOT NULL REFERENCES account ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY(customer_id, account_id),
  owner BOOLEAN DEFAULT true
)
;

CREATE EXTENSION pgcrypto; -- enter this as postgres user

-- To add a customer:
-- INSERT INTO customer (first, last, username, pword) VALUES ('joe', 'desanto', 'joed', crypt('password', gen_salt('bf')));

-- WHEN CREATING A NEW ACCOUNT, insert into account (nickname, type, balance) values ('checking2', 2, 100.00) RETURNING id;

-- how to compare password:
-- select * from customer where username = 'system.in' and pword = crypt('system.in', pword);
-- if that is an empty result, then say, invalid log in credentials.
