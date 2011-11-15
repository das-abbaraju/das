<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:include value="../actionMessages.jsp" />

<div id="search">
	<s:if test="allowCollapsed">
		<div id="showSearch"<s:if test="filtered"> style="display: none"</s:if>>
			<a href="#"><s:text name="" /><s:text name="Filters.button.ShowFilterOptions" /></a>
		</div>
		<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if>>
			<a href="#" onclick="hideSearch()"><s:text name="Filters.button.HideFilterOptions" /></a>
		</div>
	</s:if>
	
	<s:form id="form1" action="%{filter.destinationAction}">
		<s:hidden name="filter.ajax" />
		<s:hidden name="filter.destinationAction" />
		<s:hidden name="filter.allowMailMerge" />
		<s:hidden name="filter.allowMailReport" />
		<s:hidden name="showPage" value="1" />
		<s:hidden name="filter.startsWith" />
		<s:hidden name="orderBy" />
	
		<div>
			<s:if test="filter.allowMailMerge">
				<button type="submit" id="write_email_button" name="button" value="Write Email" class="picsbutton positive" style="display: none">
					<s:text name="Filters.button.WriteEmail" />
				</button>
				<button type="button" id="find_recipients" name="button" value="Find Recipients" class="picsbutton">
					<s:text name="Filters.button.FindRecipients" />
				</button>
			</s:if>
			<s:else>
				<s:if test="filter.allowMailReport">
					<button type="submit" id="send_report_button" name="button" value="Email Report" class="picsbutton positive" style="display: none">
						<s:text name="Filters.button.EmailReport" />
					</button>
					<button type="submit" name="button" value="Search" onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');" class="picsbutton positive">
						<s:text name="button.Search" />
					</button>
				</s:if>
				<s:else>
					<button id="searchfilter" type="submit" name="button" value="Search" onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');" class="picsbutton positive">
						<s:text name="button.Search" />
					</button>
				</s:else>
			</s:else>
		</div>
	
		<s:if test="filter.showTitleName">
			<div class="filterOption">
				<s:text name="global.Resource" /> 
				<s:textfield name="filter.titleName" cssClass="forms" size="18" />
			</div>
		</s:if>

		<s:if test="filter.showAccountName">
			<div class="filterOption">
				<s:textfield name="filter.accountName" cssClass="forms" size="18" />
			</div>
		</s:if>
	
		<s:if test="filter.showStatus">
			<div class="filterOption" id="status">
				<a href="#" class="filterBox"><s:text name="global.Status" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.statusList" listValue="%{getText(i18nKey)}" multiple="true" cssClass="forms" name="filter.status" />
					<br />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showType">
			<div class="filterOption" id="type">
				<a href="#" class="filterBox"><s:text name="global.Type" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.typeList" multiple="true" cssClass="forms" name="filter.type"/>
					<br />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showOpen">
			<div class="filterOption">
				<span><s:text name="global.Status" /> =</span>
				<s:select cssClass="forms" list="#{1:getText('Filters.status.Open'),0:getText('Filters.status.Closed')}" name="filter.open" />
			</div>
		</s:if>
		
		<s:if test="filter.showRequestStatus">
			<div class="filterOption">
				<span><s:text name="global.Status" /> =</span>
				<s:select cssClass="forms" list="@com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus@values()" listKey="name()" listValue="getText(getI18nKey())" name="filter.requestStatus" />
			</div>
		</s:if>
		
		<s:if test="filter.showAccountLevel">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.AccountLevel" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span><br />
				<span class="clearLink q_box select">
					<s:select list="#{'Full':getText('Filters.status.Full'), 'ListOnly':getText('Filters.status.ListOnly')}" cssClass="forms" name="filter.accountLevel" multiple="true" size="3" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showAddress">
			<br clear="all" />
			<div class="filterOption">
				<span class="q_status"><s:text name="global.Address" />:</span>
				<s:textfield name="filter.city" cssClass="forms" size="15"  />
				<s:textfield name="filter.zip" cssClass="forms" size="5"  />
			</div>
		</s:if>
	
		<s:if test="filter.showLocation">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.StateProvinceCountry" /></a> = 
				<span class="q_status">ALL</span>
				<br />
				<span class="clearLink q_box select"> 
					<s:textfield rel="Location" name="filter.location" cssClass="tokenAuto" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showTaxID">
			<div class="filterOption">
				<s:textfield name="filter.taxID" cssClass="forms" size="9" title="%{getText('Filters.MustBe9Digits')}" />
			</div>
		</s:if>
	
		<s:if test="filter.showTrade">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.Trade" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:textfield rel="Trade/true" name="filter.trade" cssClass="tokenAuto" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
					<s:radio 
						list="#{2:getText('Filters.status.Any'),1:getText('ContractorList.label.SelfPerformed'),0:getText('ContractorList.label.SubContracted')}" 
						name="filter.showSelfPerformedTrade"
						theme="pics"
						cssClass="inline"
					/>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showMinorityOwned">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.SupplierDiversity" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span><br />
				<span class="clearLink q_box select">
					<s:select list="filter.minorityQuestions" cssClass="forms" name="filter.minorityQuestion" multiple="true" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showFlagStatus">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.FlagStatus" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.flagStatusList" listValue="%{getText(i18nKey)}" cssClass="forms" name="filter.flagStatus" multiple="true" size="3" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showWorkStatus">
			<div class="filterOption">
				<a href="#"><s:text name="Filters.label.WorkStatus" /></a> =
				<s:select list="#{'Y':getText('YesNo.Yes'),'N':getText('YesNo.No'),'P':getText('Filters.status.Pending')}" headerKey="" headerValue="Any" cssClass="forms" name="filter.workStatus" />
			</div>
		</s:if>
		
		<s:if test="filter.showWaitingOn">
			<div class="filterOption">
				<s:select list="filter.waitingOnList" listValue="%{getText(value.i18nKey)}" headerKey="" headerValue="- %{getText('global.WaitingOn')} -" cssClass="forms" name="filter.waitingOn" />
			</div>
		</s:if>
	
		<s:if test="filter.showRiskLevel">
			<div class="filterOption" id="risklevel">
				<a href="#" class="filterBox"><s:text name="global.SafetyRisk" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="#{1:getText('LowMedHigh.Low'), 2:getText('LowMedHigh.Med'), 3:getText('LowMedHigh.High')}" cssClass="forms" name="filter.riskLevel" multiple="true" size="3" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showProductRiskLevel">
			<div class="filterOption" id="productRisklevel">
				<a href="#" class="filterBox"><s:text name="global.ProductRisk" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="#{1:getText('LowMedHigh.Low'), 2:getText('LowMedHigh.Med'), 3:getText('LowMedHigh.High')}" cssClass="forms" name="filter.productRiskLevel" multiple="true" size="3" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showService">
			<div class="filterOption" id="service">
				<a href="#" class="filterBox"><s:text name="Filters.label.Services" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="#{'Onsite':getText('ContractorType.Onsite'), 'Offsite':getText('ContractorType.Offsite'), 'Material Supplier':getText('ContractorType.Material')}" cssClass="forms" name="filter.service" multiple="true" size="3" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showOpertorTagName && filter.operatorTagNamesList.size() > 0">
			<div class="filterOption">
				<s:select list="filter.operatorTagNamesList" cssClass="forms" name="filter.operatorTagName" listKey="id" listValue="tag" headerKey="0" headerValue="- %{getText('Filters.header.Tag')} -" />
			</div>
		</s:if>
	
		<s:if test="filter.showHandledBy">
			<div class="filterOption">
				<s:select list="filter.handledByList" listValue="%{getText(i18nKey)}" headerKey="" headerValue="- %{getText('Filters.header.FollowUpBy')} -" cssClass="forms" name="filter.handledBy" />
			</div>
		</s:if>
	
		<s:if test="filter.showCcOnFile">
			<div class="filterOption">
				<s:select list="#{'1':getText('YesNo.Yes'),'0':getText('YesNo.No')}" headerKey="2" headerValue="- %{getText('global.CreditCard')} -" cssClass="forms" name="filter.ccOnFile" />
			</div>
		</s:if>
	
		<s:if test="filter.showContractor">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.Contractor" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:textfield rel="Contractors" name="filter.contractor" cssClass="tokenAuto" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showOperator">
			<div class="filterOption">
				<s:if test="filter.showOperatorSingle">
					<s:select list="filter.operatorList" cssClass="forms" name="filter.operatorSingle" listKey="id" 
						listValue="name" headerKey="0" headerValue="- %{getText('global.Operator')} -" />
				</s:if>
				<s:else>
					<a href="#" class="filterBox"><s:text name="global.Operators" /></a> =
					<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
					<br />
					<span class="clearLink q_box select">
						<s:textfield rel="Operator" name="filter.operator" cssClass="tokenAuto" />
						<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
						<s:radio 
							list="#{'false':getText('JS.Filters.status.All'),'true':getText('Filters.status.Any')}" 
							name="filter.showAnyOperator"
							theme="pics"
							cssClass="inline"
						/>
					</span>
				</s:else>
			</div>
		</s:if>
	
		<s:if test="filter.showWaCategories">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Audit.header.Categories" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.waCategoryList" cssClass="forms" name="filter.waCategories" listKey="id" listValue="name" multiple="true" size="25" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showWaAuditTypes">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="AuditType" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.waAuditTypesList" cssClass="forms" name="filter.waAuditTypes" listKey="id" listValue="name" multiple="true" size="5" /><br />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showCaoOperator">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.Operators" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:textfield rel="Operator" name="filter.caoOperator" cssClass="tokenAuto" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
					<s:radio 
						list="#{'false':getText('JS.Filters.status.All'),'true':getText('Filters.status.Any')}" 
						name="filter.showAnyCAOOperator"
						theme="pics"
						cssClass="inline"
					/>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showAuditType">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.PQFType" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.pQFTypeList" cssClass="forms" name="filter.pqfTypeID" listKey="id" listValue="name" multiple="true" size="5" />
					<br />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showAuditType">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="AuditType" /></a> =
				<span><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.auditTypeList" cssClass="forms" name="filter.auditTypeID" listKey="id" listValue="name" multiple="true" size="5" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showPolicyType">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.PolicyType" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select list="filter.policyTypeList" cssClass="forms" name="filter.auditTypeID" listKey="id" listValue="name" multiple="true" size="5" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showAuditStatus">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.Status" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select id="form1_auditStatus" list="filter.auditStatusList" cssClass="forms" name="filter.auditStatus" multiple="true" size="5" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showCaowStatus">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.StatusWorkflow" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select id="form1_caowStatus" list="filter.auditStatusList" listValue="%{getText(i18nKey)}" cssClass="forms" name="filter.caowStatus" multiple="true" size="5" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showAuditor">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.SafetyProfessionals" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select name="filter.auditorId" cssClass="forms" list="auditorList" listKey="id" listValue="name" multiple="true" size="5" id="form1_auditorId" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showClosingAuditor">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.ClosingSafetyProfessionals" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select name="filter.closingAuditorId" cssClass="forms" list="auditorList" listKey="id" listValue="name" multiple="true" size="5" id="form1_closingAuditorId" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showAccountManager">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.AccountManager" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select name="filter.accountManager" cssClass="forms" list="accountManagers" listKey="id" listValue="name" multiple="true" size="5" id="form1_accountManager" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showConAuditor">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.CSR" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select name="filter.conAuditorId" cssClass="forms" list="auditorList" listKey="id" listValue="name" multiple="true" size="5" id="form1_conAuditorId" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showPolicyChangedDate">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.PolicyChangedDate" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" name="filter.policyChangedDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" name="filter.policyChangedDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showConLicense">
			<br clear="all" />
			<div class="filterOption">
				<s:select name="filter.validLicense" list="#{'Valid':getText('Filters.status.Valid'),'UnValid':getText('Filters.status.Invalid'),'All':getText('JS.Filters.status.All')}" cssClass="forms" />
			</div>
		</s:if>
		
		<s:if test="filter.showCreatedDate">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.CreatedDate" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" name="filter.createdDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" name="filter.createdDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showPercentComplete">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.PercentComplete" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield name="filter.percentComplete1" cssClass="forms" size="12"  />
					<s:text name="Filters.label.To" />: <s:textfield name="filter.percentComplete2" cssClass="forms" size="12" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showPercentVerified">
			<div class="filterOption"><a href="#" class="filterBox"><s:text name="Filters.label.PercentVerified" /></a> = 
			<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
			<br />
			<span class="clearLink q_box textfield">
				<s:textfield name="filter.percentVerified1" cssClass="forms" size="12" />
				<s:text name="Filters.label.To" />: <s:textfield name="filter.percentVerified2" cssClass="forms" size="12" />
				<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
			</span></div>
		</s:if>
	
		<s:if test="filter.showUnConfirmedAudits">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.unScheduledAudits" />
					<s:text name="Filters.label.SearchUnconfirmedAudits" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showAssignedCon">
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.assignedCon" />
					<s:text name="Filters.label.SearchAssignedContractors" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showExpiredLicense">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.conExpiredLic" />
					<s:text name="Filters.label.SearchExpiredLicenses" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showInParentCorporation">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.inParentCorporation" />
					<s:text name="Filters.label.LimitContractorsWorkingInParent" />
				</label>
			</div>
		</s:if>
		
		<s:if test="filter.showAuditFor">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.ForYear" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select id="form1_auditFor" list="filter.yearList" cssClass="forms" name="filter.auditFor" multiple="true" size="5" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showShaType">
			<br clear="all" />
			<div class="filterOption">
				<s:select list="filter.oshaTypesList" cssClass="forms" name="filter.shaType" headerKey="" headerValue="- %{getText('Filters.header.OSHAType')} -" />
			</div>
		</s:if>
	
		<s:if test="filter.showShaTypeFlagCriteria">
			<br clear="all" />
			<div class="filterOption">
				<s:select list="filter.oshaTypesList" cssClass="forms" name="filter.shaTypeFlagCriteria" headerKey="" headerValue="- %{getText('Filters.header.OSHAType')} -" />
			</div>
		</s:if>
		
		<s:if test="filter.showShaLocation">
			<div class="filterOption">
				<s:select list="#{'Corporate':getText('global.Corporate'), 'Division':getText('Filters.status.Division'),'Region':getText('Filters.status.Region'),'Site':getText('Filters.status.Site')}" headerKey="" headerValue="- %{getText('Filters.header.OshaLocation')} -" cssClass="forms" name="filter.shaLocation" />
			</div>
		</s:if>
	
		<s:if test="filter.showVerifiedAnnualUpdates">
			<div class="filterOption">
				<s:select list="#{'1':getText('global.Verified'),'2':getText('Filters.status.Unverified')}" headerKey="0" headerValue="- %{getText('Filters.header.VerifiedStats')} -" cssClass="forms" name="filter.verifiedAnnualUpdate" />
			</div>
		</s:if>
	
		<s:if test="filter.showEmrRange">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.EMR" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield name="filter.minEMR" cssClass="forms" size="12"  />
					<s:text name="Filters.label.To" />: <s:textfield name="filter.maxEMR" cssClass="forms" size="12"  />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showTrirRange">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.TRIR" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span><br />
				<span class="clearLink q_box textfield">
					<s:textfield name="filter.minTRIR" cssClass="forms" size="12"  />
					<s:text name="Filters.label.To" />: <s:textfield name="filter.maxTRIR" cssClass="forms" size="12"  />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showScoreRange">
			<br clear="all" />
			<div class="filterOption">
				<a href="#" class="filterbox"><s:text name="ContractorAccount.score" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield name="filter.scoreMin" cssClass="forms" size="12" />
					<s:text name="Filters.label.To" />: <s:textfield name="filter.scoreMax" cssClass="forms" size="12"  />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showIncidenceRate">
			<div class="filterOption">
				<s:text name="Filters.label.IncidenceRate" />
				<s:textfield name="filter.incidenceRate" cssClass="forms" size="10" />
				<s:text name="Filters.label.To" />
				<s:textfield name="filter.incidenceRateMax" cssClass="forms" size="10" />
			</div>
		</s:if>
	
		<s:if test="filter.showIncidenceRateAvg">
			<div class="filterOption">
				<s:text name="Filters.label.3YearAverage" />
				<s:textfield name="filter.incidenceRateAvg" cssClass="forms" size="10" />
				<s:text name="Filters.label.To" />
				<s:textfield name="filter.incidenceRateAvgMax" cssClass="forms" size="10" />
			</div>
		</s:if>
	
		<s:if test="filter.showCohsStats">
			<div class="filterOption">
				<s:text name="Filters.label.Cad7" />
				<s:textfield name="filter.cad7" cssClass="forms" size="10" />
				<s:text name="Filters.label.Neer" />
				<s:textfield name="filter.neer" cssClass="forms" size="10" />
			</div>
		</s:if>
	
		<s:if test="filter.showAMBest">
			<br clear="all" />
			<div class="filterOption">
				<s:select list="filter.aMBestRatingsList" cssClass="forms" name="filter.amBestRating" headerKey="0" headerValue="- %{getText('Filters.header.Rating')} -" />
				<s:select list="filter.aMBestClassList" cssClass="forms" name="filter.amBestClass" headerKey="0" headerValue="- %{getText('Filters.header.Class')} -" />
			</div>
		</s:if>
	
		<s:if test="filter.showRecommendedFlag">
			<div class="filterOption">
				<s:select list="filter.flagStatusList" listValue="%{getText(i18nKey)}" headerKey="" headerValue="- %{getText('Filters.header.PolicyCompliance')} -" cssClass="forms" name="filter.recommendedFlag" />
			</div>
		</s:if>
	
		<s:if test="filter.showBillingState">
			<div class="filterOption">
				<span>
					<s:text name="ContractorAccount.billingState" />:
					<s:radio 
						list="#{'Activations':getText('Filters.status.Activations'),'Renewals':getText('Filters.status.Renewals'),'Upgrades':getText('Filters.status.Upgrades'),'All':getText('JS.Filters.status.All')}" 
						name="filter.billingState"
						theme="pics"
						cssClass="inline" 
					/>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showEmailTemplate">
			<br clear="all" />
			<div class="filterOption">
				<s:select list="filter.emailTemplateList" headerKey="0" headerValue="- %{getText('EmailTemplate')} -" cssClass="forms" name="filter.emailTemplate" listKey="id" listValue="templateName" />
			</div>
			<div class="filterOption">
				<s:text name="Filters.label.EmailSentDate" />
				<s:textfield cssClass="forms datepicker" size="10" id="form1_emailSentDate" name="filter.emailSentDate" />
			</div>
		</s:if>
		
		<s:if test="filter.showRegistrationDate">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.RegistrationDate" /></a>
				<span class="q_status">= <s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" name="filter.registrationDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" name="filter.registrationDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showExpiredDate">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.ExpiredDate" /></a>
				<span class="q_status">= <s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" name="filter.expiredDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" name="filter.expiredDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showFollowUpDate">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.FollowUpDate" /></a>
				<span class="q_status">= <s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:text name="Filters.label.Before" />: <s:textfield cssClass="forms datepicker" size="10" name="filter.followUpDate" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showCreationDate">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.CreationDate" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" id="form1_creationDate1" name="filter.creationDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" id="form1_creationDate2" name="filter.creationDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showViewAll">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.viewAll" />
					<s:text name="Filters.label.ShowAllRegistrationRequests" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showInvoiceDueDate">
			<br clear="all">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.InvoiceDueDate" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" id="form1_invoiceDueDate1" name="filter.invoiceDueDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" id="form1_invoiceDueDate2" name="filter.invoiceDueDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showCaoStatusChangedDate">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.StatusChangedDate" /> </a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" name="filter.statusChangedDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" name="filter.statusChangedDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showCaowUpdateDate">
			<br clear="all">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="Filters.label.WorkflowStatusChangedDate" /></a>
				<span class="q_status">= <s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box textfield">
					<s:textfield cssClass="forms datepicker" size="10" name="filter.caowUpdateDate1" />
					<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" name="filter.caowUpdateDate2" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
	
		<s:if test="filter.showDeactivationReason">
			<div class="filterOption">
				<s:select list="filter.deactivationReasons" headerKey=" " headerValue="- %{getText('Filters.header.DeactivationReason')} -" listKey="key" listValue="value" cssClass="forms" name="filter.deactivationReason" />
			</div>
		</s:if>
		
		<s:if test="filter.showInsuranceLimits">
			<div class="filterOption">
				<a href="#" onclick="showInsuranceTextBoxes('form1_insuranceLimits'); return false;"><s:text name="Filters.label.InsuranceLimits" /></a>
				<span id="form1_insuranceLimits_query">= <s:text name="JS.Filters.status.All" /></span>
				<br />
				<div id="form1_insuranceLimits" style="display: none"class="clearLink q_box">
					<table class="insuranceLimits">
						<tr>
							<td class="clearLink" title="<s:text name="Filters.help.GLEachOccurence" />">
								<s:text name="Filters.label.GLEachOccurence" />:
							</td>
							<td>
								$<s:textfield id="form1_insuranceLimits1" cssClass="forms" title="%{getText('Filters.help.EnterANumber')}" name="filter.glEachOccurrence" onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,1);" size="15" />
								<span id="error1" class="redMain"></span>
							</td>
						</tr>
						<tr>
							<td class="clearLink" title="<s:text name="Filters.help.GLGeneralAggregate" />">
								<s:text name="Filters.label.GLGeneralAggregate" />:
							</td>
							<td>
								$<s:textfield id="form1_insuranceLimits2" cssClass="forms" title="%{getText('Filters.help.EnterANumber')}" name="filter.glGeneralAggregate" onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,2);" size="15" />
								<span id="error2" class="redMain"></span>
							</td>
						</tr>
						<tr>
							<td class="clearLink" title="<s:text name="Filters.help.ALCombinedSingle" />">
								<s:text name="Filters.label.ALCombinedSingle" />:
							</td>
							<td>
								$<s:textfield id="form1_insuranceLimits3" cssClass="forms" title="%{getText('Filters.help.EnterANumber')}" name="filter.alCombinedSingle" onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,3);" size="15" />
								<span id="error3" class="redMain"></span>
							</td>
						</tr>
						<tr>
							<td class="clearLink" title="<s:text name="Filters.help.WCEachAccident" />">
								<s:text name="Filters.label.WCEachAccident" />:
							</td>
							<td>
								$<s:textfield id="form1_insuranceLimits4" cssClass="forms" title="%{getText('Filters.help.EnterANumber')}" name="filter.wcEachAccident" onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,4);" size="15" />
								<span id="error4" class="redMain"></span>
							</td>
						</tr>
						<tr>
							<td class="clearLink" title="<s:text name="Filters.help.EXEachOccurence" />">
								<s:text name="Filters.label.EXEachOccurence" />:
							</td>
							<td>
								$<s:textfield id="form1_insuranceLimits5" cssClass="forms" title="%{getText('Filters.help.EnterANumber')}" name="filter.exEachOccurrence" onfocus="clearInsuranceText(this);" onblur="resetEmptyField(this);" onkeyup="isNumber(this,5);" size="15" />
								<span id="error5" class="redMain"></span>
							</td>
						</tr>
					</table>
					
					<a class="clearLink" href="#" onclick="clearInsuranceTextFields('form1_insuranceLimits'); return false;"><s:text name="Filters.status.Clear" /></a>
				</div>
			</div>
		</s:if>
		
		<s:if test="filter.showQuestionAnswer">
			<br clear="all">
			<div class="filterOption">
				<a href="#" class="filterbox"><s:text name="Filters.label.Questions" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select name="filter.questionIds" cssClass="forms" list="filter.questionsByAuditList" listKey="id" listValue="name" multiple="true" size="23"/>
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
			<br clear="all">
			<div class="filterOption">
				<s:text name="Filters.label.Answer" />: <s:textfield name="filter.answer" />
			</div>
		</s:if>
	
		<s:if test="filter.showConWithPendingAudits">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.pendingPqfAnnualUpdate" />
					<s:text name="Filters.label.ShowContractorsPendingPQFAU" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showNotRenewingContractors">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.notRenewingContractors" />
					<s:text name="Filters.label.ShowContractorsNotRenew" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showContractorsWithPendingMembership">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.contractorsWithPendingMembership" />
					<s:text name="Filters.label.ShowContractorsUnpaidMembership" />
				</label>
			</div>
		</s:if>
		
		<s:if test="filter.showPrimaryInformation">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.primaryInformation" />
					<s:text name="Filters.label.ShowContactInfo" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showTradeInformation">
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.tradeInformation" />
					<s:text name="Filters.label.ShowTradeInfo" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showOQ">
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.oq" />
					<s:text name="Filters.label.ShowOQContractors" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showHSE">
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.hse" />
					<s:text name="Filters.label.ShowRequiresCompetencyReviews" />
				</label>
			</div>
		</s:if>
		
		<s:if test="filter.showSoleProprietership">
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.soleProprietership" />
					<s:text name="Filters.label.ShowSoleProprietors" />
				</label>
			</div>
		</s:if>
		
		<pics:permission perm="DevelopmentEnvironment">
			<div class="filterOption">
				<label><s:text name="Filters.label.QueryAPI" /></label>
				<s:textfield name="filter.customAPI" />
			</div>
		</pics:permission>
	
		<s:if test="filter.showAuditCreationFlagChanges || filter.showAuditStatusFlagChanges">
			<br clear="all" />
			<s:text name="Filters.label.ExpectedFlagChanges" />:
		</s:if>
	
		<s:if test="filter.showAuditCreationFlagChanges">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.auditCreationFlagChanges" />
					<s:text name="Filters.label.ShowFlagChangesRelatedNewAudits" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showCaoChangesFlagChanges">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.caoChangesFlagChanges" />
					<s:text name="Filters.label.ShowCaoDetails" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showAuditStatusFlagChanges">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.auditStatusFlagChanges" />
					<s:text name="Filters.label.ShowFlagChangesAuditStatusChanges" />
				</label>
			</div>
		</s:if>
	
		<s:if test="filter.showAuditQuestionFlagChanges">
			<br clear="all" />
			<div class="filterOption">
				<label>
					<s:checkbox name="filter.auditQuestionFlagChanges" />
					<s:text name="Filters.label.ShowAuditQuestionFlagChanges" />
				</label>
			</div>
		</s:if>
		
		<s:if test="filter.showCaowDetail">
			<br clear="all" />
			<div class="filterOption">
				<s:radio 
					list="filter.caowDetailList" 
					name="filter.caowDetailLevel"
					theme="pics"
					cssClass="inline" 
				/>
			</div>
		</s:if>
		
		<s:if test="filter.showAuditorType && !isIndepenentAuditor()">
			<br clear="all" />
			<div class="filterOption">
				<s:radio 
					list="filter.auditorTypeList" 
					name="filter.auditorType"
					theme="pics"
					cssClass="inline" 
				/>
			</div>
		</s:if>
	
		<br clear="all" />
		<div class="alphapaging">
			<s:property value="report.startsWithLinksWithDynamicForm" escape="false" />
		</div>
	</s:form>
</div>