
CREATE DATABASE IF NOT EXISTS test_franchise;

USE test_franchise;

CREATE USER IF NOT EXISTS 'test_franchise'@'%' IDENTIFIED BY 'p4ssDB1234';

GRANT ALL PRIVILEGES ON test_franchise.* TO 'test_franchise'@'%';

FLUSH PRIVILEGES;
ALTER DATABASE test_franchise CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; 