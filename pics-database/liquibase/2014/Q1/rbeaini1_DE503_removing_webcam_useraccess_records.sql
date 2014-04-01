--liquibase formatted sql

--changeset rbeaini:1
DELETE FROM useraccess WHERE accessType IN ('ManageWebcam', 'WebcamNotification');