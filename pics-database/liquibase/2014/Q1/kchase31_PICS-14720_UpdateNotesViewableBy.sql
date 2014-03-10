--liquibase formatted sql

--changeset kchase:31

-- update affected notes (about 350)
update note as n
join users as u on u.id=n.createdBy
join accounts as a on a.id=u.accountID
set n.viewableBy=a.id
where n.noteCategory='Flags'
and n.viewableBy=8
and n.summary like 'Forced%'
;
