<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />
<div id="search">
<s:if test="allowCollapsed">
	<div id="showSearch" onclick="showSearch()"
		<s:if test="filtered">style="display: none"</s:if>
		><a href="#">Show Filter Options</a></div>
	<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if>><a
		href="#" onclick="hideSearch()">Hide Filter Options</a></div>
</s:if>
<s:form id="form1" action="%{filter.destinationAction}"
	cssStyle="background-color: #F4F4F4;">
	
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div class="buttons">
		<s:if test="filter.allowMailMerge" >
			<button type="submit" id="write_email_button" name="button" value="Write Email" onclick="clickSearchSubmit('form1')" class="positive" style="display: none">Write Email</button>
			<button type="button" name="button" value="Find Recipients" onclick="clickSearch('form1')">Find Recipients</button>
		</s:if>
		<s:else>
			<button type="submit" name="button" value="Search" onclick="return clickSearch('form1');" class="positive">Search</button>
			<br clear="all" />
		</s:else>
	</div>

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
		<s:select id="form1_trade" list="filter.tradeList" listKey="id" listValue="question" cssClass="forms" name="filter.trade"
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
		<div class="filterOption"><s:select list="filter.waitingOnList" headerKey="" headerValue="- Waiting On-"
			cssClass="forms" name="filter.waitingOn" /></div>
	</s:if>

	<s:if test="filter.showCcOnFile">
		<div class="filterOption"><s:select list="#{'1':'Yes','0':'No'}" headerKey="2" headerValue="- Credit Card -"
			cssClass="forms" name="filter.ccOnFile" /></div>
	</s:if>

	<s:if test="filter.showAuditType">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_pqfAuditTypeID'); return false;">PQF
		Type</a> = <span id="form1_pqfAuditTypeID_query">ALL</span><br />
		<span id="form1_pqfAuditTypeID_select" style="display: none"
			class="clearLink"> <s:select id="form1_pqfAuditTypeID" list="filter.pQFTypeList"
			cssClass="forms" name="filter.auditTypeID" listKey="id"
			listValue="auditName" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_pqfAuditTypeID');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_pqfAuditTypeID'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAuditType">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditTypeID'); return false;">Audit
		Type</a> = <span id="form1_auditTypeID_query">ALL</span><br />
		<span id="form1_auditTypeID_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditTypeID" list="filter.auditTypeList"
			cssClass="forms" name="filter.auditTypeID" listKey="id"
			listValue="auditName" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_auditTypeID');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showPolicyType">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditTypeID'); return false;">Policy
		Type</a> = <span id="form1_auditTypeID_query">ALL</span><br />
		<span id="form1_auditTypeID_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditTypeID" list="filter.policyTypeList"
			cssClass="forms" name="filter.auditTypeID" listKey="id"
			listValue="auditName" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_auditTypeID');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAuditStatus">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditStatus'); return false;">Status</a> = <span id="form1_auditStatus_query">ALL</span><br />
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

	<s:if test="filter.showRiskLevel">
		<div class="filterOption"><s:select list="#{'1':'Low', '2':'Med', '3':'High'}"  
			headerKey="0" headerValue="- Risk Level -" cssClass="forms" name="filter.riskLevel"/></div>
	</s:if>

	<br clear="all"/>

	<s:if test="filter.showOpertorTagName && filter.operatorTagNamesList.size() > 0">
		<div class="filterOption">
			<s:select list="filter.operatorTagNamesList" cssClass="forms" name="filter.operatorTagName" listKey="id" listValue="tag" headerKey="0" headerValue="- Operator Tag -"/>
		</div>	
	</s:if>

	<s:if test="filter.showLicensedIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_stateLicensedIn'); return false;">Licensed
		In</a> = <span id="form1_stateLicensedIn_query">ALL</span><br />
		<span id="form1_stateLicensedIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_stateLicensedIn" list="filter.stateLicensesList"
			cssClass="forms" name="filter.stateLicensedIn" listKey="id" listValue="question" multiple="true" size="5" />
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
			cssClass="forms" name="filter.worksIn" listKey="id" listValue="question" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_worksIn');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_worksIn'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showOfficeIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_officeIn'); return false;">Office In</a> =
		<span id="form1_officeIn_query">ALL</span><br />
		<span id="form1_officeIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_officeIn" list="filter.officeInList"
			cssClass="forms" name="filter.officeIn" listKey="id" listValue="question" multiple="true" size="5" /> <script
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

	<s:if test="filter.showCreatedDate">
		<br clear="all"/>
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_createdDate'); return false;">Created Date</a>  
			<span id="form1_createdDate_query">= ALL</span><br /> 
			<span id="form1_createdDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="8" 
			id="form1_createdDate1" name="filter.createdDate1" />
			<a id="anchor_createdDate1" name="anchor_createdDate1"
			onclick="cal2.select($('form1_createdDate1'),'anchor_createdDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="8" 
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
			<span id="form1_completedDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="8" 
			id="form1_completedDate1" name="filter.completedDate1" />
			<a id="anchor_completedDate1" name="anchor_completedDate1"
			onclick="cal2.select($('form1_completedDate1'),'anchor_completedDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="8" 
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
			<span id="form1_closedDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="8" 
			id="form1_closedDate1" name="filter.closedDate1" />
			<a id="anchor_closedDate1" name="anchor_closedDate1"
			onclick="cal2.select($('form1_closedDate1'),'anchor_closedDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="8" 
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

	<s:if test="filter.showHasClosedDate">
		<div class="filterOption"><s:hidden name="filter.hasClosedDate"/></div>
	</s:if>

	<s:if test="filter.showExpiredDate">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_expiredDate'); return false;">Expired Date</a> 
			<span id="form1_expiredDate_query">= ALL</span><br /> 
			<span id="form1_expiredDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="8" 
			id="form1_expiredDate1" name="filter.expiredDate1" />
			<a id="anchor_expiredDate1" name="anchor_expiredDate1"
			onclick="cal2.select($('form1_expiredDate1'),'anchor_expiredDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="8" 
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
	
	<s:if test="filter.showPercentComplete">
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
	
	<s:if test="filter.showAuditFor">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditFor'); return false;">For Year
		</a> = <span id="form1_auditFor_query">ALL</span><br />
		<span id="form1_auditFor_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditFor" list="#{'2008':'2008','2007':'2007','2006':'2006','2005':'2005','2004':'2004','2003':'2003','2002':'2002','2001':'2001'}"
			cssClass="forms" name="filter.auditFor" multiple="true" size="5" />
		<script type="text/javascript">updateQuery('form1_auditFor');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditFor'); return false;">Clear</a>
		</span></div>
	</s:if>
	
	<s:if test="filter.showEmrRange">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_emr'); return false;">EMR</a> 
			<span id="form1_emr_query">= ALL</span><br /> 
			<span id="form1_emr" style="display: none" class="clearLink"><s:textfield name="filter.minEMR"
			id="form1_emr1" cssClass="forms" size="12" onfocus="clearText(this)" /> To: 
			<s:textfield name="filter.maxEMR" id="form1_emr2"
			cssClass="forms" size="12" onfocus="clearText(this)" />
			<script
			type="text/javascript">textQuery('form1_emr'); </script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_emr'); return false;">Clear</a></span>
		</div>
	</s:if>
	
	<s:if test="filter.showIncidenceRate">
		<div class="filterOption">Incidence Rate <s:textfield name="filter.incidenceRate"
			cssClass="forms" size="10" onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showCaoStatus">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_caoStatus'); return false;">Policy Status
			</a> = <span id="form1_caoStatus_query">ALL</span><br />
		<span id="form1_caoStatus_select" style="display: none"
			class="clearLink"> <s:select id="form1_caoStatus" list="filter.caoStatusList"
			cssClass="forms" name="filter.caoStatus" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_caoStatus');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_caoStatus'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showRecommendedStatus">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_recommendedStatus'); return false;">PICS Recommendation</a> = <span id="form1_recommendedStatus_query">ALL</span><br />
		<span id="form1_recommendedStatus_select" style="display: none"
			class="clearLink"> <s:select id="form1_recommendedStatus" list="filter.caoStatusList"
			cssClass="forms" name="filter.recommendedStatus" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_recommendedStatus');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_recommendedStatus'); return false;">Clear</a>
		</span></div>
	</s:if>
	
	<s:if test="filter.showBillingState">
		<div class="filterOption">
			<span>Billing State: <s:radio list="{'Activations', 'Renewals', 'Upgrades', 'All'}" name="filter.billingState"></s:radio></span>
		</div>
	</s:if>
	
	<s:if test="filter.showEmailTemplate">
		<br clear="all" />
		<div class="filterOption">
			<s:select list="filter.emailTemplateList" headerKey="0" headerValue="-Email Template-" cssClass="forms" name="filter.emailTemplate" listKey="id" listValue="templateName"/>
		</div>
	
		<div class="filterOption">Email Sent Date 
			<s:textfield cssClass="forms" size="8" 
			id="form1_emailSentDate" name="filter.emailSentDate" />
			<a id="anchor_emailSentDate" name="anchor_emailSentDate"
			onclick="cal2.select($('form1_emailSentDate'),'anchor_emailSentDate','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
		</div>
	</s:if>
	
	<s:if test="filter.showRegistrationDate">
		<br clear="all">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_registrationDate'); return false;">Registration Date</a> 
			<span id="form1_registrationDate_query">= ALL</span><br /> 
			<span id="form1_registrationDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="8" 
			id="form1_registrationDate1" name="filter.registrationDate1" />
			<a id="anchor_registrationDate1" name="anchor_registrationDate1"
			onclick="cal2.select($('form1_registrationDate1'),'anchor_registrationDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="8" 
			id="form1_registrationDate2" name="filter.registrationDate2" />
			<a id="anchor_registrationDate2" name="anchor_registrationDate2"
			onclick="cal2.select($('form1_registrationDate2'),'anchor_registrationDate2','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			<script
			type="text/javascript">textQuery('form1_registrationDate');</script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_registrationDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showInvoiceDueDate">
		<div class="filterOption"><a href="#" onclick="showTextBox('form1_invoiceDueDate'); return false;">Invoice Due Date</a> 
			<span id="form1_invoiceDueDate_query">= ALL</span><br /> 
			<span id="form1_invoiceDueDate" style="display: none" class="clearLink"><s:textfield cssClass="forms" size="8" 
			id="form1_invoiceDueDate1" name="filter.invoiceDueDate1" />
			<a id="anchor_invoiceDueDate1" name="anchor_invoiceDueDate1"
			onclick="cal2.select($('form1_invoiceDueDate1'),'anchor_invoiceDueDate1','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			To:<s:textfield cssClass="forms" size="8" 
			id="form1_invoiceDueDate2" name="filter.invoiceDueDate2" />
			<a id="anchor_invoiceDueDate2" name="anchor_invoiceDueDate2"
			onclick="cal2.select($('form1_invoiceDueDate2'),'anchor_invoiceDueDate2','M/d/yy'); return false;">
			<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
			<script
			type="text/javascript">textQuery('form1_invoiceDueDate');</script> <br />
			<a class="clearLink" href="#"
			onclick="clearTextField('form1_invoiceDueDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showConWithPendingAudits">
		<br clear="all" />
		<div class="filterOption">
		<label><s:checkbox name="filter.pendingPqfAnnualUpdate" />Show contractors with pending PQF and Annual Updates</label>
		</div>
	</s:if>
	
	<s:if test="filter.showPrimaryInformation">
		<br clear="all" />
		<div class="filterOption">
		<label><s:checkbox name="filter.primaryInformation" />
		Show Contact Info</label>
		</div>
	</s:if>

	<s:if test="filter.showTradeInformation">
		<div class="filterOption">
		<label><s:checkbox name="filter.tradeInformation" />
		Show Trade Info</label>
		</div>
	</s:if>

	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>
<div id="caldiv2" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>
