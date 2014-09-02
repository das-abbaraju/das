--liquibase formatted SQL

--changeset cfranks:11


-- AES Warrior Run
INSERT INTO `contractor_tag`
(`conID`,
`tagID`,
`createdBy`,
`updatedBy`,
`creationDate`,
`updateDate`)
SELECT
 co.conID
,2114
,135244
,135244
,NOW()
,NOW()
FROM contractor_operator co
WHERE co.opID = 33886;

-- AES Southland
INSERT INTO `contractor_tag`
(`conID`,
`tagID`,
`createdBy`,
`updatedBy`,
`creationDate`,
`updateDate`)
SELECT
 co.conID
,2113
,135244
,135244
,NOW()
,NOW()
FROM contractor_operator co
WHERE co.opID = 6166;

-- AES Shady Point
INSERT INTO `contractor_tag`
(`conID`,
`tagID`,
`createdBy`,
`updatedBy`,
`creationDate`,
`updateDate`)
SELECT
 co.conID
,2115
,135244
,135244
,NOW()
,NOW()
FROM contractor_operator co
WHERE co.opID = 16829;

