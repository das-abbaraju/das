<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Define Competencies</title>
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
<s:include value="../operators/opHeader.jsp"/>
				<div id="addCompetencyMenu" style="display: none; clear: both;">
					<s:form id="newCompetencyMenu" method="POST" enctype="multipart/form-data">
						<s:hidden name="operator.id" />
						<fieldset class="form bottom">
							<legend><span>Define Competency</span></legend>
							<ol>
								<li><label>Competency:</label>
									<s:textfield id="competencyInputBox" name="competency" />
								</li>
							</ol>
							<div style="text-align: center; margin: 0px auto;">
								<input type="submit" value="Add" class="picsbutton positive" name="button" />
								<button onclick="$('#competencyInputBox').val(''); return false;"
									class="picsbutton negative">Cancel</button>
							</div>
						</fieldset>
					</s:form>
				</div>
				<s:if test="competencies.size > 0">
						<table class="report">
							<thead>
								<tr>
									<th>Category</th>
									<th>Label</th>
									<th>Description</th>
									<th>Percent Used</th>
									<th>Help Page</th>
									<th>Edit</th>
									<th>Delete</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="competencies" status="competency">
										<tr>
											<td><s:property value="#competency.category"/></td>
											<td><s:property value="#competency.label"/></td>
											<td><s:property value="#competency.description"/></td>
											<td>0%</td>
											<td><a href="#">www.wikipedia.org</a></td>
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
