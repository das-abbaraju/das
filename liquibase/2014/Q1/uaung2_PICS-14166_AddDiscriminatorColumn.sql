--liquibase formatted sql

--changeset uaung:2

ALTER TABLE `account_group`
ADD COLUMN `type` VARCHAR(32) NOT NULL DEFAULT 'Group' COMMENT 'Discriminator column differentiating between contractor groups, operator job roles and operator site assignments' AFTER `description`;

