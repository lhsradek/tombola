--
-- TombolaApplication 3.14.15 Flyway script 
--
-- This SQL script creates the required tables

----- SEQUENCE hibernate_sequence -----

CREATE SEQUENCE hibernate_sequence MINVALUE 1;

----- SEQUENCE revinfo_seq -----

CREATE SEQUENCE revinfo_seq MINVALUE 1;


----- SEQUENCE tombola_prize_seq -----

CREATE SEQUENCE tombola_prize_seq MINVALUE 1;


----- SEQUENCE tombola_ticket_seq -----

CREATE SEQUENCE tombola_ticket_seq MINVALUE 1;


----- TABLE revinfo -----

CREATE TABLE revinfo (
    rev INTEGER DEFAULT nextval('revinfo_seq') PRIMARY KEY,
    revtstmp BIGINT
);

----- TABLE tombola_prize -----

CREATE TABLE tombola_prize (
    id BIGINT DEFAULT nextval('tombola_prize_seq') PRIMARY KEY,
    prize_name VARCHAR(255) NOT NULL,
    cnt INTEGER NOT NULL,
    issued INTEGER NOT NULL,
    created_date BIGINT NOT NULL,
    modified_date BIGINT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    CONSTRAINT tombola_prize_name_uk UNIQUE (prize_name)
);


----- TABLE tombola_prize_a -----

CREATE TABLE tombola_prize_a (
    id BIGINT NOT NULL,
    rev INTEGER REFERENCES revinfo (rev),
    revtype TINYINT,
    prize_name VARCHAR(255) NOT NULL,
    prize_name_m BOOLEAN,
    cnt INTEGER,
    cnt_m BOOLEAN,
    issued INTEGER,
    issued_m BOOLEAN,
    created_date BIGINT NULL,
    created_date_m BOOLEAN,
    modified_date BIGINT NULL,
    modified_date_m BOOLEAN NULL,
    created_by VARCHAR(255) NULL,
    created_by_m BOOLEAN NULL,
    modified_by VARCHAR(255) NULL,
    modified_by_m BOOLEAN NULL,
    CONSTRAINT primary_key_tombola_prize_a PRIMARY KEY (id, rev)
);
CREATE INDEX key_tombola_prize_a_rev ON tombola_prize_a (rev);


----- TABLE tombola_ticket -----

CREATE TABLE tombola_ticket (
    id BIGINT DEFAULT nextval('tombola_ticket_seq') PRIMARY KEY,
    win BIGINT NULL,
    created_date BIGINT NOT NULL,
    modified_date BIGINT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL
);
CREATE INDEX key_tombola_ticket_win ON tombola_ticket (win);


----- TABLE tombola_ticket_a  -----

CREATE TABLE tombola_ticket_a (
    id BIGINT NOT NULL,
    rev INTEGER REFERENCES revinfo (rev),
    revtype TINYINT,
    win BIGINT NULL,
    win_m BOOLEAN,
    created_date BIGINT NULL,
    created_date_m BOOLEAN NULL,
    modified_date BIGINT NULL,
    modified_date_m BOOLEAN NULL,
    created_by VARCHAR(255) NULL,
    created_by_m BOOLEAN NULL,
    modified_by VARCHAR(255) NULL,
    modified_by_m BOOLEAN NULL,
    CONSTRAINT primary_key_tombola_ticket_a PRIMARY KEY (id, rev)
);
CREATE INDEX key_tombola_ticket_a_rev ON tombola_ticket_a (rev);


----- TABLE tombola_prize_ticket  -----

CREATE TABLE tombola_prize_ticket (
    prize_id BIGINT REFERENCES tombola_prize (id),
    ticket_id BIGINT REFERENCES tombola_ticket (id),
    CONSTRAINT primary_key_tombola_ticket_prize_id PRIMARY KEY (prize_id, ticket_id),
    CONSTRAINT tombola_prize_ticket_id_uk UNIQUE (ticket_id)
);
CREATE INDEX key_tombola_prize_ticket_prize_id ON tombola_prize_ticket (prize_id);


----- TABLE tombola_role -----

CREATE TABLE tombola_role (
  id BIGINT NOT NULL,
  role_name CHARACTER VARYING(255) NOT NULL,
  enabled BOOLEAN,
  CONSTRAINT primary_key_role PRIMARY KEY (id),
  CONSTRAINT role_name UNIQUE (role_name)
);

  
----- TABLE tombola_user -----

CREATE TABLE tombola_user (
  id BIGINT NOT NULL,
  user_name CHARACTER VARYING(255) NOT NULL,
  password CHARACTER VARYING(255) NOT NULL,
  account_non_expired BOOLEAN,
  account_non_locked BOOLEAN,
  credentials_non_expired BOOLEAN,
  enabled BOOLEAN,
  CONSTRAINT primary_key_user PRIMARY KEY (id),
  CONSTRAINT user_name UNIQUE (user_name)
);


----- TABLE tombola_user_role -----

CREATE TABLE tombola_user_role (
  user_id BIGINT REFERENCES tombola_user (id),
  role_id BIGINT REFERENCES tombola_role (id),
  CONSTRAINT primary_key_user_role PRIMARY KEY (user_id, role_id)
);
CREATE INDEX key_user_role_user_id ON tombola_user_role (user_id);
CREATE INDEX key_user_role_role_id ON tombola_user_role (role_id);


----- VIEW tombola_view -----

CREATE VIEW tombola_view AS SELECT ROWNUM() id, a.id user_id, a.user_name, a.enabled user_enabled, c.id role_id,
  c.role_name, c.enabled role_enabled FROM tombola_user a JOIN tombola_user_role b ON a.id = b.user_id
  JOIN tombola_role c ON b.role_id = c.id ORDER BY a.id, c.id;
 

----- VIEW tombola_prize_view -----

CREATE VIEW tombola_prize_view AS SELECT
  a.id, a.prize_name, a.cnt, a.issued,
  CASE WHEN a.created_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.created_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END created_date,
  created_by,  
  CASE WHEN a.modified_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.modified_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END modified_date,
  modified_by
  FROM tombola_prize a ORDER BY id;


----- VIEW tombola_prize_a_view -----

CREATE VIEW tombola_prize_a_view AS SELECT
  TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + revtstmp / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') revdate,
  a.id, a.rev, a.revtype,
  a.prize_name, a.cnt, a.issued,
  CASE WHEN a.created_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.created_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END created_date,
  created_by,  
  CASE WHEN a.modified_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.modified_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END modified_date,
  modified_by
  FROM tombola_prize_a a, revinfo r WHERE a.rev = r.rev ORDER BY revtstmp, id, rev;
 

----- VIEW tombola_ticket_view -----

CREATE VIEW tombola_ticket_view AS SELECT
  a.id, a.win,
  CASE WHEN a.created_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.created_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END created_date,
  created_by,  
  CASE WHEN a.modified_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.modified_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END modified_date,
  modified_by
  FROM tombola_ticket a ORDER BY id;
  
  
----- VIEW tombola_ticket_a_view -----

CREATE VIEW tombola_ticket_a_view AS SELECT
  TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + revtstmp / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') revdate,
  a.id, a.rev, a.revtype,
  a.win,
  CASE WHEN a.created_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.created_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END created_date,
  created_by,  
  CASE WHEN a.modified_date > 0 THEN
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.modified_date / 1000),
    DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END modified_date,
  modified_by
  FROM tombola_ticket_a a, revinfo r WHERE a.rev = r.rev ORDER BY revtstmp, id, rev;
