<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<div id="search"><s:if test="allowCollapsed">
	<div id="showSearch" onclick="showSearch()"
		<s:if test="filtered">style="display: none"</s:if>><a href="#">Show
	Filter Options</a></div>
	<div id="hideSearch"
		<s:if test="!filtered">style="display: none"</s:if>><a href="#"
		onclick="hideSearch()">Hide Filter Options</a></div>
</s:if> <s:form id="form1" action="%{filter.destinationAction}">

	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div><s:if test="filter.allowMailMerge">
		<button type="submit" id="write_email_button" name="button"
			value="Write Email" onclick="clickSearchSubmit('form1')"
			class="picsbutton positive" style="display: none">Write
		Email</button>
		<button type="button" name="button" value="Find Recipients"
			onclick="clickSearch('form1')" class="picsbutton">Find
		Recipients</button>
	</s:if> <s:else>
		<button id="searchfilter" type="submit" name="button" value="Search"
			onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');"
			class="picsbutton positive">Search</button>
	</s:else></div>

	<s:if test="filter.showAccountName">
		<div class="filterOption"><s:textfield name="filter.accountName"
			cssClass="forms" size="18" onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showStatus">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_status'); return false;">Status</a> = <span
			id="form1_status_query">ALL</span><br /><span id="form1_status_select"
			style="display: none" class="clearLink"><s:select
			list="filter.statusList" multiple="true" cssClass="forms"
			name="filter.status" id="form1_status" /><br />
		<script type="text/javascript">updateQuery('form1_status');</script> <a
			class="clearLink" href="#"
			onclick="clearSelected('form1_status'); return false;">Clear</a> </span></div>
	</s:if>
	
	<s:if test="filter.showType">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_type'); return false;">Type</a> = <span
			id="form1_type_query">ALL</span><br /><span id="form1_type_select"
			style="display: none" class="clearLink"><s:select
			list="filter.typeList" multiple="true" cssClass="forms"
			name="filter.type" id="form1_type" /><br />
		<script type="text/javascript">updateQuery('form1_type');</script> <a
			class="clearLink" href="#"
			onclick="clearSelected('form1_type'); return false;">Clear</a> </span></div>
	</s:if>
	
	<s:if test="filter.showOpen">
		<div class="filterOption"><span>Status =</span>
			<s:select cssClass="forms" list="#{1:'Open',0:'Closed'}" name="filter.open" />
		</div>
	</s:if>

	<s:if test="filter.showOfficeIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_officeIn'); return false;">Office In State/Province</a> =
		<span id="form1_officeIn_query">ALL</span><br />
		<span id="form1_officeIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_officeIn"
			list="filter.officeInList" cssClass="forms" name="filter.officeIn"
			listKey="id" listValue="name" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_officeIn');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_officeIn'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showWorksIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_worksIn'); return false;">Works In State/Province</a> = <span
			id="form1_worksIn_query">ALL</span><br />
		<span id="form1_worksIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_worksIn"
			list="filter.worksInList" cssClass="forms" name="filter.worksIn"
			listKey="id" listValue="name" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_worksIn');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_worksIn'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showLicensedIn">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_stateLicensedIn'); return false;">Licensed In State/Province</a> = <span id="form1_stateLicensedIn_query">ALL</span><br />
		<span id="form1_stateLicensedIn_select" style="display: none"
			class="clearLink"> <s:select id="form1_stateLicensedIn"
			list="filter.stateLicensesList" cssClass="forms"
			name="filter.stateLicensedIn" listKey="id" listValue="name"
			multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_stateLicensedIn');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_stateLicensedIn'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAddress">
		<br clear="all" />
		<div class="filterOption">
		<table>
			<tr>
				<td rowspan="2" style="vertical-align: top">Address: <s:textfield name="filter.city"
					cssClass="forms" size="15" onfocus="clearText(this)" /> <s:textfield
					name="filter.zip" cssClass="forms" size="5"
					onfocus="clearText(this)" /></td>
				<td style="padding-left: 1ex;"><a href="#"
					onclick="toggleBox('form1_state'); return false;">State</a> = <span
					id="form1_state_query">ALL</span></td>
				<td style="padding-left: 1ex;"><a href="#"
					onclick="toggleBox('form1_country'); return false;">Country</a> = <span
					id="form1_country_query">ALL</span></td>
			</tr>
			<tr>
				<td style="padding-left: 1ex;"><span id="form1_state_select"
					style="display: none" class="clearLink"><s:select
					id="form1_state" name="filter.state" list="filter.stateList"
					listKey="isoCode" listValue="name" cssClass="forms" multiple="true"
					size="15" onclick="clearSelected('form1_country');" /><br />
				<script type="text/javascript">updateQuery('form1_state');</script>
				<a class="clearLink" href="#"
					onclick="clearSelected('form1_state'); return false;">Clear</a> </span></td>
				<td style="padding-left: 1ex; vertical-align: top"><span
					id="form1_country_select" style="display: none" class="clearLink"><s:select
					id="form1_country" name="filter.country" list="filter.countryList"
					listKey="isoCode" listValue="name" cssClass="forms" multiple="true"
					size="15" /><br />
				<script type="text/javascript">updateQuery('form1_country');</script>
				<a class="clearLink" href="#"
					onclick="clearSelected('form1_country'); return false;">Clear</a> </span>
				</td>
			</tr>
		</table>
		</div>
	</s:if>
	
	<s:if test="filter.showState">
		<div class="filterOption">
			<a href="#" onclick="toggleBox('form1_state'); return false;">State</a> = 
				<span id="form1_state_query">ALL</span><br />
			<span id="form1_state_select" style="display: none" class="clearLink">
				<s:select id="form1_state" name="filter.state" list="filter.stateList"
					listKey="isoCode" listValue="name" cssClass="forms" multiple="true" size="15" /><br />
				<script type="text/javascript">updateQuery('form1_state');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_state'); return false;">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showCountry">
		<div class="filterOption">
			<a href="#" onclick="toggleBox('form1_country'); return false;">Country</a> =
				<span id="form1_country_query">ALL</span><br />
			<span id="form1_country_select" style="display: none" class="clearLink">
				<s:select id="form1_country" name="filter.country" list="filter.countryList"
					listKey="isoCode" listValue="name" cssClass="forms" multiple="true"	size="15" /><br />
				<script type="text/javascript">updateQuery('form1_country');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_country'); return false;">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showTaxID">
		<div class="filterOption"><s:textfield name="filter.taxID"
			cssClass="forms" size="9" onfocus="clearText(this)"
			title="must be 9 digits" /></div>
	</s:if>
	
	<s:if test="filter.showIndustry">
		<br clear="all" />
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_industry'); return false;">Industry</a> = <span
			id="form1_industry_query">ALL</span><br />
		<span id="form1_industry_select" style="display: none"
			class="clearLink"> <s:select id="form1_industry"
			name="filter.industry" list="filter.industryList"
			listValue="description" cssClass="forms" multiple="true" size="5" />
		<script type="text/javascript">updateQuery('form1_industry');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_industry'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showTrade">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_trade'); return false;">Trade</a> = <span
			id="form1_trade_query">ALL</span> <s:select
			list="filter.tradePerformedByList" cssClass="forms"
			name="filter.performedBy" value="filter.defaultSelectPerformedBy" /> <br />
		<span id="form1_trade_select" style="display: none" class="clearLink">
		<s:select id="form1_trade" list="filter.tradeList" listKey="id"
			listValue="name" cssClass="forms" name="filter.trade"
			multiple="true" size="10" /> <script type="text/javascript">updateQuery('form1_trade');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_trade'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showMinorityOwned">
		<div class="filterOption"><s:select cssClass="forms"
			list="#{2340:'Small Business',2354:'Minority-Owned',2373:'Women-Owned',3543:'Disabled Veteran Owned',3:'All the Above'}"
			name="filter.minorityQuestion" headerKey="0"
			headerValue="- Supplier Diversity -" /></div>
	</s:if>

	<s:if test="filter.showFlagStatus">
		<br clear="all" />
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_flagStatus'); return false;">Flag
		Status</a> = <span id="form1_flagStatus_query">ALL</span><br />
		<span id="form1_flagStatus_select" style="display: none"
			class="clearLink"> <s:select id="form1_flagStatus"
			list="filter.flagStatusList" cssClass="forms"
			name="filter.flagStatus" multiple="true" size="3" /> <script
			type="text/javascript">updateQuery('form1_flagStatus');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_flagStatus'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showWorkStatus">
		<div class="filterOption"><s:select
			list="#{'Y':'Yes','N':'No','P':'Pending'}" headerKey=""
			headerValue="- Work Status -" cssClass="forms"
			name="filter.workStatus" /></div>
	</s:if>

	<s:if test="filter.showWaitingOn">
		<div class="filterOption"><s:select list="filter.waitingOnList"
			headerKey="" headerValue="- Waiting On -" cssClass="forms"
			name="filter.waitingOn" /></div>
	</s:if>
	
	<s:if test="filter.showRiskLevel">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_risklevel'); return false;">Risk Level</a>
		= <span id="form1_risklevel_query">ALL</span><br />
		<span id="form1_risklevel_select" style="display: none"
			class="clearLink"> <s:select id="form1_risklevel"
			list="#{1:'Low', 2:'Med', 3:'High'}" cssClass="forms"
			name="filter.riskLevel" multiple="true" size="3" /> <script
			type="text/javascript">updateQuery('form1_risklevel');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_risklevel'); return false;">Clear</a> </span>
		</div>
	</s:if>


	<s:if
		test="filter.showOpertorTagName && filter.operatorTagNamesList.size() > 0">
		<div class="filterOption"><s:select
			list="filter.operatorTagNamesList" cssClass="forms"
			name="filter.operatorTagName" listKey="id" listValue="tag"
			headerKey="0" headerValue="- Operator Tag -" /></div>
	</s:if>

	<s:if test="filter.showHandledBy">
		<div class="filterOption"><s:select list="filter.handledByList"
			headerKey="" headerValue="- Follow Up By -" cssClass="forms"
			name="filter.handledBy" /></div>
	</s:if>

	<s:if test="filter.showCcOnFile">
		<div class="filterOption"><s:select list="#{'1':'Yes','0':'No'}"
			headerKey="2" headerValue="- Credit Card -" cssClass="forms"
			name="filter.ccOnFile" /></div>
	</s:if>

	<s:if test="filter.showAuditType">
		<br clear="all" />
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_pqfAuditTypeID'); return false;">PQF
		Type</a> = <span id="form1_pqfAuditTypeID_query">ALL</span><br />
		<span id="form1_pqfAuditTypeID_select" style="display: none"
			class="clearLink"> <s:select id="form1_pqfAuditTypeID"
			list="filter.pQFTypeList" cssClass="forms" name="filter.pqfTypeID"
			listKey="id" listValue="auditName" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_pqfAuditTypeID');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_pqfAuditTypeID'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAuditType">
		<div class="filterOption"><a id="audittypefilter" href="#"
			onclick="toggleBox('form1_auditTypeID'); return false;">Audit
		Type</a> = <span id="form1_auditTypeID_query">ALL</span><br />
		<span id="form1_auditTypeID_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditTypeID"
			list="filter.auditTypeList" cssClass="forms"
			name="filter.auditTypeID" listKey="id" listValue="auditName"
			multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_auditTypeID');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showPolicyType">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditTypeID'); return false;">Policy
		Type</a> = <span id="form1_auditTypeID_query">ALL</span><br />
		<span id="form1_auditTypeID_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditTypeID"
			list="filter.policyTypeList" cssClass="forms"
			name="filter.auditTypeID" listKey="id" listValue="auditName"
			multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_auditTypeID');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAuditStatus">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditStatus'); return false;">Status</a> =
		<span id="form1_auditStatus_query">ALL</span><br />
		<span id="form1_auditStatus_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditStatus"
			list="filter.auditStatusList" cssClass="forms"
			name="filter.auditStatus" multiple="true" size="5" /> <script
			type="text/javascript">updateQuery('form1_auditStatus');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditStatus'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showAuditor">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditorId'); return false;">Safety Professionals</a> =
		<span id="form1_auditorId_query">ALL</span><br />
		<span id="form1_auditorId_select" style="display: none"
			class="clearLink"> <s:select name="filter.auditorId"
			cssClass="forms" list="auditorList" listKey="id" listValue="name"
			multiple="true" size="5" id="form1_auditorId" /> <script
			type="text/javascript">updateQuery('form1_auditorId');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditorId'); return false;">Clear</a></span></div>
	</s:if>

	<s:if test="filter.showClosingAuditor">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_closingAuditorId'); return false;">Closing
		Safety Professionals</a> = <span id="form1_closingAuditorId_query">ALL</span><br />
		<span id="form1_closingAuditorId_select" style="display: none"
			class="clearLink"> <s:select name="filter.closingAuditorId"
			cssClass="forms" list="auditorList" listKey="id" listValue="name"
			multiple="true" size="5" id="form1_closingAuditorId" /> <script
			type="text/javascript">updateQuery('form1_closingAuditorId');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_closingAuditorId'); return false;">Clear</a></span></div>
	</s:if>

	<s:if test="filter.showConAuditor">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_conAuditorId'); return false;">CSR</a> = <span
			id="form1_conAuditorId_query">ALL</span><br />
		<span id="form1_conAuditorId_select" style="display: none"
			class="clearLink"> <s:select name="filter.conAuditorId"
			cssClass="forms" list="auditorList" listKey="id" listValue="name"
			multiple="true" size="5" id="form1_conAuditorId" /> <script
			type="text/javascript">updateQuery('form1_conAuditorId');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_conAuditorId'); return false;">Clear</a>
		</span></div>
	</s:if>

	<s:if test="filter.showOperator">
		<br clear="all" />
		<div class="filterOption"><s:if test="filter.showOperatorSingle">
			<s:select list="filter.operatorList" cssClass="forms"
				name="filter.operatorSingle" listKey="id" listValue="name" headerKey="0"
				headerValue="- Operator -" />
		</s:if> <s:else>
			<a href="#" onclick="toggleBox('form1_operator'); return false;">Operators</a> =
		<span id="form1_operator_query">ALL</span>
			<br />
			<span id="form1_operator_select" style="display: none"
				class="clearLink"> <s:select id="form1_operator"
				list="filter.operatorList" cssClass="forms" name="filter.operator"
				listKey="id" listValue="name" multiple="true" size="%{filter.operatorList.size() < 25? filter.operatorList.size() : 25}" /> <script
				type="text/javascript">updateQuery('form1_operator');</script> <br />
			<a class="clearLink" href="#"
				onclick="clearSelected('form1_operator'); return false;">Clear</a> </span>
		</s:else></div>
	</s:if>

	<s:if test="filter.showConLicense">
		<br clear="all" />
		<div class="filterOption"><s:select name="filter.validLicense"
			list="#{'Valid':'Valid','UnValid':'Invalid','All':'All'}"
			cssClass="forms" /></div>
	</s:if>

	<s:if test="filter.showCreatedDate">
		<br clear="all" />
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_createdDate'); return false;">Created
		Date</a> <span id="form1_createdDate_query">= ALL</span><br />
		<span id="form1_createdDate" style="display: none" class="clearLink"><s:textfield
			cssClass="forms datepicker" size="10" id="form1_createdDate1"
			name="filter.createdDate1" /> To:<s:textfield
			cssClass="forms datepicker" size="10" id="form1_createdDate2"
			name="filter.createdDate2" /> <script type="text/javascript">textQuery('form1_createdDate');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_createdDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showCompletedDate">
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_completedDate'); return false;">Completed
		Date</a> <span id="form1_completedDate_query">= ALL</span><br />
		<span id="form1_completedDate" style="display: none" class="clearLink"><s:textfield
			cssClass="forms datepicker" size="10" id="form1_completedDate1"
			name="filter.completedDate1" /> To:<s:textfield
			cssClass="forms datepicker" size="10" id="form1_completedDate2"
			name="filter.completedDate2" /> <script type="text/javascript">textQuery('form1_completedDate');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_completedDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showClosedDate">
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_closedDate'); return false;">Closed
		Date</a> <span id="form1_closedDate_query">= ALL</span><br />
		<span id="form1_closedDate" style="display: none" class="clearLink"><s:textfield
			cssClass="forms datepicker" size="10" id="form1_closedDate1"
			name="filter.closedDate1" /> To:<s:textfield
			cssClass="forms datepicker" size="10" id="form1_closedDate2"
			name="filter.closedDate2" /> <script type="text/javascript">textQuery('form1_closedDate');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_closedDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showHasClosedDate">
		<div class="filterOption"><s:hidden name="filter.hasClosedDate" /></div>
	</s:if>

	<s:if test="filter.showExpiredDate">
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_expiredDate'); return false;">Expired
		Date</a> <span id="form1_expiredDate_query">= ALL</span><br />
		<span id="form1_expiredDate" style="display: none" class="clearLink"><s:textfield
			cssClass="forms datepicker" size="10" id="form1_expiredDate1"
			name="filter.expiredDate1" /> To:<s:textfield
			cssClass="forms datepicker" size="10" id="form1_expiredDate2"
			name="filter.expiredDate2" /> <script type="text/javascript">textQuery('form1_expiredDate');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_expiredDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showPercentComplete">
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_percentComplete'); return false;">Percent
		Complete</a> <span id="form1_percentComplete_query">= ALL</span><br />
		<span id="form1_percentComplete" style="display: none"
			class="clearLink"><s:textfield name="filter.percentComplete1"
			id="form1_percentComplete1" cssClass="forms" size="12"
			onfocus="clearText(this)" /> To: <s:textfield
			name="filter.percentComplete2" id="form1_percentComplete2"
			cssClass="forms" size="12" onfocus="clearText(this)" /> <script
			type="text/javascript">textQuery('form1_percentComplete'); </script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_percentComplete'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showUnConfirmedAudits">
		<br clear="all" />
		<div class="filterOption"><label><s:checkbox
			name="filter.unScheduledAudits" /> Check to Search on UnConfirmed
		Audits</label></div>
	</s:if>

	<s:if test="filter.showAssignedCon">
		<div class="filterOption"><label><s:checkbox
			name="filter.assignedCon" /> Check to Search on Assigned Contractors</label>
		</div>
	</s:if>

	<s:if test="filter.showExpiredLicense">
		<br clear="all" />
		<div class="filterOption"><label><s:checkbox
			name="filter.conExpiredLic" /> Check to Search on Expired Licenses
		for Contractors</label></div>
	</s:if>

	<s:if test="filter.showInParentCorporation">
		<br clear="all" />
		<div class="filterOption"><label><s:checkbox
			name="filter.inParentCorporation" />Check to limit to contractors
		already working within my parent corporation</label></div>
	</s:if>

	<s:if test="filter.showAuditFor">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_auditFor'); return false;">For Year </a> =
		<span id="form1_auditFor_query">ALL</span><br />
		<span id="form1_auditFor_select" style="display: none"
			class="clearLink"> <s:select id="form1_auditFor"
			list="#{'2009':'2009','2008':'2008','2007':'2007','2006':'2006','2005':'2005','2004':'2004','2003':'2003','2002':'2002','2001':'2001'}"
			cssClass="forms" name="filter.auditFor" multiple="true" size="5" />
		<script type="text/javascript">updateQuery('form1_auditFor');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_auditFor'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showShaType">
		<br clear="all" />
		<div class="filterOption"><s:select list="filter.oshaTypesList"
			cssClass="forms" name="filter.shaType" headerKey=""
			headerValue="- Osha Type -" /></div>
	</s:if>
	
	<s:if test="filter.showShaTypeFlagCriteria">
		<br clear="all" />
		<div class="filterOption"><s:select list="filter.oshaTypesList"
			cssClass="forms" name="filter.shaTypeFlagCriteria" headerKey=""
			headerValue="- Osha Type -" /></div>
	</s:if>

	<s:if test="filter.showShaLocation">
		<div class="filterOption"><s:select
			list="#{'Corporate':'Corporate', 'Division':'Division','Region':'Region','Site':'Site'}"
			headerKey="" headerValue="- Osha Location -" cssClass="forms"
			name="filter.shaLocation" /></div>
	</s:if>

	<s:if test="filter.showVerifiedAnnualUpdates">
		<div class="filterOption"><s:select
			list="#{'1':'Verified','2':'UnVerified'}" headerKey="0"
			headerValue="- Verified Stats -" cssClass="forms"
			name="filter.verifiedAnnualUpdate" /></div>
	</s:if>

	<s:if test="filter.showEmrRange">
		<br clear="all" />
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_emr'); return false;">EMR</a> <span
			id="form1_emr_query">= ALL</span><br />
		<span id="form1_emr" style="display: none" class="clearLink"><s:textfield
			name="filter.minEMR" id="form1_emr1" cssClass="forms" size="12"
			onfocus="clearText(this)" /> To: <s:textfield name="filter.maxEMR"
			id="form1_emr2" cssClass="forms" size="12" onfocus="clearText(this)" />
		<script type="text/javascript">textQuery('form1_emr'); </script> <br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_emr'); return false;">Clear</a></span></div>
	</s:if>
	
	<s:if test="filter.showTrirRange">
		<br clear="all" />
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_trir'); return false;">TRIR</a> <span
			id="form1_trir_query">= ALL</span><br />
		<span id="form1_trir" style="display: none" class="clearLink"><s:textfield
			name="filter.minTRIR" id="form1_trir1" cssClass="forms" size="12"
			onfocus="clearText(this)" /> To: <s:textfield name="filter.maxTRIR"
			id="form1_trir2" cssClass="forms" size="12" onfocus="clearText(this)" />
		<script type="text/javascript">textQuery('form1_trir'); </script> <br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_trir'); return false;">Clear</a></span></div>
	</s:if>

	<s:if test="filter.showIncidenceRate">
		<div class="filterOption">Incidence Rate
			<s:textfield name="filter.incidenceRate" cssClass="forms" size="10" onfocus="clearText(this)" /> To
			<s:textfield name="filter.incidenceRateMax" cssClass="forms" size="10" onfocus="clearText(this)" />
		</div>
	</s:if>
	
	<s:if test="filter.showIncidenceRateAvg">
		<div class="filterOption">3 Year Average
			<s:textfield name="filter.incidenceRateAvg" cssClass="forms" size="10" onfocus="clearText(this)" /> To
			<s:textfield name="filter.incidenceRateAvgMax" cssClass="forms" size="10" onfocus="clearText(this)" />
		</div>
	</s:if>

	<s:if test="filter.showCohsStats">
		<div class="filterOption">Cad7 <s:textfield name="filter.cad7"
			cssClass="forms" size="10" onfocus="clearText(this)" /> Neer <s:textfield
			name="filter.neer" cssClass="forms" size="10"
			onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showCaoStatus">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_caoStatus'); return false;">Policy
		Status </a> = <span id="form1_caoStatus_query">ALL</span><br />
		<span id="form1_caoStatus_select" style="display: none"
			class="clearLink"> <s:select id="form1_caoStatus"
			list="filter.caoStatusList" cssClass="forms" name="filter.caoStatus"
			multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_caoStatus');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_caoStatus'); return false;">Clear</a> </span></div>
	</s:if>

	<s:if test="filter.showAMBest">
		<br clear="all" />
		<div class="filterOption"><s:select
			list="filter.aMBestRatingsList" cssClass="forms"
			name="filter.amBestRating" headerKey="0" headerValue="- Rating -" />
		<s:select list="filter.aMBestClassList" cssClass="forms"
			name="filter.amBestClass" headerKey="0" headerValue="- Class -" /></div>
	</s:if>

	<s:if test="filter.showRecommendedFlag">
		<div class="filterOption"><s:select list="filter.flagStatusList"
			headerKey="" headerValue="- Policy Compliance -" cssClass="forms"
			name="filter.recommendedFlag" /></div>
	</s:if>

	<s:if test="filter.showBillingState">
		<div class="filterOption"><span>Billing State: <s:radio
			list="{'Activations', 'Renewals', 'Upgrades', 'All'}"
			name="filter.billingState"></s:radio></span></div>
	</s:if>

	<s:if test="filter.showEmailTemplate">
		<br clear="all" />
		<div class="filterOption"><s:select
			list="filter.emailTemplateList" headerKey="0"
			headerValue="-Email Template-" cssClass="forms"
			name="filter.emailTemplate" listKey="id" listValue="templateName" />
		</div>

		<div class="filterOption">Email Sent Date <s:textfield
			cssClass="forms datepicker" size="10" id="form1_emailSentDate"
			name="filter.emailSentDate" /></div>
	</s:if>

	<s:if test="filter.showRegistrationDate">
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_registrationDate'); return false;">Registration
		Date</a> <span id="form1_registrationDate_query">= ALL</span><br />
		<span id="form1_registrationDate" style="display: none"
			class="clearLink"><s:textfield cssClass="forms datepicker"
			size="10" id="form1_registrationDate1"
			name="filter.registrationDate1" /> To:<s:textfield
			cssClass="forms datepicker" size="10" id="form1_registrationDate2"
			name="filter.registrationDate2" /> <script type="text/javascript">textQuery('form1_registrationDate');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_registrationDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showFollowUpDate">
		<div class="filterOption">
			<a href="#"	onclick="showTextBox('form1_followUpDate'); return false;">Follow Up Date</a>
			<span id="form1_followUpDate_query">= ALL</span><br />
			<span id="form1_followUpDate" style="display: none"
				class="clearLink">Before: <s:textfield
				cssClass="forms datepicker" size="10" id="form1_followUpDate2"
				name="filter.followUpDate" /> <script type="text/javascript">textQuery('form1_followUpDate');</script>
			<br />
			<a class="clearLink" href="#"
				onclick="clearTextField('form1_followUpDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showViewAll">
		<br clear="all" />
		<div class="filterOption">
			<label><s:checkbox name="filter.viewAll" /> Show All Registration Requests</label>
		</div>
	</s:if>

	<s:if test="filter.showInvoiceDueDate">
		<br clear="all">
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_invoiceDueDate'); return false;">Invoice
		Due Date</a> <span id="form1_invoiceDueDate_query">= ALL</span><br />
		<span id="form1_invoiceDueDate" style="display: none"
			class="clearLink"><s:textfield cssClass="forms datepicker"
			size="10" id="form1_invoiceDueDate1" name="filter.invoiceDueDate1" />
		To:<s:textfield cssClass="forms datepicker" size="10"
			id="form1_invoiceDueDate2" name="filter.invoiceDueDate2" /> <script
			type="text/javascript">textQuery('form1_invoiceDueDate');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_invoiceDueDate'); return false;">Clear</a></span>
		</div>
	</s:if>
	
	<s:if test="filter.showCaoStatusChangedDate">
		<br clear="all">
		<div class="filterOption"><a href="#"
			onclick="showTextBox('form1_statusChangedDate'); return false;">Status Changed Date
			</a> <span id="form1_statusChangedDate_query">= ALL</span><br />
		<span id="form1_statusChangedDate" style="display: none"
			class="clearLink"><s:textfield cssClass="forms datepicker"
			size="10" id="form1_statusChangedDate1" name="filter.statusChangedDate1" />
		To:<s:textfield cssClass="forms datepicker" size="10"
			id="form1_statusChangedDate2" name="filter.statusChangedDate2" /> <script
			type="text/javascript">textQuery('form1_statusChangedDate');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearTextField('form1_statusChangedDate'); return false;">Clear</a></span>
		</div>
	</s:if>

	<s:if test="filter.showDeactivationReason">
		<div class="filterOption"><s:select
			list="filter.deactivationReasons" name="contractor.reason"
			headerKey=" " headerValue="- Deactivation Reason -" cssClass="forms"
			name="filter.deactivationReason" /></div>
	</s:if>	
	
	<s:if test="filter.showInsuranceLimits">
		<div class="filterOption">
			<a href="#"	onclick="showInsuranceTextBoxes('form1_insuranceLimits'); return false;">Insurance Limits</a>
			<span id="form1_insuranceLimits_query">= ALL</span><br />
			<div
				id="form1_insuranceLimits"
				style="display: none"
				class="clearLink">
			<table>
				<tr>
					<td class="clearLink" title="General Liability - Each Occurence">GL Each Occurrence:</td>
					<td>$<s:textfield 
							id="form1_insuranceLimits1"
							cssClass="forms"
							title="Please enter a Number such as 5,000,000" 
							name="filter.glEachOccurrence" 
							onfocus="clearInsuranceText(this);"
							onblur="resetEmptyField(this);"
							onkeyup="isNumber(this,1);"
							size="15" />
						<span id="error1" class="redMain"></span>
					</td>
				</tr>
				<tr>
					<td class="clearLink" title="General Liability - General Aggregate">GL General Aggregate:</td>
					<td>$<s:textfield
							id="form1_insuranceLimits2"
							cssClass="forms"
							title="Please enter a Number such as 5,000,000" 
							name="filter.glGeneralAggregate" 
							onfocus="clearInsuranceText(this);" 
							onblur="resetEmptyField(this);"
							onkeyup="isNumber(this,2);"
							size="15" />
						<span id="error2" class="redMain"></span>
					</td>
				</tr>
				<tr>
					<td class="clearLink" title="Automobile Liability - Combined Single">AL Combined Single:</td>
					<td>$<s:textfield
							id="form1_insuranceLimits3"
							cssClass="forms"
							title="Please enter a Number such as 5,000,000" 
							name="filter.alCombinedSingle" 
							onfocus="clearInsuranceText(this);" 
							onblur="resetEmptyField(this);"
							onkeyup="isNumber(this,3);"
							size="15" />
						<span id="error3" class="redMain"></span>
					</td>
				</tr>
				<tr>
					<td class="clearLink" title="Workers Compensation - Each Accident">WC Each Accident:</td>
					<td>$<s:textfield
							id="form1_insuranceLimits4"
							cssClass="forms"
							title="Please enter a Number such as 5,000,000" 
							name="filter.wcEachAccident" 
							onfocus="clearInsuranceText(this);" 
							onblur="resetEmptyField(this);"
							onkeyup="isNumber(this,4);"
							size="15" />
						<span id="error4" class="redMain"></span>
					</td>	
				</tr>
				<tr>
					<td class="clearLink" title="Excess Liability - Each Occurence">EX Each Occurrence:</td>
					<td>$<s:textfield 
							id="form1_insuranceLimits5"
							cssClass="forms"
							title="Please enter a Number such as 5,000,000" 
							name="filter.exEachOccurrence" 
							onfocus="clearInsuranceText(this);" 
							onblur="resetEmptyField(this);"
							onkeyup="isNumber(this,5);"
							size="15" />
						<span id="error5" class="redMain"></span>
					</td>
				</tr>
			</table>
			<script type="text/javascript">insuranceLimitsTextQuery('form1_insuranceLimits');</script>
			<a class="clearLink" href="#"
				onclick="clearInsuranceTextFields('form1_insuranceLimits'); return false;">Clear</a>
			</div>
		</div>	
	</s:if>
	
	<s:if test="filter.showQuestionAnswer">
		<br clear="all">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_questionId'); return false;">Questions</a> =
		<span id="form1_questionId_query">ALL</span><br />
		<span id="form1_questionId_select" style="display: none"
			class="clearLink"> <s:select name="filter.questionIds"
			cssClass="forms" list="filter.questionsByAuditList" listKey="id" listValue="questionTexts.get(0).question"
			multiple="true" size="23" id="form1_questionId" /> <script
			type="text/javascript">updateQuery('form1_questionId');</script> <br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_questionId'); return false;">Clear</a></span>
		</div><br clear="all">
		<div class="filterOption">Answer: <s:textfield name="filter.answer"/></div>		
	</s:if>	
	
	<s:if test="filter.showConWithPendingAudits">
		<br clear="all" />
		<div class="filterOption"><label><s:checkbox
			name="filter.pendingPqfAnnualUpdate" />Show contractors with pending
		PQF and Annual Updates</label></div>
	</s:if>

	<s:if test="filter.showPrimaryInformation">
		<br clear="all" />
		<div class="filterOption"><label><s:checkbox
			name="filter.primaryInformation" /> Show Contact Info</label></div>
	</s:if>

	<s:if test="filter.showTradeInformation">
		<div class="filterOption"><label><s:checkbox
			name="filter.tradeInformation" /> Show Trade Info</label></div>
	</s:if>
	
	<s:if test="filter.showOQ">
		<div class="filterOption"><label><s:checkbox
			name="filter.oq" /> Show OQ contractors</label></div>
	</s:if>

	<s:if test="filter.showHSE">
		<div class="filterOption"><label><s:checkbox
			name="filter.hse" /> Show contractors requiring competency reviews</label></div>
	</s:if>

	<pics:permission perm="DevelopmentEnvironment">
		<div class="filterOption"><label>Query API</label> <s:textfield
			name="filter.customAPI" /></div>
	</pics:permission>

	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>
