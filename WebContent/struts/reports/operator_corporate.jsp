<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Accounts Report</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>Manage <s:property value="accountType"/> Accounts</h1>
<s:if test="canEditCorp">
	<div><a href="FacilitiesEdit.action?type=Corporate" class="add">Create New Corporate</a></div>
</s:if>
<s:if test="canEditOp">
	<div><a href="FacilitiesEdit.action?type=Operator" class="add">Create New Operator</a></div>
</s:if>	
<s:if test="canEditAssessment">
	<div><a href="AssessmentCenterEdit.action" class="add">Create New Assessment Center</a></div>
</s:if>

<div id="search">
	<s:form id="form1">
		<s:hidden name="filter.ajax" />
		<s:hidden name="filter.destinationAction" />
		<s:hidden name="filter.allowMailMerge" />
		<s:hidden name="showPage" value="1" />
		<s:hidden name="filter.startsWith" />
		<s:hidden name="orderBy" />
		<s:hidden name="accountType" />
		
		<div>
			<button id="searchfilter" type="submit" name="button" value="Search"
				onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');"
				class="picsbutton positive">Search</button>
		</div>
		
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
		
		<s:if test="filter.showAddress">
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
		
		<s:if test="filter.showPrimaryInformation">
			<br clear="all" />
			<div class="filterOption"><label><s:checkbox
				name="filter.primaryInformation" /> Show Contact Info</label></div>
		</s:if>
	
		<s:if test="filter.showTradeInformation">
			<div class="filterOption"><label><s:checkbox
				name="filter.tradeInformation" /> Show Trade Info</label></div>
		</s:if>
	
		<pics:permission perm="DevelopmentEnvironment">
			<div class="filterOption"><label>Query API</label> <s:textfield
				name="filter.customAPI" /></div>
		</pics:permission>
		
		<br clear="all" />
		<div class="alphapaging">
			<s:property value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
	</s:form>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<td></td>
			<th>Name</th>
			<th>Type</th>
			<td>Status</td>
			<td>Industry</td>
			<s:if test="filter.primaryInformation">
				<td>Primary Contact</td>
				<td>Phone</td>
				<td>Email</td>
				<td>Address</td>
				<td>City</td>
				<td>State</td>
				<td>Country</td>
				<td>Zip</td>
			</s:if>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<s:if test="get('type') == 'Operator' || get('type') == 'Corporate'">
					<a href="FacilitiesEdit.action?id=<s:property value="get('id')"/>&type=<s:property value="get('type')"/>"
						rel="OperatorQuickAjax.action?id=<s:property value="get('id')"/>"
						class="operatorQuick account<s:property value="get('status')" />"
						title="<s:property value="get('name')" />"><s:property value="get('name')" /></a>
				</s:if>
				<s:else>
					<a href="AssessmentCenterEdit.action?id=<s:property value="get('id')"/>"
						class="account<s:property value="get('status')" />"
						title="<s:property value="get('name')" />"><s:property value="get('name')" /></a>
				</s:else>
			</td>
			<td><s:property value="get('type')"/></td>
			<td><s:property value="get('status')"/></td>
			<td><s:property value="get('industry')"/></td>
			<s:if test="filter.primaryInformation">
				<td><s:property value="get('contactname')"/></td>
				<td><s:property value="get('contactphone')"/></td>
				<td><s:property value="get('contactemail')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('state')"/></td>
				<td><s:property value="get('country')"/></td>
				<td><s:property value="get('zip')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>