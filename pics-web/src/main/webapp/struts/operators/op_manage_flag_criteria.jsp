<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
	<title><s:text name="ManageFlagCriteriaOperator.title" /></title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/manage_flag_criteria.css?v=<s:property value="version"/>" />
	
	<s:include value="../jquery.jsp"/>
	
	<script type="text/javascript" src="js/op_manage_flag_criteria.js?v=${version}"></script>
	
	<script>
	function getFlag(selectObject) {
		var flagColor = $(selectObject).find("option:selected").val();

		if (flagColor == "Red")
			flagColor = '<s:text name="FlagColor.Red.smallIcon" />';
		else if (flagColor == "Amber")
			flagColor = '<s:text name="FlagColor.Amber.smallIcon" />';
		else
			flagColor = '<s:text name="FlagColor.Green.smallIcon" />';
		
		var flagImage = $(selectObject.parentNode).find("span.flagImage img").replaceWith(flagColor);
	}

	var accountID = '<s:property value="account.id" />';
	</script>
</head>
<body>
	<s:include value="opHeader.jsp"></s:include>
	<s:include value="../config_environment.jsp" />
	
	<s:if test="permissions.operatorCorporate && ((insurance && !operator.equals(operator.inheritInsuranceCriteria)) || (!insurance && !operator.equals(operator.inheritFlagCriteria)))">
		<div id="info">
			<s:text name="ManageFlagCriteriaOperator.message.InheritedFrom">
				<s:param value="%{insurance ? getText('ManageFlagCriteriaOperator.header.Insurance') : getText('ManageFlagCriteriaOperator.header.Flag')}" />
			</s:text>
		</div>
	</s:if>
	
	<div>
		<s:form id="form1" method="get">
			<s:hidden name="id" />
			<s:hidden name="insurance" />
			
			<table style="width: 100%;">
				<tr>
					<td class="leftHand">
						<%-- Criteria --%>
						<div id="criteriaDiv">
							<s:include value="op_manage_flag_criteria_list.jsp" />
						</div>
						
						<span id="thinking"></span>
						
						<%-- Corporate List --%>
						<s:if test="(permissions.corporate || permissions.admin) && operator.operatorFacilities.size() > 0">
							<div id="corporateList">
								<div class="info"><s:text name="ManageFlagCriteriaOperator.message.LinkedAccounts" /></div>
								<a href="#" class="remove" id="emptyChildCriteria"></a>
								<div id="childCriteria" data-op="0"></div>
								
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
													<a href="#" class="childCriteria" data-facid="<s:property value="#facility.inheritInsuranceCriteria.id" />"
														data-facname="<s:property value="#facility.inheritInsuranceCriteria.name" />">
														<s:property value="#facility.inheritInsuranceCriteria.name" />
													</a>
												</s:if>
												<s:else>
													<a href="#" class="childCriteria" data-facid="<s:property value="#facility.inheritFlagCriteria.id" />"
														data-facname="<s:property value="#facility.inheritFlagCriteria.name" />">
														<s:property value="#facility.inheritFlagCriteria.name" />
													</a>
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
                                    <s:param value="%{insurance ? getText('ManageFlagCriteriaOperator.header.Insurance') : getText('global.Flag')}" />
								</s:text>
								
								<s:set name="linkedOp" value="operator" />
								
								<s:if test="insurance && !operator.equals(operator.inheritInsuranceCriteria)">
									<s:set name="linkedOp" value="operator.inheritInsuranceCriteria" />
								</s:if>
								<s:elseif test="!insurance && !operator.equals(operator.inheritFlagCriteria)">
									<s:set name="linkedOp" value="operator.inheritFlagCriteria" />
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
	<%-- Bump contractor form + so another button can alias this form without having to dynamically embed and create forms --%>
	<s:if test="permissions.admin && criteriaList.size() > 0">
		<s:form id="bump_contractors">
			<s:hidden name="operator" />
			<s:submit method="bumpContractors" key="ManageFlagCriteriaOperator.button.BumpContractors" cssClass="picsbutton" />
		</s:form>
	</s:if>
</body>
</html>
