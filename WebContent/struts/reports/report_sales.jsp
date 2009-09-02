<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Report Sales</title>
<s:include value="reportHeader.jsp" />

</head>
<body>
<h1>Report of Sales Representatives</h1>

<div id="search">
<s:form id="form1" method="post"
	cssStyle="background-color: #F4F4F4;"
	onsubmit="runSearch('form1')">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search" onclick="return clickSearch('form1');" class="picsbutton positive">Search</button>
	</div>
	<div class="filterOption"><s:select list="roleList"  
		headerKey="" headerValue="- Responsibility -" listValue="description" cssClass="forms" name="responsibility"/>
	</div>

	<pics:permission perm="UserRolePicsOperator" type="Edit">
		<div class="filterOption">
			<a href="#"
				onclick="toggleBox('form1_user'); return false;">Users</a> =
			<span id="form1_user_query">ALL</span><br />
			<span id="form1_user_select" style="display: none"
				class="clearLink"> <s:select id="form1_user" list="userList"
				cssClass="forms" name="accountUser" listKey="id" listValue="name"
				multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_user');</script>
			<br />
			<a class="clearLink" href="#"
				onclick="clearSelected('form1_user'); return false;">Clear</a> </span>
		</div>
	</pics:permission>
	
	<div class="filterOption">
		<a href="#"
			onclick="toggleBox('form1_operator'); return false;">Operators</a> =
		<span id="form1_operator_query">ALL</span><br />
		<span id="form1_operator_select" style="display: none"
			class="clearLink"> <s:select id="form1_operator" list="operatorList"
			cssClass="forms" name="operator" listKey="id" listValue="name"
			multiple="true" size="5" /> <script type="text/javascript">updateQuery('form1_operator');</script>
		<br />
		<a class="clearLink" href="#"
			onclick="clearSelected('form1_operator'); return false;">Clear</a> </span>
	</div>
	
	<div class="filterOption">
		<s:select list="monthsList" cssClass="forms" name="month" value="%{month}"/>
	</div>
	<br clear="all"/>
</s:form>
</div>

<br clear="all"/>
<s:if test="showSummary">
	<table class="report">
		<thead><tr><td>Summary For <s:property value="data.get(0).get('userName')"/></td>
				<td></td>
				<td>Accounts Managed</td>
				<td><s:property value="currentMonthName"/></td>
				<td><s:property value="previousMonthName"/></td>
				<td>Registrations to Date</td>
		</tr></thead>
		<tbody>
			<tr><th rowspan="2">Account Managers</th>
				<td>Audited</td>
				<td class="right"><s:property value="summaryData.accountManager.audited.cappedOperatorCount"/> Credited<br /> 
					(<s:property value="summaryData.accountManager.audited.accountsManaged"/> Total)</td>
				<td class="right"><s:property value="summaryData.accountManager.audited.thisMonth"/></td>
				<td class="right"><s:property value="summaryData.accountManager.audited.lastMonth"/></td>
				<td class="right"><s:property value="summaryData.accountManager.audited.toDate"/></td>
			</tr>		
			<tr>
				<td>Non-Audited</td>
				<td class="right"><s:property value="summaryData.accountManager.nonAudited.cappedOperatorCount"/> Credited<br /> 
					(<s:property value="summaryData.accountManager.nonAudited.accountsManaged"/> Total)</td>
				<td class="right"><s:property value="summaryData.accountManager.nonAudited.thisMonth"/></td>
				<td class="right"><s:property value="summaryData.accountManager.nonAudited.lastMonth"/></td>
				<td class="right"><s:property value="summaryData.accountManager.nonAudited.toDate"/></td>
			</tr>
			<tr><th rowspan="2">Sales Representatives</th>
				<td>Audited</td>
				<td class="right"><s:property value="summaryData.salesRep.audited.cappedOperatorCount"/> Credited<br /> 
					(<s:property value="summaryData.salesRep.audited.accountsManaged"/> Total)</td>
				<td class="right"><s:property value="summaryData.salesRep.audited.thisMonth"/></td>
				<td class="right"><s:property value="summaryData.salesRep.audited.lastMonth"/></td>
				<td class="right"><s:property value="summaryData.salesRep.audited.toDate"/></td>
			</tr>		
			<tr>
				<td>Non-Audited</td>
				<td class="right"><s:property value="summaryData.salesRep.nonAudited.cappedOperatorCount"/> Credited<br /> 
					(<s:property value="summaryData.salesRep.nonAudited.accountsManaged"/> Total)</td>
				<td class="right"><s:property value="summaryData.salesRep.nonAudited.thisMonth"/></td>
				<td class="right"><s:property value="summaryData.salesRep.nonAudited.lastMonth"/></td>
				<td class="right"><s:property value="summaryData.salesRep.nonAudited.toDate"/></td>
			</tr>		
		</tbody>
	</table>
