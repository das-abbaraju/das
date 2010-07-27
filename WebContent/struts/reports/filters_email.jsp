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
		onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');"
		class="picsbutton positive">Search</button></div>

	<s:if test="filter.showContractor">
		<div class="filterOption"><s:textfield name="filter.accountName" cssClass="forms" size="18"
			onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showStatus">
		<div class="filterOption">
			<a href="#"	onclick="toggleBox('form1_status'); return false;">Status</a> =
			<span id="form1_status_query">ALL</span><br />
			<span id="form1_status_select" style="display: none" class="clearLink">
				<s:select list="filter.statusList" multiple="true" cssClass="forms"
					name="filter.status" id="form1_status" /><br />
				<script type="text/javascript">updateQuery('form1_status');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_status'); return false;">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showTemplateName">
		<div class="filterOption">
			<a href="#"	onclick="toggleBox('form1_template'); return false;">Template Name</a> =
			<span id="form1_template_query">ALL</span><br />
			<span id="form1_template_select" style="display: none" class="clearLink">
				<s:select list="filter.templateList" multiple="true" cssClass="forms" listKey="templateName"
					listValue="templateName" name="filter.templateName" id="form1_template" /><br />
				<script type="text/javascript">updateQuery('form1_template');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_template'); return false;">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showSentDate">
		<div class="filterOption">
			<a href="#"	onclick="showTextBox('form1_sentDate'); return false;">Sent Date</a>
			<span id="form1_sentDate_query">= ALL</span><br />
			<span id="form1_sentDate" style="display: none" class="clearLink">
				<s:textfield cssClass="forms datepicker" size="10" id="form1_sentDate1" 
					name="filter.sentDateStart" />
				To:<s:textfield cssClass="forms datepicker" size="10" id="form1_sentDate2"
					name="filter.sentDateEnd" />
				<script type="text/javascript">textQuery('form1_sentDate');</script>
				<br />
				<a class="clearLink" href="#" onclick="clearTextField('form1_sentDate'); return false;">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showToAddress">
		<div class="filterOption"><s:textfield name="filter.toAddress" cssClass="forms" size="18"
			onfocus="clearText(this)" /></div>
	</s:if>

	<pics:permission perm="DevelopmentEnvironment">
		<div class="filterOption"><label>Query API</label> <s:textfield
			name="filter.customAPI" /></div>
	</pics:permission>

	<br clear="all" />
</s:form></div>
