<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Employee Sites</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>

</head>
<body>
	<h1>Manage Employee Sites<span class="sub"><s:property value="subHeading" escape="false"/></span></h1>
	
	<s:include value="../actionMessages.jsp"/>

	<s:if test="account.employees.size() == 0 && employee == null">
		<div class="info">
			This Employee is not associated with a Job Site. Click the "Add Employee to Job Site" button to associate this Employee with a Job Site.
		</div>
	</s:if>

	<a onclick="$('#addEmployeeSite').show(); $('#addLink').hide(); return false;"
		href="#" id="addLink" class="picsbutton positive">Add Employee to Job Site</a>
	<div id="addEmployeeSite" style="display: none; clear: both;">
		<s:form id="newEmployeeSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
			<s:hidden name="employee.id" />
			<fieldset class="form bottom">
				<legend><span>Add <s:property value="employee.displayName"/> to Job Site:</span></legend>
				<ol>
					<s:if test="permissions.contractor">
						<li><label>Job Site:</label>
							<s:select
							list="operatorsForContractor"
							name="operator.id"
							listKey="id"
							listValue="name"
							/>
						</li>
					</s:if>
					<s:elseif test="permissions.operator">
						<li><label>Job Site:</label>
							<s:select
							list="operatorsForOperator"
							name="operator.id"
							listKey="id"
							listValue="name"
							/>
						</li>
					</s:elseif>
					<s:elseif test="permissions.corporate">
						<li><label>Job Site:</label>
							<s:select
							list="operatorsForCorporate"
							name="operator.id"
							listKey="id"
							listValue="name"
							/>
						</li>
					</s:elseif>
				</ol>
				<div style="text-align: center; margin: 0px auto;">
					<input type="submit" value="Save" class="picsbutton positive" name="button" />
					<button onclick="$('#addLink').show(); $('#addEmployeeSite').hide(); return false;"
						class="picsbutton negative">Cancel</button>
				</div>
			</fieldset>
		</s:form>
	</div>

	<table style="margin-top: 20px;">
		<tr>
			<s:if test="employeeSites.size > 0">
				<td style="vertical-align:top; width: 25%;">
					<h3>Job Sites:</h3>
					<table class="report" id="jobSites">
						<thead>
							<tr>
								<th>Operator</th>
								<th>Remove</th>
							</tr>
						</thead>
						<s:iterator value="employeeSites" id="employeeSite">
							<tr>
								<td><s:property value="#employeeSite.operator.name"/>
									<s:if test="#employee.jobSite != null">:&nbsp;
										<s:property value="#employeeSite.jobSite.label"/>
									</s:if>
								</td>
								<td class="center">
									<a href="?employeeSite.id=<s:property value="#employeeSite.id"/>&button=Remove" class="remove"></a>
								</td>
							</tr>
						</s:iterator>
					</table>
				</td>
				<td style="width: 20px;"></td>
			</s:if>
		</tr>
	</table>
</body>
</html>