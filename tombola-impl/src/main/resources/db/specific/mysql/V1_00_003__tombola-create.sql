--
-- TombolaApplication 3.14.15 Flyway script 
--
-- This SQL script creates the required tables

-- SEQUENCE hibernate_sequence -----

CREATE SEQUENCE hibernate_sequence MINVALUE 1;


-- TABLE revinfo -----

CREATE TABLE revinfo (
  rev BIGINT(20) NOT NULL AUTO_INCREMENT,
  revtstmp BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (rev)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- TABLE tombola_prize -----

CREATE TABLE tombola_prize (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    prize_name VARCHAR(255) NOT NULL,
    cnt INT(11) NOT NULL,
    issued INT(11) NOT NULL,
    created_date BIGINT(20) NOT NULL,
    modified_date BIGINT(20) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY tombola_prize_name_uk (prize_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- TABLE tombola_prize_a -----

CREATE TABLE tombola_prize_a (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    rev BIGINT(20) REFERENCES revinfo (rev),
    revtype TINYINT(4),
    prize_name VARCHAR(255) NOT NULL,
    prize_name_m BIT(1),
    cnt INT(11),
    cnt_m BIT(1),
    issued INT(11),
    issued_m BIT(1),
    created_date BIGINT(20) DEFAULT NULL,
    created_date_m BIT(1) DEFAULT NULL,
    modified_date BIGINT(20) DEFAULT NULL,
    modified_date_m BIT(1) DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_by_m BIT(1) DEFAULT NULL,
    modified_by VARCHAR(255) DEFAULT NULL,
    modified_by_m BIT(1) DEFAULT NULL,
    PRIMARY KEY (id,rev),
    CONSTRAINT tombola_prize_a_rev FOREIGN KEY (rev) REFERENCES revinfo (rev)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- TABLE tombola_ticket  -----

CREATE TABLE tombola_ticket (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    win BIGINT(20) DEFAULT NULL,
    created_date BIGINT(20) NOT NULL,
    modified_date BIGINT(20) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- TABLE tombola_ticket_a  -----

CREATE TABLE tombola_ticket_a (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    rev BIGINT(20) REFERENCES revinfo (rev),
    revtype TINYINT(4),
    win BIGINT(20) DEFAULT NULL,
    win_m BIT(1),
    created_date BIGINT(20) DEFAULT NULL,
    created_date_m BIT(1) DEFAULT NULL,
    modified_date BIGINT(20) DEFAULT NULL,
    modified_date_m BIT(1) DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_by_m BIT(1) DEFAULT NULL,
    modified_by VARCHAR(255) DEFAULT NULL,
    modified_by_m BIT(1) DEFAULT NULL,
    PRIMARY KEY (id,rev),
    CONSTRAINT tombola_ticket_a_rev FOREIGN KEY (rev) REFERENCES revinfo (rev)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- TABLE tombola_prize_ticket  -----

CREATE TABLE tombola_prize_ticket (
    prize_id BIGINT(20) NOT NULL,
    ticket_id BIGINT(20) NOT NULL,
    PRIMARY KEY (prize_id, ticket_id),
    KEY tombola_prize_ticket_prize_id (prize_id),
    UNIQUE KEY tombola_prize_ticket_id_uk (ticket_id),
    CONSTRAINT tombola_prize_ticket_prize_id FOREIGN KEY (prize_id) REFERENCES tombola_prize (id),
    CONSTRAINT tombola_prize_ticket_id FOREIGN KEY (ticket_id) REFERENCES tombola_ticket (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- TABLE tombola_role -----

CREATE TABLE tombola_role (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  role_name VARCHAR(255) DEFAULT NULL,
  enabled BIT(1) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY tombola_role_name_uk (role_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- TABLE tombola_user -----

CREATE TABLE tombola_user (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(255) DEFAULT NULL,
  password VARCHAR(255) DEFAULT NULL,
  account_non_expired BIT(1) DEFAULT NULL,
  account_non_locked BIT(1) DEFAULT NULL,
  credentials_non_expired BIT(1) DEFAULT NULL,
  enabled BIT(1) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY tombola_user_name_uk (user_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- TABLE tombola_user_role -----

CREATE TABLE tombola_user_role (
  user_id BIGINT(20) NOT NULL,
  role_id BIGINT(20) NOT NULL,
  PRIMARY KEY (user_id,role_id),
  KEY tombola_user_role_user_id (user_id),
  KEY tombola_user_role_role_id (role_id),
  CONSTRAINT tombola_user_role_user_id FOREIGN KEY (user_id) REFERENCES tombola_user (id),
  CONSTRAINT tombola_user_role_role_id FOREIGN KEY (role_id) REFERENCES tombola_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- VIEW tombola_view -----

CREATE VIEW tombola_view AS SELECT ROW_NUMBER() OVER (ORDER BY a.id, c.id) as id, a.id user_id, a.user_name,
  a.enabled user_enabled, c.id role_id, c.role_name, c.enabled role_enabled FROM tombola_user a
  JOIN tombola_user_role b ON a.id = b.user_id JOIN tombola_role c ON b.role_id = c.id ORDER BY a.id, c.id;

  
-- VIEW tombola_prize_view -----

CREATE VIEW tombola_prize_view AS SELECT
  id, prize_name, cnt, issued,
  IF (created_date = 0, "",
    FROM_UNIXTIME(created_date / 1000, '%Y-%m-%d %H:%i:%s')) created_date,
  created_by,  
  IF (modified_date = 0, "",
    FROM_UNIXTIME(modified_date / 1000, '%Y-%m-%d %H:%i:%s')) modified_date,
  modified_by
  FROM tombola_prize ORDER BY id;
  

-- VIEW tombola_prize_a_view -----

CREATE VIEW tombola_prize_a_view AS SELECT
  IF (revtstmp = 0, "",
    FROM_UNIXTIME(revtstmp / 1000, '%Y-%m-%d %H:%i:%s')) revdate,
  a.id, a.rev, revtype,
  a.prize_name, a.cnt, a.issued,
  IF (created_date = 0, "",
    FROM_UNIXTIME(created_date / 1000, '%Y-%m-%d %H:%i:%s')) created_date,
  created_by,  
  IF (modified_date = 0, "",
    FROM_UNIXTIME(modified_date / 1000, '%Y-%m-%d %H:%i:%s')) modified_date,
  modified_by
  FROM tombola_prize_a a, revinfo r WHERE a.rev = r.rev ORDER BY revtstmp, id, rev;

  
-- VIEW tombola_ticket_view -----

CREATE VIEW tombola_ticket_view AS SELECT
  id, win,
  IF (created_date = 0, "",
    FROM_UNIXTIME(created_date / 1000, '%Y-%m-%d %H:%i:%s')) created_date,
  created_by,  
  IF (modified_date = 0, "",
    FROM_UNIXTIME(modified_date / 1000, '%Y-%m-%d %H:%i:%s')) modified_date,
  modified_by
  FROM tombola_ticket ORDER BY id;

  
-- VIEW tombola_ticket_a_view -----

CREATE VIEW tombola_ticket_a_view AS SELECT
  IF (revtstmp = 0, "",
    FROM_UNIXTIME(revtstmp / 1000, '%Y-%m-%d %H:%i:%s')) revdate,
  a.id, a.rev, revtype,
  win,
  IF (created_date = 0, "",
    FROM_UNIXTIME(created_date / 1000, '%Y-%m-%d %H:%i:%s')) created_date,
  created_by,  
  IF (modified_date = 0, "",
    FROM_UNIXTIME(modified_date / 1000, '%Y-%m-%d %H:%i:%s')) modified_date,
  modified_by
  FROM tombola_ticket_a a, revinfo r WHERE a.rev = r.rev ORDER BY revtstmp, id, rev;
