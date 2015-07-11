CREATE TABLE bank.clients
(
  id bigint AUTO_INCREMENT NOT NULL,
  name VARCHAR2(100) NOT NULL,
  password VARCHAR2(100) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE bank.accounts
(
  id bigint AUTO_INCREMENT NOT NULL,
  account VARCHAR2(20),
  client bigint NOT NULL,
  balance numeric(30,4) NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT balance_not_negative CHECK (balance >= 0),
  FOREIGN KEY (client) REFERENCES bank.clients (id) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE UNIQUE INDEX
ON bank.clients (name ASC NULLS LAST);

CREATE TABLE bank.documents
(
  id bigint AUTO_INCREMENT NOT NULL,
  status VARCHAR2(30) NOT NULL DEFAULT 'CREATED',
  doc_num VARCHAR2(30),
  acc_dt bigint,
  acc_kt bigint,
  doc_sum numeric(30,4) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (acc_dt) REFERENCES bank.accounts (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  FOREIGN KEY (acc_kt) REFERENCES bank.accounts (id) ON UPDATE RESTRICT ON DELETE RESTRICT
)