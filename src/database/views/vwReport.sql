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
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwReport
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
	tblReport.Report_id
,	tblReport.Report_tp
,	tblReport.Report_nm
,	tblReport.Report_cd
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwReportType.ParentReport_tp
,	vwReportType.ReportType_tx
,	vwReportType.ReportTypeLeft_id
,	vwReportType.ReportTypeRight_id
,	vwReportType.ReportTypeLevel_id
,	vwReportType.ReportTypeOrder_id
FROM
	[pics_alpha1].[tblReport]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblReport.Report_id	= vwResource.Resrc_id		-- FK1
AND	tblReport.Report_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwReportType]
ON	tblReport.Report_tp	= vwReportType.Report_tp		-- FK2

;

