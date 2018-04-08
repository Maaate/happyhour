DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE EXTENSION cube;
CREATE EXTENSION earthdistance;

CREATE TABLE account (
  id         CHAR(36)     NOT NULL PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  username   VARCHAR(255) NOT NULL,
  password   VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  phone      VARCHAR(45)  NOT NULL
);

INSERT INTO account (id, name, username, password, first_name, last_name, phone)
VALUES
  ('9b309e02-5bd2-4f19-a944-fe958aecbdb8', 'Root Account', 'admin@happyhourhunter.com.au', 'blah', 'admin', 'admin',
   '1234567890');

CREATE TABLE service_type_group (
  id   SERIAL NOT NULL PRIMARY KEY,
  name TEXT   NOT NULL
);

INSERT INTO service_type_group (name) VALUES ('Happy Hour');
INSERT INTO service_type_group (name) VALUES ('Grub');

CREATE TABLE service_type (
  id                       SERIAL NOT NULL PRIMARY KEY,
  name                     TEXT   NOT NULL,
  service_type_group_id_fk INT    NOT NULL REFERENCES service_type_group (id)
);

INSERT INTO service_type (name, service_type_group_id_fk) VALUES ('Beer', 1);
INSERT INTO service_type (name, service_type_group_id_fk) VALUES ('Wine', 1);
INSERT INTO service_type (name, service_type_group_id_fk) VALUES ('Spirits', 1);
INSERT INTO service_type (name, service_type_group_id_fk) VALUES ('Food', 2);
INSERT INTO service_type (name, service_type_group_id_fk) VALUES ('Cocktails', 1);
INSERT INTO service_type (name, service_type_group_id_fk) VALUES ('cider', 1);
INSERT INTO service_type (name, service_type_group_id_fk) VALUES ('coffee', 2);

CREATE TABLE pub (
  id              CHAR(36)       NOT NULL PRIMARY KEY,
  google_id       VARCHAR(255)            DEFAULT NULL,
  name            VARCHAR(255)   NOT NULL,
  address         VARCHAR(255)   NOT NULL,
  address_suburb  VARCHAR(255)   NOT NULL,
  address_state   VARCHAR(255)   NOT NULL,
  address_country VARCHAR(255)   NOT NULL,
  longitude       DECIMAL(10, 6) NOT NULL,
  latitude        DECIMAL(10, 6) NOT NULL,
  account_id_fk   CHAR(36)       NOT NULL REFERENCES account (id),
  website_url     VARCHAR(255)            DEFAULT NULL,
  phone_number    VARCHAR(255)            DEFAULT NULL,
  hours           TEXT           DEFAULT NULL,
  last_updated    TIMESTAMP      DEFAULT now()::timestamp,
  enabled         BOOLEAN        NOT NULL DEFAULT TRUE
);

INSERT INTO pub (id, name, address, address_suburb, address_state, address_country, longitude, latitude, account_id_fk)
VALUES ('eddcd6cd-b8c3-43a9-bef6-9fad92d378ed', 'test pub', 'middle of the ocean', 'test', 'test', 'test', -60, -140,
        '9b309e02-5bd2-4f19-a944-fe958aecbdb8');

CREATE INDEX pubLocationIndex ON pub USING GIST (ll_to_earth(latitude, longitude));

CREATE TABLE promotion (
  id              CHAR(36)               NOT NULL PRIMARY KEY,
  start_time      TIME WITHOUT TIME ZONE NOT NULL,
  end_time        TIME WITHOUT TIME ZONE NOT NULL,
  description     TEXT                   NOT NULL,
  pub_id_fk       CHAR(36)               NOT NULL REFERENCES pub (id),
  monday          BOOL                   NOT NULL DEFAULT FALSE,
  tuesday         BOOL                   NOT NULL DEFAULT FALSE,
  wednesday       BOOL                   NOT NULL DEFAULT FALSE,
  thursday        BOOL                   NOT NULL DEFAULT FALSE,
  friday          BOOL                   NOT NULL DEFAULT FALSE,
  saturday        BOOL                   NOT NULL DEFAULT FALSE,
  sunday          BOOL                   NOT NULL DEFAULT FALSE,
  next_day_finish BOOLEAN                NOT NULL DEFAULT FALSE,
  enabled         BOOLEAN                NOT NULL DEFAULT TRUE
);

INSERT INTO promotion (id, start_time, end_time, description, pub_id_fk, friday)
VALUES ('92f62d66-e336-430d-9441-d49e7dce2acd', '16:00', '20:00', 'Sample Promotion for Testing', 'eddcd6cd-b8c3-43a9-bef6-9fad92d378ed', true);

CREATE TABLE promotion_service_type (
  promotion_id_fk    CHAR(36) REFERENCES promotion (id),
  service_type_id_fk INT REFERENCES service_type (id)
);

INSERT INTO promotion_service_type (promotion_id_fk, service_type_id_fk) VALUES ('92f62d66-e336-430d-9441-d49e7dce2acd', 1);
INSERT INTO promotion_service_type (promotion_id_fk, service_type_id_fk) VALUES ('92f62d66-e336-430d-9441-d49e7dce2acd', 2);
INSERT INTO promotion_service_type (promotion_id_fk, service_type_id_fk) VALUES ('92f62d66-e336-430d-9441-d49e7dce2acd', 4);

/* Add Users and Voting
 */
CREATE TABLE punter (
  id                CHAR(36)            NOT NULL PRIMARY KEY,
  created           TIMESTAMP           DEFAULT now()::timestamp,
  last_logged_in    TIMESTAMP           DEFAULT now()::timestamp,
  uid               VARCHAR(255)        UNIQUE NOT NULL,
  email             VARCHAR(255)        NOT NULL,
  name              VARCHAR(255)        NOT NULL
);

INSERT INTO punter (id, name, email, uid) VALUES ('1d75e636-8c6f-4629-9318-0654dbd094b7', 'testy mctestface', 'test@test.com', 'test-token');


CREATE TABLE punter_activity (
  id              SERIAL                   NOT NULL PRIMARY KEY,
  timestamp       TIMESTAMP WITH TIME ZONE DEFAULT now()::timestamp,
  punter_id_fk    CHAR(36)                 NOT NULL REFERENCES punter (id),
  pub_id_fk       CHAR(36)                 REFERENCES pub (id),
  promotion_id_fk CHAR(36)                 REFERENCES promotion (id),
  action          TEXT                     NOT NULL
);

