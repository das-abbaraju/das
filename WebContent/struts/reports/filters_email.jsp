<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<div id="search">
	<s:form id="form1" action="%{filter.destinationAction}">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="orderBy" />
	
	<div><button id="searchfilter" type="submit" name="button" value="Search"
		onclick="checkCountrySubdivisionAndCountry('form1_state','form1_country'); return clickSearch('form1');"
		class="picsbutton positive"><s:text name="button.Search" /></button></div>

	<s:if test="filter.showContractor">
		<div class="filterOption"><s:textfield name="filter.accountName" cssClass="forms" size="18"
			onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showStatus">
		<div class="filterOption">
			<a href="#"	onclick="toggleBox('form1_status'); return false;"><s:text name="global.Status" /></a> =
			<span id="form1_status_query"><s:text name="JS.Filters.status.All" /></span><br />
			<span id="form1_status_select" style="display: none" class="clearLink">
				<s:select list="filter.statusList" multiple="true" cssClass="forms"
					name="filter.status" id="form1_status" /><br />
				<a class="clearLink" href="#" onclick="clearSelected('form1_status'); return false;"><s:text name="Filters.status.Clear" /></a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showTemplateName">
		<div class="filterOption">
			<a href="#"	onclick="toggleBox('form1_template'); return false;"><s:text name="Filters.label.TemplateName" /></a> =
			<span id="form1_template_query"><s:text name="JS.Filters.status.All" /></span><br />
			<span id="form1_template_select" style="display: none" class="clearLink">
				<s:select list="filter.templateList" multiple="true" cssClass="forms" listKey="templateName"
					listValue="templateName" name="filter.templateName" id="form1_template" /><br />
				<a class="clearLink" href="#" onclick="clearSelected('form1_template'); return false;"><s:text name="Filters.status.Clear" /></a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showSentDate">
		<div class="filterOption">
			<a href="#"	onclick="showTextBox('form1_sentDate'); return false;"><s:text name="Filters.label.SentDate" /></a>
			<span id="form1_sentDate_query">= <s:text name="JS.Filters.status.All" /></span><br />
			<span id="form1_sentDate" style="display: none" class="clearLink">
				<s:textfield cssClass="forms datepicker" size="10" id="form1_sentDate1" 
					name="filter.sentDateStart" />
				<s:text name="Filters.label.To" />:<s:textfield cssClass="forms datepicker" size="10" id="form1_sentDate2"
					name="filter.sentDateEnd" />
				<br />
				<a class="clearLink" href="#" onclick="clearTextField('form1_sentDate'); return false;"><s:text name="Filters.status.Clear" /></a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showToAddress">
		<div class="filterOption"><s:textfield name="filter.toAddress" cssClass="forms" size="18"
			onfocus="clearText(this)" /></div>
	</s:if>

	<pics:permission perm="DevelopmentEnvironment">
		<div class="filterOption"><label><s:text name="Filters.label.QueryAPI" /></label> <s:textfield
			name="filter.customAPI" /></div>
	</pics:permission>

	<br clear="all" />
</s:form></div>
