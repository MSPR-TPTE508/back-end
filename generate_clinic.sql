-- Generate `clinic` database

-- -----------------------------------------------------
-- Retro-compatibility options
-- -----------------------------------------------------
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema clinic
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `clinic` ;

CREATE SCHEMA IF NOT EXISTS `clinic` DEFAULT CHARACTER SET utf8 ;
SHOW WARNINGS;
USE `clinic` ;

-- -----------------------------------------------------
-- Table `clinic`.`browsers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `clinic`.`browsers` (
  `browser_id` INT NOT NULL,
  `browser_name` VARCHAR(45) NULL,
  PRIMARY KEY (`browser_id`))
ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `clinic`.`hospital_users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `clinic`.`hospital_users` (
  `hospital_user_id` INT NOT NULL,
  `hospital_user_username` VARCHAR(45) NULL,
  `hospital_user_last_ip_address` VARCHAR(16) NULL,
  `browsers_browser_id` INT NOT NULL,
  PRIMARY KEY (`hospital_user_id`, `browsers_browser_id`),
  CONSTRAINT `fk_hospital_users_browsers`
    FOREIGN KEY (`browsers_browser_id`)
    REFERENCES `clinic`.`browsers` (`browser_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Foreign keys
-- -----------------------------------------------------
CREATE INDEX `fk_hospital_users_browsers_idx` ON `clinic`.`hospital_users` (`browsers_browser_id` ASC);

SHOW WARNINGS;

-- -----------------------------------------------------
-- Retro-compatibility options
-- -----------------------------------------------------
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
