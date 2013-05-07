DROP VIEW IF EXISTS	`vwRole_Report_RightType`
;

CREATE OR REPLACE VIEW	`vwRole_Report_RightType`
(
	ReportPermissionAccount_id
,	Role_id
,	Role_tp
,	Role_nm
,	Role_cd
,	Role_tx

,	Report_id
,	Report_tp
,	Report_nm
,	Report_cd
,	Report_tx

,	ReportOwner_id
,	ReportIsDeleted_fg
,	ReportParameters_tx
,	ReportFilter_tx
,	ReportIsPrivate_fg

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
**	Name:		vwRole_Report_RightType
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
	Report_Permission_Account.id 	AS ReportPermissionAccount_id
,	Accounts.ID	AS Role_id
,	Accounts.type	AS Role_tp
,	Accounts.name	AS Role_nm
,	CAST(Accounts.id AS CHAR)	AS Role_cd
,	Accounts.description	AS Role_tx

,	vwReport.Report_id	AS Report_id
,	vwReport.Report_tp	AS Report_tp
,	vwReport.Report_nm	AS Report_nm
,	vwReport.Report_cd	AS Report_cd
,	vwReport.Report_tx	AS Report_tx

,	vwReport.ReportOwner_id
,	vwReport.ReportIsDeleted_fg
,	vwReport.ReportParameters_tx
,	vwReport.ReportFilter_tx
,	vwReport.ReportIsPrivate_fg


-- ,	Report_Permission_Account.Right_tp

,	vwReport.ReportADD_dm
,	vwReport.ReportADD_nm
,	vwReport.ReportUPD_dm
,	vwReport.ReportUPD_nm
,	vwReport.ReportDEL_dm
,	vwReport.ReportDEL_nm
,	vwReport.ParentReport_tp
,	vwReport.ReportType_tx
,	vwReport.ReportTypeLeft_id
,	vwReport.ReportTypeRight_id
,	vwReport.ReportTypeLevel_id
,	vwReport.ReportTypeOrder_id
FROM
	Report_Permission_Account
JOIN	
	vwReport
ON	vwReport.Report_id	= Report_Permission_Account.reportID

JOIN
	Accounts
ON	Accounts.id	= Report_Permission_Account.accountID
;

/*
select *
from vwRole_Report_RightType
;
*/