</s:if>

<br clear="all"/>
<table class="report">
	<thead>
		<tr>
			<td>Employee</td>
			<td>Operator Account</td>
			<td>Renew <br/>Date</td>
			<td>Role</td>
			<td>Weight(%)</td>
			<td>Class</td>
			<td>Start Date</td>
			<td>End Date</td>
			<td>Weighted Registrations in <s:property value="currentMonthName"/></td>
			<td>Weighted Registrations in <s:property value="previousMonthName"/></td>
			<td>Registrations to Date</td>
		</tr>
	</thead>
	
	<s:iterator value="data" status="stat">
		<tr>
			<td><s:property value="get('userName')" /></td>
			<td><a href="FacilitiesEdit.action?id=<s:property value="get('accountID')"/>&type=<s:property value="get('type')"/>"><s:property value="get('accountName')" /></a></td>
			<td class="center"><s:date name="get('creationDate')" format="MM/dd"/></td>
			<td><s:property value="@com.picsauditing.actions.users.UserAccountRole@getDesc(get('role'))"/></td>
			<td class="right"><s:property value="get('ownerPercent')" /></td>
			<td><s:if test="get('doContractorsPay').toString() == 'Yes'">
					<s:if test="get('audited') == 1">Audited</s:if>
				</s:if>
				<s:else>Free</s:else>
				<s:if test="get('audited') == null">Non-Audited</s:if>
			</td>
			<td class="center"><s:date name="get('startDate')" format="MM/dd/yy"/></td>
			<td class="center"><s:date name="get('endDate')" format="MM/dd/yy"/></td>
			<td class="right"><s:property value="@java.lang.Math@round((get('regisThisMonth')*get('ownerPercent'))/100)" /> Credited
			 <br/>(<a href=""><s:property value="get('regisThisMonth')" /> Total</a>)
			 1<s:property value="urlCurrentMonth(data)" escape="false"/>
			 2<s:property value="getUrlCurrentMonth(data)" />
			 3<s:property value="%{getUrlCurrentMonth(data)}" escape="false"/>
			 
			 </td>
			<td class="right"><s:property value="@java.lang.Math@round((get('regisLastMonth')*get('ownerPercent'))/100)" /> Credited 
			 <br/>(<a href="<s:property value="getUrlCurrentMonth(data)" escape="false"/>"><s:property value="get('regisLastMonth')" /> Total</a>)</td>
			<td class="right"><a href=""><s:property value="get('totalCons')" /></a></td>
		</tr>
	</s:iterator>
	<tr>
		<td colspan="8" class="right" style="font-weight: bold;">Totals</td>
		<td class="right" style="font-weight: bold;"><s:property value="dataTotals.thisMonthCredited" /> Credited 
		 <br/>(<s:property value="dataTotals.thisMonthTotal" /> Total) </td>
		<td class="right" style="font-weight: bold;"><s:property value="dataTotals.lastMonthCredited" /> Credited 
		 <br/>(<s:property value="dataTotals.lastMonthTotal" /> Total)</td>
		<td class="right" style="font-weight: bold;"><s:property value="dataTotals.toDate" /></td>
	</tr>
	
</table>
</body>
</html>
