--
-- TombolaApplication 3.14.15 Flyway script 
--
  
----- TABLE tombola_role -----
  
INSERT INTO tombola_role (id, role_name, enabled) VALUES (1, 'adminRole', true);
INSERT INTO tombola_role (id, role_name, enabled) VALUES (2, 'managerRole', true);
INSERT INTO tombola_role (id, role_name, enabled) VALUES (3, 'userRole', true);


----- TABLE tombola_user -----
  
INSERT INTO tombola_user
  (id, user_name, password, account_non_expired, account_non_locked, credentials_non_expired, enabled)
    VALUES (1, 'admin',
    '243261243034246a77614b5352774e75766e4433596734426849714b2e5856724b5a594c37494543316a5765374e74757867454369554a35726c4e65',
    true, true, true, true);
INSERT INTO tombola_user
  (id, user_name, password, account_non_expired, account_non_locked, credentials_non_expired, enabled)
    VALUES (2, 'lhs', 
    '24326124303424655569755352476f2e30737873636f4a3832515878756875656d573634436a7054674f784d5656654b3379586c4f584f6c32684447',
    true, true, true, true);
INSERT INTO tombola_user
  (id, user_name, password, account_non_expired, account_non_locked, credentials_non_expired, enabled)
    VALUES (3, 'user',
    '243261243034244358565237544c4f3853754a6f695552384d386f572e5259693672486c6b6b4b36724163542e624c4b44716878365a735a306a6d75',
    true, true, true, true);

    
----- TABLE tombola_user_role -----
  
    
-- old
-- 
-- Simple INSERT for H2, Postgresql, MariaDB (MySQL) and others databases
-- 
-- INSERT INTO tombola_user_role (user_id, role_id) VALUES (1, 1);
-- INSERT INTO tombola_user_role (user_id, role_id) VALUES (1, 2);
-- INSERT INTO tombola_user_role (user_id, role_id) VALUES (1, 3);
-- INSERT INTO tombola_user_role (user_id, role_id) VALUES (2, 2);
-- INSERT INTO tombola_user_role (user_id, role_id) VALUES (2, 3);
-- INSERT INTO tombola_user_role (user_id, role_id) VALUES (3, 3);

    
-- For H2, Postgresql, MariaDB (MySQL)

INSERT INTO tombola_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM tombola_role
    WHERE role_name='adminRole') role_id
    FROM tombola_user WHERE user_name='admin');
INSERT INTO tombola_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM tombola_role
    WHERE role_name='managerRole') role_id
    FROM tombola_user WHERE user_name='admin');
INSERT INTO tombola_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM tombola_role
    WHERE role_name='userRole') role_id
    FROM tombola_user WHERE user_name='admin');
INSERT INTO tombola_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM tombola_role
    WHERE role_name='managerRole') role_id
    FROM tombola_user WHERE user_name='lhs');
INSERT INTO tombola_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM tombola_role
    WHERE role_name='userRole') role_id
    FROM tombola_user WHERE user_name='lhs');
INSERT INTO tombola_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM tombola_role
    WHERE role_name='userRole') role_id
    FROM tombola_user WHERE user_name='user');

    
/*
----- TABLE tombola_prize -----

INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (1, 'Kanec', 1, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (2, 'Zajíc', 3, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (3, 'Zájezd', 4, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (4, 'Telefon', 4, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (5, 'Outdoorové hodinky', 8, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (6, 'Porcelánový servis', 5, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (7, 'Květinový koš', 6, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (8, 'Pytel brambor', 7, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (9, 'Pytel cibule', 7, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (10, 'Hasící přístroj', 7, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (11, 'Váza', 8, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (12, 'Sauna', 9, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (13, 'Kuchyňské hodiny', 10, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (14, 'Pláštěnka', 11, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (15, 'Kniha', 12, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (16, 'Sportovní prádlo', 15, 0, 0, 0, 'admin', '');
INSERT INTO tombola_prize (id, prize_name, cnt, issued, created_date, modified_date, created_by, modified_by)
    VALUES (17, 'Kalendář', 15, 0, 0, 0, 'admin', '');
*/
