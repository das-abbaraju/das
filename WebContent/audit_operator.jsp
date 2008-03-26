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
	var radioGrp = $('form1')['riskLevel'+id];
	var value = null;
	for(i=0; i < radioGrp.length; i++){
	    if (radioGrp[i].checked == true) {
		value = radioGrp[i].value;
	    }
	}
	pars = '&operatorID='+oID+'&auditTypeID='+aID+'&riskLevel='+value+'&auditOperatorID='+pKey;
	
	var divName = 'td'+id;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'AuditOperatorSaveAjax.action', {method: 'post', parameters: pars});
	new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
</script>
</head>
<body>
<h1>Edit Operator/Audit Permissions</h1>
<s:form>
			<s:if test="oID > 0">
				<s:select list="operators" listKey="[0].get('id')" cssClass="blueMain" onchange="location = '?oID='+this.options[this.selectedIndex].value;"
				listValue="%{[0].get('name')}" value="oID"></s:select>
			</s:if>
			<s:else>
				<s:select list="auditTypes" listKey="auditTypeID" cssClass="blueMain" onchange="location = '?aID='+this.options[this.selectedIndex].value;"
				listValue="auditName" value="aID"></s:select>
			</s:else>
</s:form>
<s:form id="form1">
	<table cellspacing="1" cellpadding="3" border="0">
		<tr class="whiteTitle" bgcolor="#003366" align="center">
			<td>Operator/Audit</td>
			<td><s:property value="aName"/><s:property value="oName"/>
			<br />Minimum Risk Level</td>
		</tr>
		
		<s:iterator value="data" status="stat">
			<tr class="blueMain" <s:if test="#stat.odd==true">bgcolor="#ffffff"</s:if> >
				<s:if test="oID > 0">
						<td><a
						href="AuditOperator.action?aID=<s:property value="auditTypeID" />"><s:property
						value="auditName" /></a></td>
				</s:if>
				<s:else>
					<td><a
						href="AuditOperator.action?oID=<s:property value="operatorID" />"><s:property
						value="operatorName" /></a></td>
				</s:else>
				<td id="td<s:property value="id" />"><label><input
					<s:if test="riskLevel == 0">CHECKED</s:if> value="0"
					name="riskLevel<s:property value="id" />" type="radio"
					onchange="save('<s:property value="id" />', '<s:property value="auditTypeID" />', '<s:property value="operatorID" />', '<s:property value="auditOperatorID" />')" />None</label>
				<label><input <s:if test="riskLevel == 1">CHECKED</s:if>
					value="1" name="riskLevel<s:property value="id" />" type="radio"
					onchange="save('<s:property value="id" />', '<s:property value="auditTypeID" />', '<s:property value="operatorID" />', '<s:property value="auditOperatorID" />')" />Low</label>
				<label><input <s:if test="riskLevel == 2">CHECKED</s:if>
					value="2" name="riskLevel<s:property value="id" />" type="radio"
					onchange="save('<s:property value="id" />', '<s:property value="auditTypeID" />', '<s:property value="operatorID" />', '<s:property value="auditOperatorID" />')" />Med</label>
				<label><input <s:if test="riskLevel == 3">CHECKED</s:if>
					value="3" name="riskLevel<s:property value="id" />" type="radio"
					onchange="save('<s:property value="id" />', '<s:property value="auditTypeID" />', '<s:property value="operatorID" />', '<s:property value="auditOperatorID" />')" />High</label>
				</td>
			</tr>
		</s:iterator>
	</table>
</s:form>
</body>
</html>
