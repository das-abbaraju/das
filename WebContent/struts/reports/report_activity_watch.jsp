<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Activity Watch</title>
<s:include value="reportHeader.jsp" />
<style type="text/css">
table.report {
	margin-right: 10px;
}

#search {
	margin-bottom: 10px;
}
</style>
</head>
<body>
<h1>Contractor Activity Watch</h1>

<div id="search"><s:form>
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search" class="picsbutton positive">Search</button>
	</div>
	
	<div class="filterOption">
		<s:select list="watched" listKey="contractor.id" listValue="contractor.name" name="conID" headerKey="0" headerValue="- Contractor -"></s:select>
	</div>
	
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_activity'); return false;">Activity Type</a> =
		<span id="form1_activity_query">ALL</span><br />
		<span id="form1_activity_select" style="display: none" class="clearLink">
			<s:select list="activityTypes" multiple="true" cssClass="forms" name="atype" id="form1_activity" /><br />
			<script type="text/javascript">updateQuery('form1_activity');</script>
			<a class="clearLink" href="#" onclick="clearSelected('form1_activity'); return false;">Clear</a>
		</span>
	</div>
	
	<br clear="all" />
</s:form></div>

<table>
	<tr>
		<td>
			<table class="report">
				<thead>
					<tr>
						<th>Contractor</th>
						<th>Remove</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="watched">
						<tr>
							<td><a href="ContractorView.action?id=<s:property value="contractor.id" />"><s:property value="contractor.name" /></a></td>
							<td class="center"><a href="#" onclick="return false;" class="remove"></a></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</td><td>
			<table class="report">
				<thead>
					<tr>
						<th>Contractor</th>
						<th>Activity Details</th>
						<th>Activity Date</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="data" status="stat">
					<tr>
						<td><s:property value="get('name')" /></td>
						<td><a href="<s:property value="get('url')" />"><s:property value="get('body')" /></a></td>
						<td><span title="<s:date name="get('activityDate')" nice="true" />"><s:date name="get('activityDate')" format="MM/dd/yyyy HH:mm:ss" /></span></td>
					</tr>
				</s:iterator>
				</tbody>
			</table>
		</td>
	</tr>
</table>

</body>
</html>
