<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<style type="text/css">
table.report a {
	text-decoration: underline;
}

.flagImage {
	width: 10px;
	height: 12px;
}

.leftHand, .rightHand {
	vertical-align: top;
}

.leftHand {
	width: 70%;
}

.leftHand table.report {
	width: 100%;
}

.rightHand table.report {
	float: right;
}

.editable {
	display: none;
}

.hover {
	margin-left: 10px;
}

.newImpact {
	color: gray;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function getFlag(selectObject) {
	var flagColor = $(selectObject).find("option:selected").val();

	if (flagColor == "Red")
		flagColor = '<s:property value="@com.picsauditing.jpa.entities.FlagColor@Red.smallIcon" escape="false" />';
	else if (flagColor == "Amber")
		flagColor = '<s:property value="@com.picsauditing.jpa.entities.FlagColor@Amber.smallIcon" escape="false" />';
	else
		flagColor = '<s:property value="@com.picsauditing.jpa.entities.FlagColor@Green.smallIcon" escape="false" />';
	
	var flagImage = $(selectObject.parentNode).find("span.flagImage img").replaceWith(flagColor);
}

var confirmRemoveCriteria = "<s:text name="ManageFlagCriteriaOperator.javascript.ConfirmRemoveCriteria" />";
var addingCriteria = "<s:text name="ManageFlagCriteriaOperator.javascript.AddingCriteria" />";
var savingChanges = "<s:text name="ManageFlagCriteriaOperator.javascript.SavingChanges" />";
var loadingCriteria = "<s:text name="ManageFlagCriteriaOperator.javascript.LoadingCriteria" />";
var impactedContractors = "<s:text name="ManageFlagCriteriaOperator.javascript.ImpactedContractors" />";
var loadingAffected = "<s:text name="ManageFlagCriteriaOperator.javascript.LoadingAffected" />";
var loadingLinked = "<s:text name="ManageFlagCriteriaOperator.javascript.LoadingLinked" />";
</script>
<script type="text/javascript" src="js/op_manage_flag_criteria.js"></script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<s:if test="permissions.operatorCorporate && ((insurance && !operator.equals(operator.inheritInsuranceCriteria)) || (!insurance && !operator.equals(operator.inheritFlagCriteria)))">
	<div id="info">
		<s:text name="ManageFlagCriteriaOperator.message.InheritedFrom">
			<s:param>
				<s:if test="insurance">
					<s:text name="%{scope}.header.Insurance" />
				</s:if>
				<s:else>
					<s:text name="%{scope}.header.Flag" />
				</s:else>
			</s:param>
			<s:param>
				<s:if test="insurance && !operator.equals(operator.inheritInsuranceCriteria)">
					<s:property value="operator.inheritInsuranceCriteria.name" />
				</s:if>
				<s:elseif test="!insurance && !operator.equals(operator.inheritFlagCriteria)">
					<s:property value="operator.inheritFlagCriteria.name" />
				</s:elseif>
			</s:param>
		</s:text>
	</div>
</s:if>

<div style="vertical-align: top">
<s:form id="form1" method="get">
	<s:hidden name="id" />
	<s:hidden name="insurance" />
	<table style="width: 100%;">
		<tr>
			<td class="leftHand">
				<div id="criteriaDiv">
					<s:include value="op_manage_flag_criteria_list.jsp" />
				</div>
					<s:if test="canEdit">
						<nobr>
							<pics:permission perm="ManageAudits">
								<a href="ManageFlagCriteria.action"><s:text name="ManageFlagCriteriaOperator.link.ManageFlagCriteria" /></a> &nbsp;|&nbsp;
							</pics:permission>
							<a href="#" onclick="getAddQuestions(); return false;" class="add"><s:text name="ManageFlagCriteriaOperator.link.AddNewCriteria" /></a>
						</nobr>
						<div id="addCriteria" style="display:none;"></div>
					</s:if>
				<span id="thinking"></span>
				<s:if test="(permissions.corporate || permissions.admin) && operator.operatorFacilities.size() > 0">
					<div id="corporateList">
						<div class="info"><s:text name="ManageFlagCriteriaOperator.message.LinkedAccounts" /></div>
						<div id="childCriteria"></div>
						<table class="report">
							<thead>
								<tr>
									<th colspan="2"><s:text name="ManageFlagCriteriaOperator.header.LinkedAccounts" /></th>
									<th><s:text name="ManageFlagCriteriaOperator.header.InheritsFrom">
										<s:param>
											<s:if test="insurance"><s:text name="ManageInsuranceCriteriaOperator.header.Insurance" /></s:if>
											<s:else><s:text name="global.Flag" /></s:else>
										</s:param>
									</s:text></th>
								</tr>
							</thead>
							<tbody>
							<s:iterator status="stat" id="linked" value="operator.operatorFacilities">
								<s:set name="facility" value="#linked.operator" />
								<tr>
									<td><s:property value="#stat.count" /></td>
									<td><s:property value="#facility.name" /></td>
									<td>
										<s:if test="insurance">
											<a href="#" onclick="getChildCriteria(<s:property value="#facility.inheritInsuranceCriteria.id" />, '<s:property value="#facility.inheritInsuranceCriteria.name" />'); return false;">
											<s:property value="#facility.inheritInsuranceCriteria.name" /></a>
										</s:if>
										<s:else>
											<a href="#" onclick="getChildCriteria(<s:property value="#facility.inheritFlagCriteria.id" />, '<s:property value="#facility.inheritFlagCriteria.name" />'); return false;">
												<s:property value="#facility.inheritFlagCriteria.name" /></a>
										</s:else>
									</td>
								</tr>
							</s:iterator>
							</tbody>
						</table>
					</div>
				</s:if>
				<s:if test="permissions.admin && ((insurance && !operator.equals(operator.inheritInsuranceCriteria)) || (!insurance && !operator.equals(operator.inheritFlagCriteria)))">
					<div style="clear: left;">
						<s:text name="ManageFlagCriteriaOperator.message.InheritedFrom">
							<s:param>
								<s:if test="insurance">
									<s:text name="ManageInsuranceCriteriaOperator.header.Insurance" />
								</s:if>
								<s:else>
									<s:text name="global.Flag" />
								</s:else>
							</s:param>
						</s:text>
						<s:set name="linkedOp" value="operator" />
						<s:if test="insurance && !operator.equals(operator.inheritInsuranceCriteria)">
							<s:set name="linkedOp" value="operator.inheritFlagCriteria" />
						</s:if>
						<s:elseif test="!insurance && !operator.equals(operator.inheritFlagCriteria)">
							<s:set name="linkedOp" value="operator.inheritInsuranceCriteria" />
						</s:elseif>
						<a href="ManageFlagCriteriaOperator.action?id=<s:property value="#linkedOp.id" />&insurance=<s:property value="insurance" />"><s:property value="#linkedOp.name" /></a>
					</div>
				</s:if>
			</td>
			<td class="rightHand">
				<div id="impactDiv"></div>
			</td>
		</tr>
	</table>
	
	
</s:form>
</div>

</body>
</html>
