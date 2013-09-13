DROP VIEW IF EXISTS	`vwUser_Person`
;

CREATE OR REPLACE VIEW	`vwUser_Person`
(
	User_id
,	User_tp
,	User_nm
,	Domain_nm
,	Password_cd
,	Email_tx
,	Person_id
,	Person_tp
,	Person_nm
,	First_nm
,	Middle_nm
,	Last_nm
,	FirstSNDX_cd
,	LastSNDX_cd
,	Birth_dm
,	Gender_cd
,	User_tx
,	Person_tx
,	UserADD_dm
,	UserADD_nm
,	UserUPD_dm
,	UserUPD_nm
,	UserDEL_dm
,	UserDEL_nm
,	ParentUser_tp
,	UserType_tx
,	PersonADD_dm
,	PersonADD_nm
,	PersonUPD_dm
,	PersonUPD_nm
,	PersonDEL_dm
,	PersonDEL_nm
,	ParentPerson_tp
,	PersonType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwUser_Person
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblUser_Person.User_id
,	tblUser_Person.User_tp
,	vwUser.User_nm
,	vwUser.Domain_nm
,	vwUser.Password_cd
,	vwUser.Email_tx
,	tblUser_Person.Person_id
,	tblUser_Person.Person_tp
,	vwPerson.Person_nm
,	vwPerson.First_nm
,	vwPerson.Middle_nm
,	vwPerson.Last_nm
,	vwPerson.FirstSNDX_cd
,	vwPerson.LastSNDX_cd
,	vwPerson.Birth_dm
,	vwPerson.Gender_cd
,	vwUser.User_tx
,	vwPerson.Person_tx
,	vwUser.UserADD_dm
,	vwUser.UserADD_nm
,	vwUser.UserUPD_dm
,	vwUser.UserUPD_nm
,	vwUser.UserDEL_dm
,	vwUser.UserDEL_nm
,	vwUser.ParentUser_tp
,	vwUser.UserType_tx
,	vwPerson.PersonADD_dm
,	vwPerson.PersonADD_nm
,	vwPerson.PersonUPD_dm
,	vwPerson.PersonUPD_nm
,	vwPerson.PersonDEL_dm
,	vwPerson.PersonDEL_nm
,	vwPerson.ParentPerson_tp
,	vwPerson.PersonType_tx
FROM
	[pics_alpha1].[tblUser_Person]
INNER
JOIN	[pics_alpha1].[vwUser]
ON	tblUser_Person.User_id	= vwUser.User_id		-- FK1
AND	tblUser_Person.User_tp	= vwUser.User_tp
INNER
JOIN	[pics_alpha1].[vwPerson]
ON	tblUser_Person.Person_id	= vwPerson.Person_id		-- FK2
AND	tblUser_Person.Person_tp	= vwPerson.Person_tp

;

