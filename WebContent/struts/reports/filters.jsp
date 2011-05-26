<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<script type="text/javascript" src="js/jquery/tokeninput/jquery.tokeninput.js"></script>
<link rel="stylesheet" type="text/css" href="js/jquery/tokeninput/styles/token-input.css" />

<style>
.q_box{
	display: none;	
}

.open {
	display: inline-block;
}

select.hidden {
	display: none;
}

</style>

<div id="search">
<s:if test="allowCollapsed">
	<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if>>
		<a href="#">Show Filter Options</a>
	</div>
	<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if>>
		<a href="#" onclick="hideSearch()">Hide Filter Options</a>
	</div>
</s:if>

<s:form id="form1" action="%{filter.destinationAction}">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div>
		<s:if test="filter.allowMailMerge">
			<button type="submit" id="write_email_button" name="button"
			value="Write Email" class="picsbutton positive" style="display: none">Write Email</button>
			<button type="button" id="find_recipients" name="button" value="Find Recipients" class="picsbutton">Find Recipients</button>
		</s:if>
		<s:else>
			<button id="searchfilter" type="submit" name="button" value="Search"
			onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');"
			class="picsbutton positive">Search</button>
		</s:else>
	</div>

	<s:if test="filter.showAccountName">
		<div class="filterOption">
			<s:textfield name="filter.accountName" cssClass="forms" size="18" /></div>
	</s:if>

	<s:if test="filter.showStatus">
		<div class="filterOption" id="status">
			<a href="#" class="filterBox">Status</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select">
				<s:select list="filter.statusList" multiple="true" cssClass="forms" name="filter.status" /><br />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showType">
		<div class="filterOption" id="type">
			<a href="#" class="filterBox">Type</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select">
				<s:select list="filter.typeList" multiple="true" cssClass="forms" name="filter.type"/><br />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showOpen">
		<div class="filterOption">
			<span>Status =</span> 
			<s:select cssClass="forms" list="#{1:'Open',0:'Closed'}" name="filter.open" />
		</div>
	</s:if>

	<s:if test="filter.showOfficeIn">
		<div class="filterOption">
			<a href="#"class="filterBox">Office In State/Province</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:textfield rel="StateQuestion/OfficeLocation" name="filter.officeIn" cssClass="tokenAuto" />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showWorksIn">
		<div class="filterOption">
			<a href="#" class="filterBox">Works In State/Province</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:textfield rel="StateQuestion/OfficeLocation" name="filter.worksIn" cssClass="tokenAuto" />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showLicensedIn">
		<div class="filterOption">
			<a href="#" class="filterBox">Licensed In State/Province</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:textfield rel="StateQuestion/OfficeLocation" name="filter.stateLicensedIn" cssClass="tokenAuto" />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showAddress">
		<br clear="all" />
		<div class="filterOption">
		<table>
			<tr>
				<td rowspan="2" style="vertical-align: top">Address: 
				<s:textfield name="filter.city" cssClass="forms" size="15"  /> 
				<s:textfield name="filter.zip" cssClass="forms" size="5"  />
				</td>
				<td style="padding-left: 1ex;">
					<a href="#" class="filterBox">State</a> = 
					<span class="q_status">ALL</span>
				</td>
				<td style="padding-left: 1ex;">
					<a href="#" class="filterBox">Country</a> = 
					<span class="q_status">ALL</span>
				</td>
			</tr>
			<tr>
				<td style="padding-left: 1ex;">
					<span id="form1_state_select" style="display: none" class="clearLink q_box">
						<s:select id="form1_state" name="filter.state" list="filter.stateList" listKey="isoCode" 
							listValue="name" cssClass="forms" multiple="true" size="15" onclick="clearSelected('form1_country');" /><br />
						<script type="text/javascript">updateQuery('form1_state');</script>
						<a class="clearLink" href="#" onclick="clearSelected('form1_state'); return false;">Clear</a> \
					</span>
				</td>
				<td style="padding-left: 1ex; vertical-align: top">
					<span id="form1_country_select" style="display: none" class="clearLink q_box">
						<s:select id="form1_country" name="filter.country" list="filter.countryList" listKey="isoCode" 
							listValue="name" cssClass="forms" multiple="true" size="15" /><br />
						<script type="text/javascript">updateQuery('form1_country');</script>
						<a class="clearLink" href="#" onclick="clearSelected('form1_country'); return false;">Clear</a> 
					</span>
				</td>
			</tr>
		</table>
		</div>
	</s:if>

	<s:if test="filter.showState">
		<div class="filterOption">
			<a href="#" class="filterBox">State</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select">
				<s:select id="form1_state" name="filter.state" list="filter.stateList" listKey="isoCode" 
					listValue="name" cssClass="forms" multiple="true" size="15" /><br />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showCountry">
		<div class="filterOption">
			<a href="#" class="filterBox">Country</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select id="form1_country" name="filter.country" list="filter.countryList" listKey="isoCode"
					listValue="name" cssClass="forms" multiple="true" size="15" /><br />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showTaxID">
		<div class="filterOption">
			<s:textfield name="filter.taxID" cssClass="forms" size="9"  title="must be 9 digits" />
		</div>
	</s:if>
	
	<s:if test="filter.showTrade">
		<div class="filterOption">
			<a href="#" class="filterBox">Trade</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select">
				<s:textfield rel="Trade" name="filter.trade" cssClass="tokenAuto" />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showMinorityOwned">
		<div class="filterOption">
			<s:select cssClass="forms" list="filter.minorityQuestions"
				name="filter.minorityQuestion" headerKey="0" headerValue="- Supplier Diversity -" />
		</div>
	</s:if>

	<s:if test="filter.showFlagStatus">
		<br clear="all" />
		<div class="filterOption">
			<a href="#" class="filterBox">Flag Status</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select list="filter.flagStatusList" cssClass="forms" name="filter.flagStatus" multiple="true" size="3" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showWorkStatus">
		<div class="filterOption">
			<a href="#">Work Status</a> =  
			<s:select list="#{'Y':'Yes','N':'No','P':'Pending'}" headerKey="" headerValue="Any" cssClass="forms" name="filter.workStatus" />
		</div>
	</s:if>

	<s:if test="filter.showWaitingOn">
		<div class="filterOption">
			<s:select list="filter.waitingOnList" headerKey="" headerValue="- Waiting On -" cssClass="forms" name="filter.waitingOn" />
		</div>
	</s:if>

	<s:if test="filter.showRiskLevel">
		<div class="filterOption" id="risklevel">
			<a href="#" class="filterBox">Risk Level</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select list="#{1:'Low', 2:'Med', 3:'High'}" cssClass="forms" name="filter.riskLevel" multiple="true" size="3" /> 
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>


	<s:if
		test="filter.showOpertorTagName && filter.operatorTagNamesList.size() > 0">
		<div class="filterOption">
			<s:select list="filter.operatorTagNamesList" cssClass="forms" name="filter.operatorTagName" listKey="id" listValue="tag" headerKey="0" headerValue="- Tag -" />
		</div>
	</s:if>

	<s:if test="filter.showHandledBy">
		<div class="filterOption">
			<s:select list="filter.handledByList" headerKey="" headerValue="- Follow Up By -" cssClass="forms" name="filter.handledBy" />
		</div>
	</s:if>

	<s:if test="filter.showCcOnFile">
		<div class="filterOption">
			<s:select list="#{'1':'Yes','0':'No'}" headerKey="2" headerValue="- Credit Card -" cssClass="forms" name="filter.ccOnFile" />
		</div>
	</s:if>
	
	<s:if test="filter.showOperator">
		<div class="filterOption">
			<s:if test="filter.showOperatorSingle">
				<s:select list="filter.operatorList" cssClass="forms" name="filter.operatorSingle" listKey="id" listValue="name" headerKey="0" headerValue="- Operator -" />
			</s:if> 
			<s:else>
				<a href="#" class="filterBox">Operators</a> =
				<span class="q_status">ALL</span><br />
				<span class="clearLink q_box select"> 
					<s:textfield rel="Operator" name="filter.operator" cssClass="tokenAuto" />
					<a class="clearLink" href="#">Clear</a>
				</span>
			</s:else>
		</div>
	</s:if>
	
	<s:if test="filter.showWaCategories">
		<br clear="all" />
		<div class="filterOption">
			<a href="#" class="filterBox">Categories</a> =
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select">
				<s:select list="filter.waCategoryList" cssClass="forms" 
					name="filter.waCategories" listKey="id" listValue="name" multiple="true" size="25" />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showWaAuditTypes">
		<div class="filterOption">
			<a href="#" class="filterBox">Audit Type</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select list="filter.waAuditTypesList" cssClass="forms" 
					name="filter.waAuditTypes" listKey="id" listValue="name" multiple="true" size="5" /><br /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showCaoOperator">
		<br clear="all" />
		<div class="filterOption">
			<a href="#" class="filterBox">Operators</a> =
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:textfield rel="Operator" name="filter.operator" cssClass="tokenAuto" />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showAuditType">
		<div class="filterOption">
			<a href="#" class="filterBox">PQF Type</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select list="filter.pQFTypeList" cssClass="forms" name="filter.pqfTypeID" listKey="id" listValue="name" multiple="true" size="5" /> 
					<script type="text/javascript">updateQuery('form1_pqfAuditTypeID');</script><br />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showAuditType">
		<div class="filterOption">
			<a href="#" class="filterBox">Audit Type</a> = 
			<span >ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select list="filter.auditTypeList" cssClass="forms" name="filter.auditTypeID" listKey="id" listValue="name" multiple="true" size="5" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showPolicyType">
		<div class="filterOption">
			<a href="#" class="filterBox">Policy Type</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select list="filter.policyTypeList" cssClass="forms" name="filter.auditTypeID"
					 listKey="id" listValue="name" multiple="true" size="5" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showAuditStatus">
		<div class="filterOption">
			<a href="#" class="filterBox">Status</a> =
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select id="form1_auditStatus" list="filter.auditStatusList" cssClass="forms" name="filter.auditStatus" multiple="true" size="5" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showCaowStatus">
		<div class="filterOption">
			<a href="#" class="filterBox">Status Workflow</a> =
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select id="form1_caowStatus" list="filter.auditStatusList" cssClass="forms" name="filter.caowStatus" multiple="true" size="5" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showAuditor">
		<div class="filterOption">
			<a href="#" class="filterBox">Safety Professionals</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select name="filter.auditorId" cssClass="forms" list="auditorList" listKey="id" listValue="name" multiple="true" size="5" id="form1_auditorId" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showClosingAuditor">
		<div class="filterOption">
			<a href="#" class="filterBox">Closing Safety Professionals</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select name="filter.closingAuditorId" cssClass="forms" list="auditorList" listKey="id" listValue="name" 
					multiple="true" size="5" id="form1_closingAuditorId" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showAccountManager">
		<div class="filterOption">
			<a href="#" class="filterBox">Account Mgr</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select name="filter.accountManager"cssClass="forms" list="accountManagers" listKey="id" listValue="name"
					multiple="true" size="5" id="form1_accountManager" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showConAuditor">
		<div class="filterOption">
			<a href="#" class="filterBox">CSR</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select name="filter.conAuditorId"cssClass="forms" list="auditorList" listKey="id" listValue="name"
					multiple="true" size="5" id="form1_conAuditorId" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showConLicense">
		<br clear="all" />
		<div class="filterOption">
			<s:select name="filter.validLicense" list="#{'Valid':'Valid','UnValid':'Invalid','All':'All'}" cssClass="forms" />
		</div>
	</s:if>

	<s:if test="filter.showCreatedDate">
		<br clear="all" />
		<div class="filterOption">
			<a href="#" class="filterBox">Created Date</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield cssClass="forms datepicker" size="10" name="filter.createdDate1" /> 
				To:<s:textfield cssClass="forms datepicker" size="10" name="filter.createdDate2" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showPercentComplete">
		<div class="filterOption">
			<a href="#" class="filterBox">Percent Complete</a> =  
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield name="filter.percentComplete1" cssClass="forms" size="12"  /> 
				To: <s:textfield name="filter.percentComplete2" cssClass="forms" size="12"  />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showUnConfirmedAudits">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.unScheduledAudits" /> Check to Search on UnConfirmed Audits
			</label>
		</div>
	</s:if>

	<s:if test="filter.showAssignedCon">
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.assignedCon" /> Check to Search on Assigned Contractors
			</label>
		</div>
	</s:if>

	<s:if test="filter.showExpiredLicense">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.conExpiredLic" /> Check to Search on Expired Licenses for Contractors
			</label>
		</div>
	</s:if>

	<s:if test="filter.showInParentCorporation">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.inParentCorporation" />Check to limit to contractors already working within my parent corporation
			</label>
		</div>
	</s:if>

	<s:if test="filter.showAuditFor">
		<div class="filterOption">
			<a href="#" class="filterBox">For Year </a> =
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select id="form1_auditFor" list="filter.yearList" cssClass="forms" name="filter.auditFor" multiple="true" size="5" />
				<a class="clearLink" href="#">Clear</a> 
			</span>
		</div>
	</s:if>

	<s:if test="filter.showShaType">
		<br clear="all" />
		<div class="filterOption">
			<s:select list="filter.oshaTypesList" cssClass="forms" name="filter.shaType" headerKey="" headerValue="- Osha Type -" />
		</div>
	</s:if>

	<s:if test="filter.showShaTypeFlagCriteria">
		<br clear="all" />
		<div class="filterOption">
			<s:select list="filter.oshaTypesList" cssClass="forms" name="filter.shaTypeFlagCriteria" headerKey="" headerValue="- Osha Type -" />
		</div>
	</s:if>

	<s:if test="filter.showShaLocation">
		<div class="filterOption">
			<s:select list="#{'Corporate':'Corporate', 'Division':'Division','Region':'Region','Site':'Site'}"
				headerKey="" headerValue="- Osha Location -" cssClass="forms" name="filter.shaLocation" />
		</div>
	</s:if>

	<s:if test="filter.showVerifiedAnnualUpdates">
		<div class="filterOption">
			<s:select list="#{'1':'Verified','2':'UnVerified'}" headerKey="0" headerValue="- Verified Stats -" 
				cssClass="forms" name="filter.verifiedAnnualUpdate" />
		</div>
	</s:if>

	<s:if test="filter.showEmrRange">
		<br clear="all" />
		<div class="filterOption">
			<a href="#" class="filterBox">EMR</a> =  
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield name="filter.minEMR" cssClass="forms" size="12"  /> 
				To: <s:textfield name="filter.maxEMR" cssClass="forms" size="12"  />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showTrirRange">
		<br clear="all" />
		<div class="filterOption">
			<a href="#" class="filterBox">TRIR</a> =  
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield name="filter.minTRIR" cssClass="forms" size="12"  /> 
				To: <s:textfield name="filter.maxTRIR" cssClass="forms" size="12"  />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showScoreRange">
		<br clear="all" />
		<div class="filterOption">
			<a href="#" class="filterbox">Score</a> =  
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield name="filter.scoreMin" cssClass="forms" size="12"  /> 
				To: <s:textfield name="filter.scoreMax" cssClass="forms" size="12"  />
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showIncidenceRate">
		<div class="filterOption">
			Incidence Rate <s:textfield name="filter.incidenceRate" cssClass="forms" size="10"  /> 
			To <s:textfield name="filter.incidenceRateMax" cssClass="forms" size="10"  />
		</div>
	</s:if>

	<s:if test="filter.showIncidenceRateAvg">
		<div class="filterOption">
			3 Year Average <s:textfield name="filter.incidenceRateAvg" cssClass="forms" size="10"  /> 
			To <s:textfield name="filter.incidenceRateAvgMax" cssClass="forms" size="10"  />
		</div>
	</s:if>

	<s:if test="filter.showCohsStats">
		<div class="filterOption">
			Cad7 <s:textfield name="filter.cad7" cssClass="forms" size="10"  /> 
			Neer <s:textfield name="filter.neer" cssClass="forms" size="10"  /></div>
	</s:if>

	<s:if test="filter.showAMBest">
		<br clear="all" />
		<div class="filterOption">
			<s:select list="filter.aMBestRatingsList" cssClass="forms" name="filter.amBestRating" headerKey="0" headerValue="- Rating -" />
			<s:select list="filter.aMBestClassList" cssClass="forms" name="filter.amBestClass" headerKey="0" headerValue="- Class -" />
		</div>
	</s:if>

	<s:if test="filter.showRecommendedFlag">
		<div class="filterOption">
			<s:select list="filter.flagStatusList" headerKey="" headerValue="- Policy Compliance -" cssClass="forms"
				name="filter.recommendedFlag" />
		</div>
	</s:if>

	<s:if test="filter.showBillingState">
		<div class="filterOption">
			<span>Billing State: 
				<s:radio list="{'Activations', 'Renewals', 'Upgrades', 'All'}" name="filter.billingState"></s:radio>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showEmailTemplate">
		<br clear="all" />
		<div class="filterOption">
			<s:select list="filter.emailTemplateList" headerKey="0" headerValue="-Email Template-" cssClass="forms"
				name="filter.emailTemplate" listKey="id" listValue="templateName" />
		</div>
		<div class="filterOption">
			Email Sent Date 
			<s:textfield cssClass="forms datepicker" size="10" id="form1_emailSentDate" name="filter.emailSentDate" />
		</div>
	</s:if>

	<s:if test="filter.showRegistrationDate">
		<div class="filterOption">
			<a href="#" class="filterBox">Registration Date</a> 
			<span class="q_status">= ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield cssClass="forms datepicker" size="10" name="filter.registrationDate1" /> 
				To:<s:textfield cssClass="forms datepicker" size="10" name="filter.registrationDate2" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showExpiredDate">
		<div class="filterOption">
			<a href="#" class="filterBox">Expired Date</a> 
			<span class="q_status">= ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield cssClass="forms datepicker" size="10" name="filter.expiredDate1" /> 
				To:<s:textfield cssClass="forms datepicker" size="10" name="filter.expiredDate2" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showFollowUpDate">
		<div class="filterOption">
			<a href="#" class="filterBox">Follow Up Date</a> 
			<span class="q_status">= ALL</span><br />
			<span class="clearLink q_box textfield">
				Before: <s:textfield cssClass="forms datepicker" size="10" name="filter.followUpDate" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showViewAll">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.viewAll" /> Show All Registration Requests
			</label>
		</div>
	</s:if>

	<s:if test="filter.showInvoiceDueDate">
		<br clear="all">
		<div class="filterOption">
			<a href="#" class="filterBox">Invoice Due Date</a> =  
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield cssClass="forms datepicker" size="10" id="form1_invoiceDueDate1" name="filter.invoiceDueDate1" />
				To:<s:textfield cssClass="forms datepicker" size="10" id="form1_invoiceDueDate2" name="filter.invoiceDueDate2" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showCaoStatusChangedDate">
		<div class="filterOption">
			<a href="#" class="filterBox">Status Changed Date </a> =  
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield cssClass="forms datepicker" size="10" name="filter.statusChangedDate1" /> 
				To:<s:textfield cssClass="forms datepicker" size="10" name="filter.statusChangedDate2" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showCaowUpdateDate">
		<br clear="all">
		<div class="filterOption">
			<a href="#" class="filterBox">Workflow Status Changed Date </a> 
			<span class="q_status">= ALL</span><br />
			<span class="clearLink q_box textfield">
				<s:textfield cssClass="forms datepicker" size="10" name="filter.caowUpdateDate1" /> 
				To:<s:textfield cssClass="forms datepicker" size="10" name="filter.caowUpdateDate2" /> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showDeactivationReason">
		<div class="filterOption">\
			<s:select list="filter.deactivationReasons" headerKey=" " headerValue="- Deactivation Reason -" cssClass="forms"
				name="filter.deactivationReason" />
		</div>
	</s:if>

	<s:if test="filter.showInsuranceLimits">
		<div class="filterOption">
			<a href="#" onclick="showInsuranceTextBoxes('form1_insuranceLimits'); return false;">Insurance Limits</a> 
			<span id="form1_insuranceLimits_query">= ALL</span><br />
				<div id="form1_insuranceLimits" style="display: none"class="clearLink q_box">
					<table class="insuranceLimits">
						<tr>
							<td class="clearLink" title="General Liability - Each Occurence">GL Each Occurrence:</td>
							<td>
								$<s:textfield id="form1_insuranceLimits1" cssClass="forms" title="Please enter a Number such as 5,000,000" 
									name="filter.glEachOccurrence" onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,1);" size="15" /> 
								<span id="error1" class="redMain"></span>
							</td>
						</tr>
						<tr>
							<td class="clearLink" title="General Liability - General Aggregate">GL General Aggregate:</td>
							<td>
								$<s:textfield id="form1_insuranceLimits2" cssClass="forms" title="Please enter a Number such as 5,000,000"
									name="filter.glGeneralAggregate" onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" 
									onkeyup="isNumber(this,2);" size="15" /> 
								<span id="error2" class="redMain"></span></td>
						</tr>
						<tr>
							<td class="clearLink" title="Automobile Liability - Combined Single">AL Combined Single:</td>
							<td>
								$<s:textfield id="form1_insuranceLimits3" cssClass="forms" title="Please enter a Number such as 5,000,000" name="filter.alCombinedSingle" 
									onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,3);" size="15" /> 
								<span id="error3" class="redMain"></span>
							</td>
						</tr>
						<tr>
							<td class="clearLink" title="Workers Compensation - Each Accident">WC Each Accident:</td>
							<td>
								$<s:textfield id="form1_insuranceLimits4" cssClass="forms" title="Please enter a Number such as 5,000,000" name="filter.wcEachAccident" 
									onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,4);" size="15" /> 
								<span id="error4" class="redMain"></span>
							</td>
						</tr>
						<tr>
							<td class="clearLink" title="Excess Liability - Each Occurence">EX Each Occurrence:</td>
							<td>
								$<s:textfield id="form1_insuranceLimits5" cssClass="forms" title="Please enter a Number such as 5,000,000" name="filter.exEachOccurrence" 
									onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,5);" size="15" /> 
								<span id="error5" class="redMain"></span>
							</td>
						</tr>
				</table>
				<a class="clearLink" href="#" onclick="clearInsuranceTextFields('form1_insuranceLimits'); return false;">Clear</a>
			</div>
		</div>
	</s:if>

	<s:if test="filter.showQuestionAnswer">
		<br clear="all">
		<div class="filterOption">
			<a href="#" class="filterbox">Questions</a> = 
			<span class="q_status">ALL</span><br />
			<span class="clearLink q_box select"> 
				<s:select name="filter.questionIds" cssClass="forms" list="filter.questionsByAuditList" listKey="id" listValue="name" multiple="true" size="23"/> 
				<a class="clearLink" href="#">Clear</a>
			</span>
		</div>
		<br clear="all">
		<div class="filterOption">
			Answer: <s:textfield name="filter.answer" />
		</div>
	</s:if>

	<s:if test="filter.showConWithPendingAudits">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.pendingPqfAnnualUpdate" />Show contractors with pending PQF and Annual Updates
			</label>
		</div>
	</s:if>
	
	<s:if test="filter.showNotRenewingContractors">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.notRenewingContractors" />Show contractors set to not renew
			</label>
		</div>
	</s:if>
	
	<s:if test="filter.showContractorsWithPendingMembership">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.contractorsWithPendingMembership" />Show contractors with unpaid Membership Invoices
			</label>
		</div>
	</s:if>

	<s:if test="filter.showPrimaryInformation">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.primaryInformation" /> Show Contact Info
			</label>
		</div>
	</s:if>

	<s:if test="filter.showTradeInformation">
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.tradeInformation" /> Show Trade Info
			</label>
		</div>
	</s:if>

	<s:if test="filter.showOQ">
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.oq" /> Show OQ contractors
			</label>
		</div>
	</s:if>

	<s:if test="filter.showHSE">
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.hse" /> Show contractors requiring competency reviews
			</label>
		</div>
	</s:if>

	<pics:permission perm="DevelopmentEnvironment">
		<div class="filterOption">
			<label>Query API</label> 
			<s:textfield name="filter.customAPI" />
		</div>
	</pics:permission>

	<s:if test="filter.showAuditCreationFlagChanges || filter.showAuditStatusFlagChanges">
		<br clear="all" />
		Expected Flag Changes:		
	</s:if>

	<s:if test="filter.showAuditCreationFlagChanges">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.auditCreationFlagChanges" />Show Flag Changes related to newly created Audits
			</label>
		</div>
	</s:if>

	<s:if test="filter.showAuditStatusFlagChanges">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.auditStatusFlagChanges" />Show Flag Changes due to Audit Status changes
			</label>
		</div>
	</s:if>

	<s:if test="filter.showAuditQuestionFlagChanges">
		<br clear="all" />
		<div class="filterOption">
			<label>
				<s:checkbox name="filter.auditQuestionFlagChanges" />Show Audit Question Related Flag Changes
			</label>
		</div>
	</s:if>

	<br clear="all" />
	<div class="alphapaging">
		<s:property value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form>
</div>
