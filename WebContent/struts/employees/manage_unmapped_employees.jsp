<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Employee Mapping</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<style type="text/css">
.hidden {
	display: none;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function add(row) {
	cancel();
	$('tr#' + row + ' .add').hide();
	$('tr#' + row + ' .hidden').show();
	$('#stageID').val(row);
}

function cancel() {
	$('.add').show();
	$('.edit').show();
	$('.hidden').hide();
	$('select.hidden').val(0);
	$('#stageID').val(0);
}
</script>
</head>
<body>
<h1><s:property value="account.name" /><span class="sub"><s:property value="subHeading" escape="false"/></span></h1>
<s:include value="../actionMessages.jsp"/>

<h2>Unmapped Employees</h2>
<s:form>
	<input type="hidden" name="id" value="<s:property value="account.id" />" />
	<input type="hidden" name="stageID" value="0" id="stageID" />
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Unmapped Employee</th>
				<th># of Assessment Results</th>
				<th>Match with PICS</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="unmapped.keySet()" id="staged" status="stat">
				<tr id="<s:property value="unmapped.get(#staged).get(0).id" />">
					<td><s:property value="#stat.count" /></td>
					<td><span class="name"><s:property value="unmapped.get(#staged).get(0).firstName" /> <s:property value="unmapped.get(#staged).get(0).lastName" /></span></td>
					<td class="right"><s:property value="unmapped.get(#staged).size()" /></td>
					<td class="center"><a href="#" class="add"
						onclick="add(<s:property value="unmapped.get(#staged).get(0).id" />); return false;"></a>
						<s:if test="employees.size() > 0">
							<s:select list="employees" cssClass="hidden" headerKey="0" headerValue="- Employees -"
								listKey="id" listValue="displayName" name="employeeID" />
							<input type="submit" value="Save" name="button" class="picsbutton positive hidden" />
						</s:if>
						<input type="submit" value="Add New Employee" name="button" class="picsbutton hidden" />
						<input type="button" value="Cancel" class="picsbutton hidden" onclick="cancel();" />
					</td>
				</tr>
			</s:iterator>
			<s:if test="unmapped.keySet().size() == 0">
				<tr><td colspan="4">No unmapped employees currently</td></tr>
			</s:if>
		</tbody>
	</table>
</s:form>

</body>
</html>