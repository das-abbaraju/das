DROP VIEW IF EXISTS	`vwPerson`
;

CREATE OR REPLACE VIEW	`vwPerson`
(
	Person_id
,	Person_tp
,	Person_nm
,	First_nm
,	Middle_nm
,	Last_nm
,	FirstSNDX_cd
,	LastSNDX_cd
,	Birth_dm
,	Gender_cd
,	Person_tx
,	PersonADD_dm
,	PersonADD_nm
,	PersonUPD_dm
,	PersonUPD_nm
,	PersonDEL_dm
,	PersonDEL_nm
,	ParentPerson_tp
,	PersonType_tx
,	PersonTypeLeft_id
,	PersonTypeRight_id
,	PersonTypeLevel_id
,	PersonTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwPerson
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
	tblPerson.Person_id
,	tblPerson.Person_tp
,	tblPerson.Person_nm
,	tblPerson.First_nm
,	tblPerson.Middle_nm
,	tblPerson.Last_nm
,	tblPerson.FirstSNDX_cd
,	tblPerson.LastSNDX_cd
,	tblPerson.Birth_dm
,	tblPerson.Gender_cd
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwPersonType.ParentPerson_tp
,	vwPersonType.PersonType_tx
,	vwPersonType.PersonTypeLeft_id
,	vwPersonType.PersonTypeRight_id
,	vwPersonType.PersonTypeLevel_id
,	vwPersonType.PersonTypeOrder_id
FROM
	[pics_alpha1].[tblPerson]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblPerson.Person_id	= vwResource.Resrc_id		-- FK1
AND	tblPerson.Person_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwPersonType]
ON	tblPerson.Person_tp	= vwPersonType.Person_tp		-- FK2

;

