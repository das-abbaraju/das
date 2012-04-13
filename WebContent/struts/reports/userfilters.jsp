<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<div id="search"><s:form id="form1" action="%{filter.destinationAction}" cssStyle="background-color: #F4F4F4;">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div><s:if test="filter.allowMailMerge">
		<button type="submit" id="write_email_button" name="button" value="Write Email" onclick="clickSearchSubmit('form1')"
			class="picsbutton positive" style="display: none"><s:text name="Filters.button.WriteEmail" /></button>
		<button type="button" name="button" value="Find Recipients"
			onclick="clickSearch('form1')" class="picsbutton"><s:text name="Filters.button.FindRecipients" /></button>
	</s:if> <s:else>
		<button type="submit" name="button" value="Search"
			onclick="return clickSearch('form1');" class="picsbutton positive"><s:text name="button.Search" /></button>
		<br clear="all" />
	</s:else></div>
	
	<s:if test="filter.showContact">
		<div class="filterOption"><s:text name="Filters.label.ContactName" />: 
			<s:textfield name="filter.contactName" cssClass="forms" size="15" onfocus="clearText(this)" />
		</div>
	</s:if>

	<s:if test="filter.showUser">
		<div class="filterOption"><s:text name="Filters.label.UserName" />:
			<s:textfield name="filter.userName" cssClass="forms" size="15" onfocus="clearText(this)" />
		</div>
	</s:if>

	<s:if test="filter.showCompanyName">
		<div class="filterOption"><s:text name="global.CompanyName" />:
			<s:textfield name="filter.companyName" cssClass="forms" size="15" onfocus="clearText(this)" />
		</div>
	</s:if>

	<s:if test="filter.showPhone">
		<div class="filterOption"><s:text name="Filters.label.PhoneNumber" />:
			<s:textfield name="filter.phoneNumber" cssClass="forms" size="15" onfocus="clearText(this)" title="%{getText('Filters.MustBe9Digits')}" />
		</div>
	</s:if>

	<s:if test="filter.showEmail">
		<div class="filterOption"><s:text name="Filters.label.EmailAddress" />:
			<s:textfield name="filter.emailAddress" cssClass="forms" size="15" onfocus="clearText(this)" />
		</div>
	</s:if>
	
	<s:if test="filter.showActive">
		<div class="filterOption"><s:text name="AccountStatus.Active" />: 
			<s:select cssClass="forms" list="#{'Yes':getTextNullSafe('YesNo.Yes'),'No':getTextNullSafe('YesNo.No'),'':getTextNullSafe('JS.Filters.status.All')}" name="filter.active" />
		</div>
	</s:if>
	
	<s:if test="filter.showCompanyStatus">
		<div class="filterOption"> 
			<a href="#" onclick="toggleBox('form1_company_status'); return false;"><s:text name="Filters.label.CompanyStatus" /></a> =
			<span id="form1_company_status_query"><s:text name="JS.Filters.status.All" /></span><br />
			<span id="form1_company_status_select" style="display: none" class="clearLink">
				<s:select list="filter.companyStatusList" listValue="%{getText(i18nKey)}" multiple="true" cssClass="forms" 
					name="filter.companyStatus" id="form1_company_status" /><br />
				<a class="clearLink" href="#" onclick="clearSelected('form1_company_status'); return false;"><s:text name="Filters.status.Clear" /></a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showCompanyType">
		<div class="filterOption"> 
			<a href="#" onclick="toggleBox('form1_company_type'); return false;"><s:text name="Filters.label.CompanyType" /></a> =
			<span id="form1_company_type_query"><s:text name="JS.Filters.status.All" /></span><br />
			<span id="form1_company_type_select" style="display: none" class="clearLink">
				<s:select list="#{'Contractor':getTextNullSafe('global.Contractor'),'Operator':getTextNullSafe('global.Operator'),'Corporate':getTextNullSafe('global.Corporate')}" multiple="true" 
					cssClass="forms" name="filter.companyType" id="form1_company_type" /><br />
				<a class="clearLink" href="#" onclick="clearSelected('form1_company_type'); return false;"><s:text name="Filters.status.Clear" /></a>
			</span>
		</div>
	</s:if>
	
	<br clear="all" />
	<div class="alphapaging"><s:property value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>