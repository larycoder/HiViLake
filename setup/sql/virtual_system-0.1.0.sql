-- Hivilake virtual system version 0.1.0 --

-- initialize hivilake --
CREATE DATABASE IF NOT EXISTS hivilake;
USE hivilake;

-- =================================================================================== --
-- initialize system table --
CREATE TABLE IF NOT EXISTS virtual_database(
  id  INT AUTO_INCREMENT PRIMARY KEY,
  `database_name` VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS map_table(
  id           INT AUTO_INCREMENT PRIMARY KEY,
  `database_id` INT NOT NULL,
  `table_name` VARCHAR(100) NOT NULL,
  `table_pos`  VARCHAR(200) NOT NULL,
  CONSTRAINT `fk_map_database`
    FOREIGN KEY (`database_id`) REFERENCES virtual_database(id)
) ENGINE = InnoDB;