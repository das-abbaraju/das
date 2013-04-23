DROP VIEW IF EXISTS	`vwReport`
;

CREATE OR REPLACE VIEW	`vwReport`
(
	Report_id
,	Report_tp
,	Report_nm
,	Report_cd
,	Report_tx
,	ReportADD_dm
,	ReportADD_nm
,	ReportUPD_dm
,	ReportUPD_nm
,	ReportDEL_dm
,	ReportDEL_nm
,	ParentReport_tp
,	ReportType_tx
,	ReportTypeLeft_id
,	ReportTypeRight_id
,	ReportTypeLevel_id
,	ReportTypeOrder_id
,	ReportOwner_id
,	ReportIsDeleted_fg
,	ReportParameters_tx
,	ReportFilter_tx
,	ReportIsPrivate_fg
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwReport
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/9/2013
**
**	Modified:	4/9/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	Report.id	AS Report_id
,	Report.modelType	AS Report_tp
,	Report.name	AS Report_nm
,	CAST(Report.id AS CHAR)	AS Report_cd
,	Report.description	AS Resrc_tx
,	Report.creationDate	AS ADD_dm
,	CAST(Report.createdBy AS CHAR)	AS ADD_nm
,	Report.updateDate	AS UPD_dm
,	CAST(Report.updatedBy AS CHAR)	AS UPD_nm
,	NULL	AS DEL_dm
,	NULL	AS DEL_nm
,	'Report'	AS ParentReport_tp
,	''	AS ReportType_tx
,	-1	AS ReportTypeLeft_id
,	-1	AS ReportTypeRight_id
,	-1	AS ReportTypeLevel_id
,	-1	AS ReportTypeOrder_id
,	ownerID	AS ReportOwner_id
,	deleted AS ReportIsDeleted_fg
,	Parameters AS ReportParameters_tx
,	filterExpression	AS ReportFilter_tx
,	private	AS ReportIsPrivate_fg
FROM
	Report
;
