<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
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
<script type="text/javascript" src="js/op_manage_flag_criteria.js"></script>
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
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<s:if test="permissions.operatorCorporate">
	<s:if test="!insurance && !operator.equals(operator.inheritFlagCriteria)">
		<div id="info">Flag Criteria inherited from <s:property value="operator.inheritFlagCriteria.name" /></div>
	</s:if>
	<s:if test="insurance && !operator.equals(operator.inheritInsuranceCriteria)">
		<div id="info">Insurance Criteria inherited from <s:property value="operator.inheritInsuranceCriteria.name" /></div>
	</s:if>
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
								<a href="ManageFlagCriteria.action">Manage Flag Criteria List</a> &nbsp;|&nbsp;
							</pics:permission>
							<a href="#" onclick="getAddQuestions(); return false;" class="add">Add New Criteria</a>
						</nobr>
						<div id="addCriteria" style="display:none;"></div>
					</s:if>
				<span id="thinking"></span>
				<s:if test="(permissions.corporate || permissions.admin) && operator.operatorFacilities.size() > 0">
					<div id="corporateList">
						<div class="info">
							Below is a list of all accounts that are linked to your corporate account, together with the link to their flag criteria.
							Click on the flag criteria links for more details.
						</div>
						<div id="childCriteria"></div>
						<table class="report">
							<thead><tr><th colspan="2">Linked Accounts</th><th>Inherits <s:if test="insurance">Insurance</s:if><s:else>Flag</s:else> Criteria From</th></tr></thead>
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
				<s:if test="permissions.admin">
					<div style="clear: left;">
						<s:if test="!insurance && !operator.equals(operator.inheritFlagCriteria)">
							Flag Criteria inherited from <a href="ManageFlagCriteriaOperator.action?id=<s:property value="operator.inheritFlagCriteria.id" />">
								<s:property value="operator.inheritFlagCriteria.name" /></a>
						</s:if>
						<s:if test="insurance && !operator.equals(operator.inheritInsuranceCriteria)">
							Insurance Criteria inherited from <a href="ManageFlagCriteriaOperator.action?id=<s:property value="operator.inheritInsuranceCriteria.id" />">
								<s:property value="operator.inheritInsuranceCriteria.name" /></a>
						</s:if>
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
