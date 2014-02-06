--liquibase formatted sql

--sql:
--changeset dalvarado:DROP1 splitStatements:true endDelimiter:|
DROP VIEW IF EXISTS	vw_contractor_logos
;
--sql:
--changeset dalvarado:DROP2 splitStatements:true endDelimiter:|
DROP VIEW IF EXISTS	vw_contractor_logos_counts
;
--sql:
--changeset dalvarado:DROP3 splitStatements:true endDelimiter:|
DROP VIEW IF EXISTS	vw_single_operator_contractors
;
--sql:
--changeset dalvarado:CREATE1 splitStatements:true endDelimiter:|
CREATE OR REPLACE DEFINER =`pics_admin`@`%` VIEW	vw_single_operator_contractors
AS
(
	SELECT
		con.id		con_id
	,	con.name	con_name
	,	logo.id		logo_account_id
	,	logo.name	logo_account_name
	,	COUNT(1)	num_logos
	FROM
		accounts	con
	JOIN
		contractor_operator
	ON	con.id	= contractor_operator.conID
	JOIN
		operators
	ON	contractor_operator.opID	= operators.id
	JOIN
		accounts	logo
	ON	operators.reportingID	= logo.id
	WHERE	TRUE
	AND	operators.inPicsConsortium	= 0
	GROUP BY
		con.id
	HAVING
		num_logos	= 1

	ORDER BY
		con.name
	,	logo.id
)
;

