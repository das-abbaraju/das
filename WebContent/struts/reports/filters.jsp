<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />
<script type="text/javascript">
function download(url) {
	newurl = url + "CSV.action?" + $('form1').serialize();
	popupWin = window.open(newurl, url, '');
}
</script>

<div id="search">
<s:if test="allowCollapsed">
	<div id="showSearch" onclick="showSearch()"
		<s:if test="filtered">style="display: none"</s:if>
		><a href="#">Show Filter Options</a></div>
	<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if>><a
		href="#" onclick="hideSearch()">Hide Filter Options</a></div>
</s:if>
<s:if test="filter.ajax">
<s:property value="filter.destinationAction" />
</s:if>
<a href="#" onclick="runSearchAjax('form1','<s:property value="filter.destinationAction"/>'); return false;">Test Me</a>
<s:form id="form1" method="post" action="%{filter.destinationAction}"
	cssStyle="background-color: #F4F4F4;"
	onsubmit="runSearch( 'form1')">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div style="text-align: center; width: 100%">
	<div class="buttons">
		<button class="positive" type="submit" name="button" value="Search">Search</button>
	</div>
	</div>
	<br clear="all" />

	<s:if test="filter.showAccountName">
		<div class="filterOption"><s:textfield name="filter.accountName"
			cssClass="forms" size="10" onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showVisible">
		<div class="filterOption"><s:select list="filter.visibleOptions"
			cssClass="forms" name="filter.visible" /></div>
	</s:if>

	<s:if test="filter.showAddress">
		<div class="filterOption">Address: <s:textfield name="filter.city"
			cssClass="forms" size="15" onfocus="clearText(this)" /> <s:select
			list="filter.stateList" cssClass="forms" name="filter.state" /> <s:textfield
			name="filter.zip" cssClass="forms" size="5" onfocus="clearText(this)" /></div>
	</s:if>
	<s:if test="filter.showTaxID">
		<div class="filterOption"><s:textfield name="filter.taxID"
			cssClass="forms" size="9" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>
	<br /><br />
	<s:if test="filter.showIndustry">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_industry'); return false;">Industry</a> = <span
			id="form1_industry_query">ALL</span><br />
		<span id="form1_industry_select" style="display: none"
			class="clearLink"> <s:select id="form1_industry" name="filter.industry"
			list="filter.industryList" cssClass="forms" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_industry');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_industry'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showTrade">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_trade'); return false;">Trade</a> =
		<span id="form1_trade_query">ALL</span>
		<s:select
			list="filter.tradePerformedByList" cssClass="forms" name="filter.performedBy" />
			<br />
		<span id="form1_trade_select" style="display: none" class="clearLink">
		<s:select id="form1_trade" list="filter.tradeList" listKey="questionID" listValue="question" cssClass="forms" name="filter.trade"
			multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_trade');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_trade'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showFlagStatus">
		<div class="filterOption"><s:select list="filter.flagStatusList"
			cssClass="forms" name="filter.flagStatus" /></div>
	</s:if>

	<s:if test="filter.showWaitingOn">
		<div class="filterOption"><s:select list="filter.waitingOnList" headerKey="0" headerValue="- Waiting On-"
			cssClass="forms" name="filter.waitingOn" /></div>
	</s:if>

	<s:if test="filter.showAuditType">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditTypeID'); return false;">Audit
		Type</a> = <span id="form1_auditTypeID_query">ALL</span><br />
		<span id="form1_auditTypeID_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditTypeID" list="filter.auditTypeList"
			cssClass="forms" name="filter.auditTypeID" listKey="auditTypeID"
			listValue="auditName" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_auditTypeID');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAuditStatus">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditStatus'); return false;">Audit
		Status</a> = <span id="form1_auditStatus_query">ALL</span><br />
		<span id="form1_auditStatus_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditStatus" list="filter.auditStatusList"
			cssClass="forms" name="filter.auditStatus" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_auditStatus');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditStatus'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAuditor">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditorId'); return false;">Auditors</a> = <span
			id="form1_auditorId_query">ALL</span><br />
		<span id="form1_auditorId_select" style="display: none" class="clearLink">
		<s:select name="filter.auditorId" cssClass="forms" list="auditorList" listKey="id" listValue="name"
        multiple="true" size="5" id="form1_auditorId" />
		<script type="text/javascript">updateQuery('form1_auditorId');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditorId'); return false;">Clear</a></span></div>
	</s:if>

	<s:if test="filter.showConAuditor">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_conAuditorId'); return false;">CSR</a> = <span
			id="form1_conAuditorId_query">ALL</span><br />
		<span id="form1_conAuditorId_select" style="display: none" class="clearLink">
		<s:select name="filter.conAuditorId" cssClass="forms" list="auditorList" listKey="id" listValue="name"
        multiple="true" size="5" id="form1_conAuditorId" />
		<script type="text/javascript">updateQuery('form1_conAuditorId');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_conAuditorId'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showOperator">
		<div class="filterOption">
		<s:if test="filterOperatorSingle">
			<s:select list="filter.operatorList" cssClass="forms" name="filter.operator" listKey="id" listValue="name"/>
		</s:if>
		<s:else>
		<a href="#"
			onclick="toggleBox('form1_operator'); return false;">Operators</a> =
		<span id="form1_operator_query">ALL</span><br />
		<span id="form1_operator_select" style="display: none"
			class="clearLink"> <s:select id="form1_operator" list="filter.operatorList"
			cssClass="forms" name="filter.operator" listKey="id" listValue="name"
			multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_operator');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_operator'); return false;">Clear</a> </span>
		</s:else>	
		</div>
	</s:if>

	<s:if test="filter.showCerts">
		<div class="filterOption"><s:select list="filter.certsOptions"
			cssClass="forms" name="filter.certsOnly" /></div>
	</s:if>
	
	<s:if test="filter.showRiskLevel">
		<div class="filterOption"><s:select list="#{'1':'Low', '2':'Med', '3':'High'}"  
			headerKey="0" headerValue="- Risk Level -" cssClass="forms" name="filter.riskLevel"/></div>
	</s:if>
	
	<br clear="all"/>
	<s:if test="filter.showLicensedIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_stateLicensedIn'); return false;">Licensed
		In</a> = <span id="form1_stateLicensedIn_query">ALL</span><br />
		<span id="form1_stateLicensedIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_stateLicensedIn" list="filter.stateLicensesList"
			cssClass="forms" name="filter.stateLicensedIn" listKey="questionID" listValue="question" multiple="true" size="5" />
		<script type="text/javascript">updateQuery('form1_stateLicensedIn');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_stateLicensedIn'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showWorksIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_worksIn'); return false;">Works In</a> = <span
			id="form1_worksIn_query">ALL</span><br />
		<span id="form1_worksIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_worksIn" list="filter.worksInList"
			cssClass="forms" name="filter.worksIn" listKey="questionID" listValue="question" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_worksIn');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_worksIn'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showOfficeIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_officeIn'); return false;">Office In</a> =
		<span id="form1_officeIn_query">ALL</span><br />
		<span id="form1_officeIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_worksIn" list="filter.officeInList"
			cssClass="forms" name="filter.officeIn" listKey="questionID" listValue="question" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_officeIn');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_officeIn'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showConLicense">
		<br clear="all" />
		<div class="filterOption">
		<s:select name="filter.validLicense" list="#{'Valid':'Valid','UnValid':'UnValid','All':'All'}" cssClass="forms"/>
		</div>
	</s:if>

	<s:if test="filter.showPercentComplete">
		<br clear="all"/>
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_percentComplete'); return false;">Percent Complete</a> 
			<span id="form1_percentComplete_query">= ALL</span><br /> 
			<span id="form1_percentComplete" style="display: none" class="clearLink"><s:textfield name="filter.percentComplete1"
			id="form1_percentComplete1" cssClass="forms" size="12" onfocus="clearText(this)" /> To: 
			<s:textfield name="filter.percentComplete2" id="form1_percentComplete2"
			cssClass="forms" size="12" onfocus="clearText(this)" />
			<script
			type="text/javascript">textQuery('form1_percentComplete'); </script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_percentComplete'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showCreatedDate">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_createdDate'); return false;">Created Date</a>  
			<span id="form1_createdDate_query">= ALL</span><br /> 
			<span id="form1_createdDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="6" 
			id="form1_createdDate1" name="filter.createdDate1" />
			<a id="anchor_createdDate1" name="anchor_createdDate1"
			onclick="cal2.select($('form1_createdDate1'),'anchor_createdDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="6" 
			id="form1_createdDate2" name="filter.createdDate2" />
			<a id="anchor_createdDate2" name="anchor_createdDate2"
			onclick="cal2.select($('form1_createdDate2'),'anchor_createdDate2','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			<script
			type="text/javascript">textQuery('form1_createdDate');</script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_createdDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showCompletedDate">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_completedDate'); return false;">Completed Date</a> 
			<span id="form1_completedDate_query">= ALL</span><br /> 
			<span id="form1_completedDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="6" 
			id="form1_completedDate1" name="filter.completedDate1" />
			<a id="anchor_completedDate1" name="anchor_completedDate1"
			onclick="cal2.select($('form1_completedDate1'),'anchor_completedDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="6" 
			id="form1_completedDate2" name="filter.completedDate2" />
			<a id="anchor_completedDate2" name="anchor_completedDate2"
			onclick="cal2.select($('form1_completedDate2'),'anchor_completedDate2','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			<script
			type="text/javascript">textQuery('form1_completedDate');</script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_completedDate'); return false;">Clear</a></span>
		</div>
	</s:if>
	
	<s:if test="filter.showClosedDate">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_closedDate'); return false;">Closed Date</a> 
			<span id="form1_closedDate_query">= ALL</span><br /> 
			<span id="form1_closedDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="6" 
			id="form1_closedDate1" name="filter.closedDate1" />
			<a id="anchor_closedDate1" name="anchor_closedDate1"
			onclick="cal2.select($('form1_closedDate1'),'anchor_closedDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="6" 
			id="form1_closedDate2" name="filter.closedDate2" />
			<a id="anchor_closedDate2" name="anchor_closedDate2"
			onclick="cal2.select($('form1_closedDate2'),'anchor_closedDate2','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			<script
			type="text/javascript">textQuery('form1_closedDate');</script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_closedDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showExpiredDate">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_expiredDate'); return false;">Expired Date</a> 
			<span id="form1_expiredDate_query">= ALL</span><br /> 
			<span id="form1_expiredDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="6" 
			id="form1_expiredDate1" name="filter.expiredDate1" />
			<a id="anchor_expiredDate1" name="anchor_expiredDate1"
			onclick="cal2.select($('form1_expiredDate1'),'anchor_expiredDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="6" 
			id="form1_expiredDate2" name="filter.expiredDate2" />
			<a id="anchor_expiredDate2" name="anchor_expiredDate2"
			onclick="cal2.select($('form1_expiredDate2'),'anchor_expiredDate2','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			<script
			type="text/javascript">textQuery('form1_expiredDate');</script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_expiredDate'); return false;">Clear</a></span>
		</div>
	</s:if>
	
	<s:if test="filter.showOshaEmr">
		<br clear="all" />
		<div class="filterOption">
		<p class="blueMain">Show Contractors that need:</p>
		<table border="0" cellpadding="2" cellspacing="0" class="blueMain">
			<tr>
				<td></td>
				<td>2007</td>
				<td>2006</td>
				<td>2005</td>
			</tr>
			<tr>
				<td>OSHA</td>
				<td><s:checkbox name="filter.osha1" /></td>
				<td><s:checkbox name="filter.osha2" /></td>
				<td><s:checkbox name="filter.osha3" /></td>
			</tr>
			<tr>
				<td>EMR</td>
				<td><s:checkbox name="filter.emr07" /></td>
				<td><s:checkbox name="filter.emr06" /></td>
				<td><s:checkbox name="filter.emr05" /></td>
			</tr>
		</table>
		</div>
	</s:if>

	<s:if test="filter.showUnConfirmedAudits">
		<br clear="all" />
		<div class="filterOption">
		<label><s:checkbox name="filter.unScheduledAudits" />
		Check to Search on UnConfirmed Audits</label>
		</div>
	</s:if>

	<s:if test="filter.showAssignedCon">
		<br clear="all" />
		<div class="filterOption">
		<label><s:checkbox name="filter.assignedCon" />
		Check to Search on Assigned Contractors</label>
		</div>
	</s:if>
	
	<s:if test="filter.showExpiredLicense">
		<br clear="all" />
		<div class="filterOption">
		<label><s:checkbox name="filter.conExpiredLic" />
		Check to Search on Expired Licenses for Contractors</label>
		</div>
	</s:if>
	
	<s:if test="filter.showInParentCorporation">
		<br clear="all" />
		<div class="filterOption">
		<label><s:checkbox name="filter.inParentCorporation" />Check to limit to contractors already working within my parent corporation</label>
		</div>
	</s:if>
	
	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>
<div id="caldiv2" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>
