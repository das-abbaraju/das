<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="operatorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript">
	function removeName(nameId) {
		var pars = "button=RemoveName&nameId=" + nameId+'&opID='+<s:property value="operatorAccount.id"/>;
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
		var pars = "button=AddName&name=" + name+'&opID='+<s:property value="operatorAccount.id"/>;
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
<s:if test="operatorAccount == null">
	<h1>Create New <s:property value="type"/> Account</h1>
</s:if>
<s:else>
	<s:include value="opHeader.jsp"></s:include>
</s:else>
<s:include value="../actionMessages.jsp" />
<s:form id="save" method="POST" enctype="multipart/form-data">
<div class="buttons">
	<input type="submit" class="picsbutton positive" name="button" value="Save"/>
</div>
<br clear="all" />
<s:hidden name="opID"/>
<s:hidden name="type"/>
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
				<legend><span>Details</span></legend>
				<ol>
					<li><label>Name:</label>
						<s:textfield name="operatorAccount.name" size="35" />
					</li>
					<li><label>Primary Contact:</label>
						<s:textfield name="operatorAccount.contact" />
					</li>
					<li><label>Industry:</label>
						<s:select list="industryList" name="operatorAccount.industry"/>
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Primary Address</span></legend>
				<ol>
					<li><label>Address:</label>
						<s:textfield name="operatorAccount.address" size="35" />
					</li>
					<li><label>City:</label>
						<s:textfield name="operatorAccount.city" size="20" />
					</li>
					<li><label>State/Province:</label>
						<s:textfield name="operatorAccount.state" size="5" />
					</li>
					<li><label>Zip:</label>
						<s:textfield name="operatorAccount.zip" size="7" />
					</li>
					<li><label>Phone:</label>
						<s:textfield name="operatorAccount.phone" size="15" />
					</li>
					<li><label>Phone 2:</label>
						<s:textfield name="operatorAccount.phone2" size="15" />
					</li>
					<li><label>Fax:</label>
						<s:textfield name="operatorAccount.fax" size="15" />
					</li>
					<li><label>Email:</label>
						<s:textfield name="operatorAccount.email" size="30" />
					</li>
					<li><label>Web URL:</label>
						<s:textfield name="operatorAccount.webUrl" size="30" />
					</li>
				</ol>
				</fieldset>
			</td>
			<td style="vertical-align: top; width: 50%; padding-left: 10px;">
				<fieldset class="form">
				<legend><span>Admin Fields</span></legend>
				<ol>
					<li><label>Visible?</label>
						<s:radio list="#{'Y':'Yes','N':'No'}" name="operatorAccount.active" theme="pics" />
					</li>
					<li><label>Receive contractor activation emails:</label>
						<s:radio list="#{'Yes':'Yes','No':'No'}" name="operatorAccount.doSendActivationEmail" theme="pics" />
					</li>
					<li><label>Approves Contractors:</label>
						<s:radio list="#{'Yes':'Yes','No':'No'}" name="operatorAccount.approvesRelationships" theme="pics" />
					</li>
					<li><label>Sees Ins. Certs:</label>
						<nobr><s:radio list="#{'Yes':'Yes','No':'No'}" name="operatorAccount.canSeeInsurance" theme="pics" />
						<span id="auditorID"><s:select list="auditorList" listValue="name" listKey="id" name="auditorid"/></span></nobr>
					</li>
					
					<li><label>Verified By PICS:</label>
						<s:checkbox name="operatorAccount.verifiedByPics" />	
					</li>
					<s:if test="operatorAccount.corporateFacilities.size() != 0 || operatorAccount.parent != null">
						
						<li><label>Parent Corporation / Division / Hub:</label>
							<s:if test="operatorAccount.corporateFacilities.size() != 0">
								<s:select list="operatorAccount.corporateFacilities"
									listKey="corporate.id" listValue="corporate.name" headerKey="0"
									headerValue=" - Select a Parent Facility - " name="parentid" />
							</s:if>
							<s:else>
								<s:select 
									list="#{operatorAccount.parent.id:operatorAccount.parent.name}"	
									headerKey="0" headerValue=" - Select a Parent Facility - "
									name="parentid" />
							</s:else>
						</li>
					
						<li><label>Inherit Flags:</label>
							<s:checkbox name="operatorAccount.inheritFlagCriteria" />	
						</li>
						<li><label>Inherit Insurance Criteria:</label>
							<s:checkbox name="operatorAccount.inheritInsuranceCriteria" />	
						</li>
						<li><label>Inherit Audit Matrix:</label>
							<s:checkbox name="operatorAccount.inheritAudits" />	
						</li>
						<li><label>Inherit Legal Names:</label>
							<s:checkbox name="operatorAccount.inheritLegalNames" />	
						</li>
					</s:if>
					<s:if test="!typeOperator">
						<li><label>Facilities:</label>
						<s:select list="operatorList" listValue="name" listKey="id" name="facilities" multiple="7" size="15"/>
						</li>
					</s:if>
					<li><label>Contractors pay:</label>
					<s:radio list="#{'Yes':'Yes','No':'No','Multiple':'Multiple'}" name="operatorAccount.doContractorsPay" theme="pics" />
					</li>
					<li><label>Send Emails to:</label>
						<s:textfield name="operatorAccount.activationEmails"/>
						<br />* separate emails with commas ex: a@bb.com, c@dd.com
					</li>
									
				</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Legal Additional Insured Names</span></legend>
					<ol><div id="operator_name">
							<s:include value="operator_names.jsp" />
						</div>
					</ol>
				</fieldset>	

			</td>
		</tr>
	</table>
	<br clear="all">
	<div class="buttons">
		<input type="submit" class="picsbutton positi`" name="button" value="Save"/>
	</div>
</s:form>
<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>
</body>
</html>
