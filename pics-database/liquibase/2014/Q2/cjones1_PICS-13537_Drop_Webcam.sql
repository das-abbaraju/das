--liquibase formatted sql

--changeset cjones:1

-- PICS-13537 (See also US706 https://rally1.rallydev.com/#/16360690749d/detail/userstory/17876136878)

DROP TABLE IF EXISTS `webcam`;