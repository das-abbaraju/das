--liquibase formatted sql

--changeset dabbaraju:4

CREATE TABLE `idp_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idpusername` varchar(200) NOT NULL,
  `userid` int(11) NOT NULL,
  `idp` varchar(200) NOT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` date DEFAULT NULL,
  `updateDate` date DEFAULT NULL,
   PRIMARY KEY (`id`),
  CONSTRAINT `fk1_idp_user` FOREIGN KEY (`userid`) REFERENCES users (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
