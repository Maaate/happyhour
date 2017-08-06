INSERT INTO `happyhour`.`account` (`id`, `name`, `username`, `password`, `first_name`, `last_name`, `phone`) VALUES ('1', 'Good Drinks Group', 'abc@test.com', 'Michael1', 'Michael', 'Hoffman', '+61123456789');
INSERT INTO `happyhour`.`account` (`id`, `name`, `username`, `password`, `first_name`, `last_name`, `phone`) VALUES ('2', 'So So Beers', 'beers@test.com', 'Michael1', 'Michael', 'Martinez', '123');





INSERT INTO `happyhour`.`pub` (`id`, `name`, `address`, `address_suburb`, `address_state`, `address_country`, `longitude`, `latitude`, `account_id_fk`, `enabled`) VALUES ('1', 'Cheers', '561 George St', 'Sydney', 'NSW', 'Australia', '-33.876635', '151.205939', '1', 'T');
INSERT INTO `happyhour`.`pub` (`id`, `name`, `address`, `address_suburb`, `address_state`, `address_country`, `longitude`, `latitude`, `account_id_fk`, `enabled`) VALUES ('2', 'bar 100', '100 George St', 'Sydney', 'NSW', 'Australia', '-33.85823', '151.209345', '1', 'F');
INSERT INTO `happyhour`.`pub` (`id`, `name`, `address`, `address_suburb`, `address_state`, `address_country`, `longitude`, `latitude`, `account_id_fk`, `enabled`) VALUES ('3', 'world bar', '24 Bayswater Road', 'Kings Cross', 'NSW', 'Australia', '-33.87485', '151.223972', '2', 'T');
INSERT INTO `happyhour`.`pub` (`id`, `name`, `address`, `address_suburb`, `address_state`, `address_country`, `longitude`, `latitude`, `account_id_fk`, `enabled`) VALUES ('4', '3 Monkeys', '555 George St', 'Sydney', 'NSW', 'Australia', '-33.876207', '151.206013', '2', 'T');



INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('1', '1', '1800', '2000', 'Beers $7, Champers $5', '1', '1');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('2', '1', '1900', '2100', 'Beers $6', '3', '1');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('3', '2', '1200', '2000', 'Tacos $3', '1', '1');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('4', '2', '1200', '2000', 'Tacos $3', '1', '2');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('5', '2', '1200', '2000', 'Tacos $3', '1', '3');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('6', '2', '1200', '2000', 'Tacos $3', '1', '4');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('7', '2', '1200', '2000', 'Tacos $3', '1', '5');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('8', '1', '1800', '2000', 'Beers $7, Champers $5', '1', '2');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('9', '1', '1800', '2000', 'Beers $7, Champers $5', '1', '3');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('10', '1', '1800', '2000', 'Beers $7, Champers $5', '1', '4');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('11', '1', '1800', '2000', 'Beers $7, Champers $5', '1', '5');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('12', '1', '1800', '2000', 'Beers $7, Champers $5', '1', '6');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('13', '1', '1900', '2100', 'Beers $6', '3', '2');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('14', '1', '1900', '2100', 'Beers $6', '3', '3');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('15', '1', '1900', '2100', 'Beers $6', '3', '4');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('16', '1', '1900', '2100', 'Beers $6', '3', '5');
INSERT INTO `happyhour`.`promotion` (`id`, `category_id_fk`, `start_time`, `end_time`, `description`, `pub_id_fk`, `day_of_week`) VALUES ('17', '1', '1900', '2100', 'Beers $6', '3', '6');

