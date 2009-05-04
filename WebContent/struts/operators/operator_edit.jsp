<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="operator.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript">
	function removeName(nameId) {
		var pars = "button=RemoveName&nameId=" + nameId+'&id='+<s:property value="operator.id"/>;
		var divName ='operator_name';
		$(divName).innerHTML="<img src='images/ajax_process.gif' />";
		var myAjax = new Ajax.Updater(divName, 'AccountNameEditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) { 
				new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
		return false;
	}
	
	function addName() {
		name = $('legalName').value;
		var pars = "button=AddName&name=" + name+'&id='+<s:property value="operator.id"/>;
		var divName ='operator_name';
		$(divName).innerHTML="<img src='images/ajax_process.gif' />";
		var myAjax = new Ajax.Updater(divName, 'AccountNameEditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) { 
				
				new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
		return false;
	}
</script>
</head>
<body>
<s:if test="operator == null">
	<h1>Create New <s:property value="type" /> Account</h1>
</s:if>
<s:else>
	<s:include value="opHeader.jsp"></s:include>
</s:else>
<s:include value="../actionMessages.jsp" />
<s:form id="save" method="POST" enctype="multipart/form-data">
	<div class="buttons"><input type="submit" class="picsbutton positive" name="button" value="Save" /></div>
	<br clear="all" />
	<s:hidden name="id" />
	<s:hidden name="type" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
			<fieldset class="form"><legend><span>Details</span></legend>
			<ol>
				<li><label>Name:</label> <s:textfield name="operator.name" size="35" /></li>
				<li><label>Primary Contact:</label> <s:textfield name="operator.contact" /></li>
				<li><label>Industry:</label> <s:select list="industryList" name="operator.industry" /></li>
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Primary Address</span></legend>
			<ol>
				<li><label>Address:</label> <s:textfield name="operator.address" size="35" /></li>
				<li><label>City:</label> <s:textfield name="operator.city" size="20" /></li>
				<li><label>State/Province:</label> <s:textfield name="operator.state" size="5" /></li>
				<li><label>Zip:</label> <s:textfield name="operator.zip" size="7" /></li>
				<li><label>Phone:</label> <s:textfield name="operator.phone" size="15" /></li>
				<li><label>Phone 2:</label> <s:textfield name="operator.phone2" size="15" /></li>
				<li><label>Fax:</label> <s:textfield name="operator.fax" size="15" /></li>
				<li><label>Email:</label> <s:textfield name="operator.email" size="30" /></li>
				<li><label>Web URL:</label> <s:textfield name="operator.webUrl" size="30" /></li>
			</ol>
			</fieldset>
			</td>
			<td style="vertical-align: top; width: 50%; padding-left: 10px;">
			<fieldset class="form"><legend><span>Admin Fields</span></legend>
			<ol>
				<li><label>Visible?</label> <s:radio list="#{'Y':'Yes','N':'No'}" name="operator.active" theme="pics" /></li>
				<li><label>Receive contractor activation emails:</label> <s:radio list="#{'Yes':'Yes','No':'No'}"
					name="operator.doSendActivationEmail" theme="pics" /></li>
				<li><label>Approves Contractors:</label> <s:radio list="#{'Yes':'Yes','No':'No'}"
					name="operator.approvesRelationships" theme="pics" /></li>
				<li><label>Sees Ins. Certs:</label> <s:radio list="#{'Yes':'Yes','No':'No'}" name="operator.canSeeInsurance"
					theme="pics" /></li>

				<li><label>Verified By PICS:</label> <s:checkbox name="operator.verifiedByPics" /></li>
				<li><label>Contractors pay:</label> <s:radio list="#{'Yes':'Yes','No':'No','Multiple':'Multiple'}"
					name="operator.doContractorsPay" theme="pics" /></li>
				<li><label>Send Emails to:</label> <s:textarea name="operator.activationEmails" rows="3" cols="40" /> <br />
				* separate emails with commas ex: a@bb.com, c@dd.com</li>
				
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Linked Accounts</span></legend>
			<ol>
				<s:if test="operator.corporate">
					<li><label>Facilities:</label> <s:select list="operatorList" listValue="name" listKey="id" name="facilities"
						multiple="7" size="15" /></li>
				</s:if>
				<s:if test="operator.operator">
					<s:if test="operator.corporateFacilities.size() > 0">
						<li><label>Parent Corporation / Division / Hub:</label> <s:select list="operator.corporateFacilities"
							listKey="corporate.id" listValue="corporate.name" headerKey="0" headerValue=" - Select a Parent Facility - "
							name="operator.parent.id" /></li>
					</s:if>

					<li>
					<div style="font-weight: bold; text-align: center;">Operator Configuration Inheritance</div>
					</li>
					<li><label>Flag Criteria:</label> <s:select name="operator.inheritFlagCriteria.id" list="relatedFacilities"
						listKey="id" listValue="name"></s:select></li>
					<li><label>Insurance Criteria:</label> <s:select name="operator.inheritInsuranceCriteria.id"
						list="relatedFacilities" listKey="id" listValue="name"></s:select></li>
					<li><label>Policy Types:</label> <s:select name="operator.inheritInsurance.id" list="relatedFacilities"
						listKey="id" listValue="name"></s:select></li>
					<li><label>Audit Types:</label> <s:select name="operator.inheritAudits.id" list="relatedFacilities"
						listKey="id" listValue="name"></s:select></li>
					<li><label>Audit Categories:</label> <s:select name="operator.inheritAuditCategories.id"
						list="relatedFacilities" listKey="id" listValue="name"></s:select></li>
				</s:if>

			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Legal Additional Insured Names</span></legend>
			<ol>
				<div id="operator_name"><s:include value="operator_names.jsp" /></div>
			</ol>
			</fieldset>

			</td>
		</tr>
	</table>
	<br clear="all">
	<div class="buttons"><input type="submit" class="picsbutton positive" name="button" value="Save" /></div>
</s:form>
<div id="caldiv1"
	style="position: absolute; visibility: hidden; background-color: white; layer-background-color: white;"></div>
</body>
</html>
