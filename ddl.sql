create table 'happyhour'.'categories' (id BIGINT, name TEXT);

CREATE TABLE 'happyhour'.'item_categories' (
  'id' BIGINT(11) NOT NULL AUTO_INCREMENT,
  'name' TEXT NOT NULL,
  PRIMARY KEY ('id'));

CREATE TABLE 'happyhour'.'promotion_service_type' (
  'promotion_id_fk' BIGINT(20) NOT NULL,
  'service_type_id_fk' BIGINT(20) NOT NULL,
  INDEX 'FK_promotion_service_type_promotion_idx' ('promotion_id_fk' ASC),
  INDEX 'FK_promotion_service_type_service_type_idx' ('service_type_id_fk' ASC),
  CONSTRAINT 'FK_promotion_service_type_promotion'
    FOREIGN KEY ('promotion_id_fk')
    REFERENCES 'happyhour'.'promotion' ('id')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT 'FK_promotion_service_type_service_type'
    FOREIGN KEY ('service_type_id_fk')
    REFERENCES 'happyhour'.'service_type' ('id')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE 'happyhour'.'account' (
  'id' char(36) not null primary key,
  'name' VARCHAR(255) NOT NULL,
  'username' VARCHAR(255) NOT NULL,
  'password' VARCHAR(255) NOT NULL,
  'first_name' VARCHAR(255) NOT NULL,
  'last_name' VARCHAR(255) NOT NULL,
  'phone' VARCHAR(45) NOT NULL;


CREATE TABLE 'happyhour'.'pub' (
  'id' BIGINT(11) NOT NULL AUTO_INCREMENT,
  'name' VARCHAR(255) NOT NULL,
  'address' VARCHAR(255) NOT NULL,
  'address_suburb' VARCHAR(255) NOT NULL,
  'address_state' VARCHAR(255) NOT NULL,
  'address_country' VARCHAR(255) NOT NULL,
  'longitude' DECIMAL(10,6) NOT NULL,
  'latitude' DECIMAL(10,6) NOT NULL,
  'account_id_fk' INT(11) NOT NULL,
  'website_url' VARCHAR(255),
  'enabled' CHAR(1) NOT NULL DEFAULT 'T',
  PRIMARY KEY ('id'));


CREATE TABLE 'happyhour'.'promotion' (
  'id' BIGINT(11) NOT NULL AUTO_INCREMENT,
  'category_id_fk' BIGINT(20) NOT NULL,
  'start_time' TIME NOT NULL,
  'end_time' TIME NOT NULL,
  'description' LONGTEXT NOT NULL,
  'pub_id_fk' BIGINT(20) NOT NULL,
  'day_of_week' INT(1) NULL DEFAULT NULL AFTER 'pub_id_fk',
  'date' DATE NULL DEFAULT NULL AFTER 'day_of_week'
  PRIMARY KEY ('id'));

  
CREATE TABLE 'happyhour'.'pub_metric' (
  'id' BIGINT(11) NOT NULL AUTO_INCREMENT,
  'timestamp' DATETIME NOT NULL,
  'pub_id_fk' INT(11) NOT NULL,
  'promotion_id_fk' INT(11) NOT NULL,
  'note' LONGTEXT NOT NULL,
  PRIMARY KEY ('id'));
