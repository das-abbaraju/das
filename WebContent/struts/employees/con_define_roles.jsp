<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Define Job Roles</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
#rolesTable {
	width: 100%;
}

#rolesTable table.report {
	margin-right: 10px;
	margin-bottom: 10px;
}

fieldset.form label {
	width: 5em;
	margin-right: 0px;
}
</style>
<s:include value="../jquery.jsp"/>
</head>
<body>
<s:include value="../contractors/conHeader.jsp"/>
				<div id="addRoleMenu" style="display: none; clear: both;">
					<s:form id="newRoleMenu" method="POST" enctype="multipart/form-data">
						<s:hidden name="contractor.id" />
						<fieldset class="form bottom">
							<legend><span>Define Role</span></legend>
							<ol>
								<li><label>Role:</label>
									<s:textfield id="roleInputBox" name="role" />
								</li>
							</ol>
							<div style="text-align: center; margin: 0px auto;">
								<input type="submit" value="Add" class="picsbutton positive" name="button" />
								<button onclick="$('#roleInputBox').val(''); return false;"
									class="picsbutton negative">Cancel</button>
							</div>
						</fieldset>
					</s:form>
				</div>
				<s:if test="jobRoles.size > 0">
						<table class="report">
							<thead>
								<tr>
									<th>Name</th>
									<th>Status</th>
									<th>Times Used</th>
									<th>Edit</th>
									<th>Delete</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="jobRoles" status="role">
										<tr>
											<td><s:property value="#role.name"/></td>
											<td><s:if test="#role.active">Active</s:if><s:else>Inactive</s:else></td>
											<td><s:property value="getUsedCount(#role.id)"/></td>
											<td class="center">
												<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&groupNumber=<s:property value="#groupNumber" />&jobTaskID=<s:property value="jobTaskID" />&button=Edit&jobTaskCriteriaID=<s:property value="id" />"
													class="edit"></a>
											</td>
											<td class="center">
												<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&groupNumber=<s:property value="#groupNumber" />&jobTaskID=<s:property value="jobTaskID" />&button=Remove&jobTaskCriteriaID=<s:property value="id" />"
													class="remove"></a>
											</td>
										</tr>
																	
								</s:iterator>
							</tbody>
						</table>
						</s:if>
	</body>
</html>
