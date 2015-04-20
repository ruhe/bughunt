CREATE DATABASE demo;
GRANT ALL PRIVILEGES ON demo.* TO 'demo'@'localhost' IDENTIFIED BY 'demo';

use demo;

CREATE TABLE bugs (
       bug_id int(11),
       title varchar(320),
       owner varchar(80),
       importance varchar(20),
       status varchar(20),
       target varchar(20),
       milestone varchar(30),
       assignee varchar(20),
       date_assigned datetime,
       date_closed datetime,
       date_confirmed datetime,
       date_created datetime,
       date_fix_committed datetime,
       date_fix_released datetime,
       date_in_progress datetime,
       date_incomplete datetime,
       date_left_closed datetime,
       date_left_new datetime,
       date_triaged datetime
);
