<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min.js"></script>

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />

<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
<style>
div.dataTables_filter { width: 65%; }
div.dataTables_length { width: 35%; }
.newJobSite { display: none; }
</style>
<script type="text/javascript">
var employeeID = <s:property value="employee == null ? 0 : employee.id"/>;
$(function() {
	$('#employees').dataTable({
		aoColumns: [
	            {bVisible: false},
	            {sType: "html"},
	            {sType: "html"},
	            null,
	            null,
	            null
			],
		aaSorting: [[1, 'asc']],
		bJQueryUi: true,
		bStateSave: true,
		bLengthChange: false,
		oLanguage: {
			sSearch:"Search",
			sLengthMenu: '_MENU_', 
			sInfo:"_START_ to _END_ of _TOTAL_",
			sInfoEmpty:"",
			sInfoFiltered:"(filtered from _MAX_)" },
		fnRowCallback: function( nRow, aData, iDisplayIndex ) {
			if (aData[0] == employeeID)
				$(nRow).not('.highlight').addClass('highlight');

			return nRow;
		}
	});
	if (employeeID > 0)
		loadEmployee(employeeID, false);
});

function loadEmployee(id, add) {
	$('#employeeForm').load('ManageEmployees!loadAjax.action', {'employee.id' : id, button: (add ? "Add" : null) })
	return false;
}
</script>
</head>
<body>
	<s:if test="auditID > 0">
		<div class="info"><a href="Audit.action?auditID=<s:property value="auditID" />"><s:text name="%{scope}.message.ReturnTo" /> <s:text name="AuditType.99.name" /></a></div>
	</s:if>
	<h1><s:property value="account.name" /><span class="sub"><s:text name="%{scope}.title" /></span></h1>
	<s:include value="../actionMessages.jsp"/>

	<s:if test="account.employees.size() == 0 && employee == null">
		<div class="info">
			<s:text name="%{scope}.message.NoEmployees">
				<s:param><s:text name="%{scope}.link.Add" /></s:param>
			</s:text>
		</div>
	</s:if>

	<a href="#" class="add" onclick="return loadEmployee(0, true);"><s:text name="%{scope}.link.Add" /></a><br />
	<a href="#" onclick="showExcelUpload(); return false;" class="add"><s:text name="%{scope}.link.Import" /></a>
	<table>
		<tr>
			<s:if test="account.employees.size() > 0">
				<td style="vertical-align:top; width: 25%;">
					<table class="report" id="employees">
						<thead>
							<tr>
								<th>id</th>
								<th>Last Name</th>
								<th>First Name</th>
								<th>Title</th>
								<th>Classification</th>
								<th><s:text name="%{scope}.message.Profile" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="account.employees" id="e">
								<tr>
									<td><s:property value="#e.id"/></td>
									<td><a href="#" onclick="return loadEmployee(<s:property value="#e.id" />);"><s:property value="#e.lastName"/></a></td>
									<td><a href="#" onclick="return loadEmployee(<s:property value="#e.id" />);"><s:property value="#e.firstName"/></a></td>
									<td><s:property value="#e.title"/></td>
									<td><s:property value="#e.classification"/></td>
									<td class="center"><a href="EmployeeDetail.action?employee.id=<s:property value="#e.id" />"><s:text name="%{scope}.link.View" /></a></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</td>
				
				<td style="width: 20px;"></td>
			</s:if>
			<td style="vertical-align:top;">
				<div id="employeeForm"></div>
			</td>
		</tr>
	</table>
	<div id="siteEditBox" style="display: none;"></div>
</body>
</html>