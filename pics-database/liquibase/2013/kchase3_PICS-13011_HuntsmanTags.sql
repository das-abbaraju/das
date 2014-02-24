--liquibase formatted sql

--changeset kchase:3

-- Woodlands
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1198, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID = 3544;

-- Geismer
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1201, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID = 3852;

-- Auburn
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1202, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3857;

-- Derry
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1202, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3860;

-- Ringwood
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1202, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3859;

-- Neches
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1204, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3848;

-- Conroe
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1205, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3850;

-- Dayton
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1205, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3849;

-- Freeport
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1206, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3851;

-- Charlotte
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1207, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3855;

-- Lansing
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1207, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3858;

-- LA
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1207, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3854;

-- McIntosh
Insert ignore into contractor_tag
(conID, tagID, createdBy, updatedBy, creationDate, updateDate)
select gc.subID, 1207, 37745, 37745, NOW(), NOW() from generalcontractors gc
where gc.genID =3853;
