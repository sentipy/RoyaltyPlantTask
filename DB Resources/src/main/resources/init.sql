CREATE ROLE bank LOGIN ENCRYPTED PASSWORD 'md5a152a95a3a8382823018843798d0981f'
  SUPERUSER CREATEDB CREATEROLE REPLICATION
   VALID UNTIL 'infinity';

CREATE DATABASE bank
  WITH ENCODING='UTF8'
       OWNER=bank
       CONNECTION LIMIT=-1;

CREATE SCHEMA bank
       AUTHORIZATION bank;

--clients

CREATE TABLE bank.clients
(
  id bigserial NOT NULL,
  name character varying(100) NOT NULL,
  password character varying(100) NOT NULL,
  PRIMARY KEY (id)
)
WITH (
OIDS = FALSE
)
;

ALTER TABLE bank.clients
OWNER TO bank;

CREATE UNIQUE INDEX
ON bank.clients (name ASC NULLS LAST);

--accounts

CREATE TABLE bank.accounts
(
  id bigserial NOT NULL,
  account character varying(20),
  client bigint NOT NULL,
  balance numeric(30,4) NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  --CONSTRAINT balance_not_negative CHECK (balance >= 0),
  FOREIGN KEY (client) REFERENCES bank.clients (id) ON UPDATE RESTRICT ON DELETE RESTRICT
)
WITH (
OIDS = FALSE
)
;

ALTER TABLE bank.accounts
OWNER TO bank;

CREATE INDEX
ON bank.accounts (accountNumber ASC NULLS LAST);

CREATE INDEX
ON bank.accounts (client DESC NULLS FIRST);

/*CREATE OR REPLACE FUNCTION bank.set_account()
  RETURNS trigger AS
  $BODY$
BEGIN
  NEW.accountNumber := '40702' + lpad(NEW.id, 15, 0);
  return NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION bank.set_account()
OWNER TO bank;

CREATE TRIGGER set_account
BEFORE INSERT
ON bank.accounts
FOR EACH ROW
WHEN ((new.accountNumber IS NULL))
EXECUTE PROCEDURE bank.set_account();*/

-- documents

CREATE TABLE bank.documents
(
  id bigserial NOT NULL,
  status character varying(30) NOT NULL DEFAULT 'CREATED',
  doc_num character varying(30) NOT NULL DEFAULT 'DOC #' + id,
  acc_dt bigint,
  acc_kt bigint,
  doc_sum numeric(30,4) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (acc_dt) REFERENCES bank.accounts (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  FOREIGN KEY (acc_kt) REFERENCES bank.accounts (id) ON UPDATE RESTRICT ON DELETE RESTRICT
)
WITH (
OIDS = FALSE
)
;

CREATE INDEX
ON bank.documents (acc_dt DESC NULLS LAST);

CREATE INDEX
ON bank.documents (acc_kt DESC NULLS LAST);
