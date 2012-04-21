<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>Create New Audit</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=${version}" />
		
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js?v=${version}"></script>
		
		<script type="text/javascript">
			$(function() {
				$('.block').click(function() {
					$.blockUI({message:'Creating Audit...'});
				});
				
				updateDisplay();
			})
			

			function updateDisplay() {
				var auditType = $("#auditTypeSelector input[type='radio']:checked").val();
				if (typeof auditType == "undefined") {
					auditType = 0;
				}

				if (auditType == 17 || auditType == 29) {
					$('#employeeSelector').show();
				} else {
					$('#employeeSelector').hide();						
				}
				
				return true;
			}
		</script>
	</head>
	<body>
		<s:include value="../contractors/conHeader.jsp"/>
		<s:include value="../actionMessages.jsp"/>
	
		<s:if test="manuallyAddAudits.size() > 0">
			<s:form method="post" >
				<s:hidden name="id"/>
				
				<fieldset class="form">
					<h2 class="formLegend">Create New Audit</h2>
					<ol>
						<li id="auditTypeSelector">
							<label>Audit Type:</label>
							<s:radio
							    id="auditTypeSelect"
								name="selectedAudit"
								list="manuallyAddAudits"
								listKey="id"
								listValue="name"
								theme="pics"
								onchange= "updateDisplay()"
							/>
						</li>
						
						<s:if test="permissions.admin">
							<li >
								<label>Operator</label>
								<s:select name="selectedOperator" list="contractor.nonCorporateOperators" headerKey="" headerValue="- Operator -" listKey="operatorAccount.id" listValue="operatorAccount.name" />
							</li>
						</s:if>
						
						<li id="auditForSelector" >
							<label>For:</label>
							<s:textfield name="auditFor" />
						</li>
						
						<li id="employeeSelector" >
							<label>Employees:</label>
							<s:optiontransferselect
								name="employeesLeftList"
								list="employeesLeftList"
								listKey="id"
								listValue="nameTitle"
								doubleName="selectedEmployeeIds"
								doubleList="employeesRightList"
								doubleListKey="id"
								doubleListValue="nameTitle"
								leftTitle="%{getText('AuditOverride.AvailableEmployeeList')}"
								rightTitle="%{getText('AuditOverride.SelectedEmployeeList')}"
								addToLeftLabel="%{getText('AuditOverride.Remove')}"
								addToRightLabel="%{getText('AuditOverride.Add')}"
								allowAddAllToLeft="false"
								allowAddAllToRight="false"
								allowSelectAll="false"
								allowUpDownOnLeft="false"
								allowUpDownOnRight="false"
								buttonCssClass="arrow"
								theme="pics"
	 						/>
						</li>
					</ol>
				</fieldset>
				
				<fieldset class="form submit">
					<s:submit name="button" value="Create" cssClass="picsbutton positive block"/>
				</fieldset>
			</s:form>
		</s:if>
		<s:else>
			<div class="info">There are no audits to add.</div>
		</s:else>
	</body>
</html>