<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>

<s:include value="../actionMessages.jsp" />

<div id="search"><s:form id="form1" action="%{filter.destinationAction}">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
	
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search"
			onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');"
			class="picsbutton positive"><s:text name="button.Search" /></button>
	</div>

	<s:if test="filter.showAccountName">
		<div class="filterOption"><s:textfield name="filter.accountName"
			cssClass="forms" size="18" onfocus="clearText(this)" /></div>
	</s:if>
	
	<s:if test="filter.showType">
		<div class="filterOption"><a href="#"
			onclick="toggleBox('form1_type'); return false;"><s:text name="global.Type" /></a> =
			<span id="form1_type_query"><s:text name="JS.Filters.status.All" /></span>
			<br />
			<span id="form1_type_select" style="display: none" class="clearLink">
				<s:select list="#{'Corporate':getText('global.Corporate'),'Operator':getText('global.Operator'),'Contractor':getText('global.Contractor')}" 
				multiple="true" cssClass="forms" name="filter.type" id="form1_type" />
				<br />
				<script type="text/javascript">updateQuery('form1_type');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_type'); return false;">
					<s:text name="Filters.status.Clear" />
				</a>
			</span>
		</div>
	</s:if>

	<s:if test="filter.showTaxID">
		<div class="filterOption">
			<s:textfield name="filter.taxID" cssClass="forms" onfocus="clearText(this)" value="- %{getText('Filters.status.EmployeeName')} -" />
		</div>
	</s:if>

	<s:if test="filter.showOperator">
		<br clear="all" />
		<div class="filterOption"><s:if test="filter.showOperatorSingle">
			<s:select list="filter.operatorList" cssClass="forms"
				name="filter.operatorSingle" listKey="id" listValue="name" headerKey="0"
				headerValue="- Operator -" />
		</s:if> <s:else>
			<a href="#" onclick="toggleBox('form1_operator'); return false;"><s:text name="global.Operators" /></a> =
		<span id="form1_operator_query"><s:text name="JS.Filters.status.All" /></span>
			<br />
			<span id="form1_operator_select" style="display: none"
				class="clearLink"> <s:select id="form1_operator"
				list="filter.operatorList" cssClass="forms" name="filter.operator"
				listKey="id" listValue="name" multiple="true" size="10" /> <script
				type="text/javascript">updateQuery('form1_operator');</script> <br />
			<a class="clearLink" href="#"
				onclick="clearSelected('form1_operator'); return false;"><s:text name="Filters.status.Clear" /></a> </span>
		</s:else></div>
	</s:if>

	<pics:permission perm="DevelopmentEnvironment">
		<div class="filterOption"><label><s:text name="Filters.label.QueryAPI" /></label> <s:textfield
			name="filter.customAPI" /></div>
	</pics:permission>

	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>

<div id="report_data">
<s:include value="employee_list_data.jsp"></s:include>
</div>

</body>
</html>
