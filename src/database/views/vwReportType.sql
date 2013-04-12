DROP VIEW IF EXISTS	`vwReportType`
;

CREATE OR REPLACE VIEW	`vwReportType`
(
	Report_tp
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
**	Name:		vwReportType
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
	tblReportType.Report_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	[pics_alpha1].[tblReportType]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblReportType.Report_tp	= tblResourceType.Resrc_tp		-- FK1

;

