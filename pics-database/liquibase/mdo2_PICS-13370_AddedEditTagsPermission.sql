--liquibase formatted sql

--changeset mdo:2
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
INSERT INTO useraccess (userID,accessType,viewFlag,editFlag,deleteFlag,grantFlag,lastUpdate,grantedByID)
SELECT userID,'EditTags',viewFlag,editFlag,deleteFlag,grantFlag,lastUpdate,grantedByID
FROM useraccess
WHERE accessType = 'ContractorTags'
