DROP PROCEDURE IF EXISTS	`gfpHSECompetenciesReport`
;

DELIMITER //
CREATE PROCEDURE	gfpHSECompetenciesReport()
BEGIN
/*
**	Name:		gfpHSECompetenciesReport
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	07-MAY-2013
**	Modnumber:	00
**	Modification:	Original
	Use:	CALL	gfpHSECompetenciesReport();
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpHSECompetenciesReport';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Primary Key lookup
	#######################################################################
	DROP TEMPORARY TABLE IF EXISTS Set1
	;
	CREATE TEMPORARY TABLE IF NOT EXISTS Set1
	SELECT
		sub.id accountID
	,	sub.name
	,	employee.id employeeID
	,	employee.firstName
	,	employee.lastName
	,	employee.title
	,	CONCAT('EmployeeClassification.', employee.classification, '.description') classification
	,	employee.hireDate
	,	employee.email
	,	employee.phone
	,	employee.twicExpiration
	-- ,	ISNULL(gcw.subID) notWorksFor
	,	required.name roles
	,	IFNULL(skilled.counts, 0) skilled
	,	IFNULL(required.counts, 0) required
	,	ROUND((IFNULL(skilled.counts, 0) / IFNULL(required.counts, 1)) * 100) percent
	,	sub.status
	,	sub.requiresCompetencyReview
	FROM
		employee
	JOIN
		accounts	sub
	ON	sub.id	= employee.accountID
	AND 	sub.status	= 'Active'
	AND	sub.requiresCompetencyReview	= 1 
	LEFT JOIN 
		vwHSECompetenciesRequiredCount	required 
	ON	required.employeeID	= employee.id
	LEFT JOIN 
		vwHSECompetenciesSkilledCount	skilled 
	ON	skilled.employeeID	= employee.id
	;
	#######################################################################
	SELECT
		Set1.accountID
	,	Set1.name
	,	Set1.employeeID
	,	Set1.firstName
	,	Set1.lastName
	,	Set1.title
	,	Set1.classification
	,	Set1.hireDate
	,	Set1.email
	,	Set1.phone
	,	Set1.twicExpiration
	,	ISNULL(gcw.subID) notWorksFor
	,	Set1.roles
	,	Set1.skilled
	,	Set1.required
	,	Set1.percent
	FROM
		Set1
	JOIN
		generalcontractors
	ON	generalcontractors.subID	= Set1.accountID
	JOIN
		facilities
	ON	generalcontractors.genID	= facilities.opID
	AND 	facilities.corporateID > 0
	AND	facilities.corporateID	NOT IN (4,5,6,7,8,9,10,11) 
	AND	facilities.corporateID	IN (1336,17248,4,5,1488,8,33394)
	AND	Set1.accountID	= generalcontractors.subID
	JOIN
		accounts	gen
	ON	gen.id	= generalcontractors.genID
	AND 	gen.status	= 'Active'
	LEFT JOIN
		generalcontractors	gcw
	ON	gcw.subID	= Set1.accountID
	AND 	gcw.genID	= 1202
	GROUP BY
		Set1.employeeID
	;
	#######################################################################
END	GFP
;
###############################################################################
END
//
DELIMITER ;
;

