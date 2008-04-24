<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Edit Operator/Audit Permissions</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<script type="text/javascript">
function save(id, aID, oID, pKey) {
	pars = '&ao.operatorAccount.id='+oID+'&ao.auditType.auditTypeID='+aID+'&ao.auditOperatorID='+pKey;

	var radioGrp = $('form1')['riskLevel'+id];
	for(i=0; i < radioGrp.length; i++){
	    if (radioGrp[i].checked == true) {
			pars = pars + '&ao.minRiskLevel='+ radioGrp[i].value;
	    }
	}
	
	var radioGrp = $('form1')['requiredForFlag'+id];
	for(i=0; i < radioGrp.length; i++){
	    if (radioGrp[i].checked == true) {
			pars = pars + '&ao.requiredForFlag='+ radioGrp[i].value;
	    }
	}
	
	var divName = 'td'+id;
	new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
	var myAjax = new Ajax.Updater(divName, 'AuditOperatorSaveAjax.action', {method: 'get', parameters: pars});
}
</script>
</head>
<body>
<h1>Edit Operator/Audit Permissions <span class="sub"><s:property
	value="aName" /><s:property value="oName" /></span></h1>
<s:form>
	<s:if test="oID > 0">
		<s:select list="operators" listKey="id" cssClass="blueMain"
			onchange="location = '?oID='+this.options[this.selectedIndex].value;"
			listValue="name" value="oID"></s:select>
	</s:if>
	<s:else>
		<s:select list="auditTypes" listKey="auditTypeID" cssClass="blueMain"
			onchange="location = '?aID='+this.options[this.selectedIndex].value;"
			listValue="auditName" value="aID"></s:select>
	</s:else>
</s:form>
<br />
<s:form id="form1">
	<table cellspacing="1" cellpadding="3" border="0">
		<tr class="whiteTitle" bgcolor="#003366" align="center">
			<td>Operator/Audit</td>
			<td style="text-align: center;">Minimum Risk Level &amp; Flag Color</td>
		</tr>

		<s:iterator value="data" status="stat">
			<tr class="blueMain"
				<s:if test="#stat.even">bgcolor="#EEEEEE"</s:if>>
				<td style="vertical-align: middle;">
				<s:if test="oID > 0">
					<a
						href="AuditOperator.action?aID=<s:property value="auditType.auditTypeID" />"><s:property
						value="auditType.auditName" /></a>
				</s:if>
				<s:else>
					<a
						href="AuditOperator.action?oID=<s:property value="operatorAccount.id" />"><s:property
						value="operatorAccount.name" /></a>
				</s:else>
				</td>
				<td id="td<s:property value="htmlID" />">
					<table border="0">
					<tr>
					<td style="text-align: right">Minimum Risk Level:</td>
					<td><s:radio name="riskLevel%{htmlID}" list="riskLevelList" value="minRiskLevel" 
					onchange="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" /></td>
					</tr>
					<tr>
					<td style="text-align: right">Flagged if Missing:</td>
					<td><s:radio name="requiredForFlag%{htmlID}" list="FlagColorList" value="requiredForFlag"
					onchange="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" /></td>
					</tr>
					</table>
				</td>
			</tr>
		</s:iterator>
	</table>
</s:form>
</body>
</html>
