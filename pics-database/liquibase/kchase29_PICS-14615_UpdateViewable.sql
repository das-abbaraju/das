--liquibase formatted sql

--changeset kchase:29

CREATE TEMPORARY TABLE myNotes
AS
select n.id as id, u.accountID as act from note n
join users u on u.id=n.`createdBy`
join accounts a on a.id=u.accountID
where n.`viewableBy` = 1
and u.`accountID`  != 1100
and n.summary like 'Added%manually'
;

update note as n
join myNotes as m on n.id=m.id
set n.viewableBy=m.act
;