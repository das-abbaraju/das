DROP VIEW IF EXISTS	`vwapp_language`
;

CREATE OR REPLACE VIEW	`vwapp_language`
(
	Locale_cd
,	Language_cd
,	Country_cd
,	Status_nm
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwapp_language
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	app_language.locale
,	app_language.language
,	app_language.country
,	app_language.status
FROM
	app_language

;

