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
	<br clear="all"/>
</s:form>
</div>

<br clear="all"/>

<table class="report">
	<thead>
		<tr>
			<td>User</td>
			<td>Account</td>
			<td>Renew <br/>Date</td>
			<td>Role</td>
			<td>Weight(%)</td>
			<td>Class</td>
			<td>Start Date</td>
			<td>End Date</td>
			<td>Weighted Registrations This Month</td>
			<td>Weighted Registrations Last Month</td>
			<td>Total Registrations</td>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td><s:property value="get('userName')" /></td>
			<td><a href="FacilitiesEdit.action?id=<s:property value="get('accountID')"/>&type=<s:property value="get('type')"/>"><s:property value="get('accountName')" /></a></td>
			<td><s:date name="get('creationDate')" format="MM/dd"/></td>
			<td><s:property value="@com.picsauditing.actions.users.UserAccountRole@getDesc(get('role'))"/></td>
			<td><s:property value="get('ownerPercent')" /></td>
			<td><s:if test="get('doContractorsPay').toString() == 'Yes'">
					<s:if test="get('audited') == 1">Audited</s:if>
				</s:if>
				<s:else>Free</s:else>
				<s:if test="get('audited') == null">Non-Audited</s:if>
			</td>
			<td><s:date name="get('startDate')" format="MM/dd/yy"/></td>
			<td><s:date name="get('endDate')" format="MM/dd/yy"/></td>
			<td><s:property value="@java.lang.Math@round((get('regisThisMonth')*get('ownerPercent'))/100)" /> Credited 
			 <br/>(<s:property value="get('regisThisMonth')" /> Total) </td>
			<td><s:property value="@java.lang.Math@round((get('regisLastMonth')*get('ownerPercent'))/100)" /> Credited 
			 <br/>(<s:property value="get('regisLastMonth')" /> Total)</td>
			<td><s:property value="get('totalCons')" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
<div id="caldiv2" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

