# bughunt

Hunts for bugs on Launchpad

## Dependencies
* [Leiningen] (https://github.com/technomancy/leiningen)
* MySQL

## Configure MySQL database

```
$ mysql -u root
mysql> CREATE DATABASE demo;
mysql> GRANT ALL PRIVILEGES ON demo.* TO 'demo'@'localhost' IDENTIFIED BY 'demo';
mysql> exit;

$ mysql -u root demo < resources/create_db.sql
```