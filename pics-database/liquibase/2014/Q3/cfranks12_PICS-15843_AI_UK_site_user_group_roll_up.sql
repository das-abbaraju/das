--liquibase formatted SQL

--changeset cfranks:12

INSERT INTO usergroup
(userId, groupId, creationDate, createdBy, updatedBy, updateDate)
VALUES
(70310 , 111138, NOW(), 135224, 135224,  NOW());

INSERT INTO usergroup
(userId, groupId, creationDate, createdBy, updatedBy, updateDate)
VALUES
(90444  , 111138, NOW(), 135224, 135224,  NOW());