<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function(){
	var sortList = $('#list').sortable({
		update: function() {
			$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="auditType.id"/>&type=AuditType', 
				sortList.sortable('serialize').replace(/\[|\]/g,''), 
				function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
			);
		}
	});
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
	<s:if test="">
		var jsonObj = <s:property value="" />
	</s:if>	
});

function copyAuditType(atypeID) {
	$('#copy_audit').load('ManageAuditTypeAjax.action', {button: 'text', 'id': atypeID},
		function() {
			$(this).dialog({
				modal: true, 
				title: 'Copy Audit Type',
				width: '55%',
				close: function(event, ui) {
					$(this).dialog('destroy');
					location.reload();
				},
				buttons: {
					Cancel: function() {
						$(this).dialog('close');
					},
					'Copy All': function() {
						var data = $('form#textForm').serialize();
						data += "&button=CopyAll&originalID="+atypeID;
						startThinking( {div: 'copy_audit', message: 'Copying Audit Type...' } );
						$.ajax(
							{
								url: 'ManageAuditTypeAjax.action',
								data: data,
								complete: function() {
									stopThinking( {div: 'copy_audit' } );
									$(this).dialog('close');
									location.reload();
								}
							}
						);
					},
					'Copy Only Audit Type': function() {
						var data = $('form#textForm').serialize();
						data += "&button=Copy&originalID="+atypeID;
						startThinking( {div: 'copy_audit', message: 'Copying Audit Type...' } );
						$.ajax(
							{
								url: 'ManageAuditTypeAjax.action',
								data: data,
								complete: function() {
									stopThinking( {div: 'copy_audit' } );
									$(this).dialog('close');
									location.reload();
								}
							}
						);
					}
				}
			});
		}
	);
}
</script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<s:form id="save">
<s:hidden name="id"></s:hidden>
	<div>
		<fieldset class="form">
		<h2 class="formLegend">Audit Type</h2>
			<ol>
				<li><label>ID:</label>
					<s:if test="auditType.id > 0"><s:property value="auditType.id" /></s:if>
					<s:else>NEW</s:else>
				</li>
				<li><label>Name:</label>
					<s:textfield name="auditType.auditName"></s:textfield>
					<div class="fieldhelp">
						<h3>Audit Name</h3>
						<p>The name of the document, policy type, audit, or operator specific form</p>
					</div>
				</li>
				<li><label>Class:</label>
					<s:select list="classList" name="auditType.classType" />
				</li>
				<li><label>Sort Order:</label>
					<s:textfield name="auditType.displayOrder" />
				</li>
				<li><label>Description:</label>
					<s:textfield name="auditType.description"></s:textfield>
					<div class="fieldhelp">
						<h3>Description</h3>
						<p>An optional description used for reference. Currently this is not used anywhere.</p>
					</div>
				</li>
				<li><label>Has Multiple:</label>
					<s:checkbox name="auditType.hasMultiple" />
					<div class="fieldhelp">
						<h3>Has Multiple</h3>
						<p>Check this box if a given contractor can have more than one of these types of audits active at the same time. This is usually NOT checked.</p>
					</div>
				</li>
				<li><label>Can Renew:</label>
					<s:checkbox name="auditType.renewable" />
					<div class="fieldhelp">
						<h3>Can Renew</h3>
						<p>Check this box if the document or audit is reusable at the end of its life. For example, PQF is renewable because we don't make them fill out a whole new PQF each year. GL Policy is NOT renewable because we force them to fill out a brand new policy each time. One major drawback to renewable audit types is they don't maintain a history of past audits.</p>
					</div>
				</li>
				<li><label>Is Scheduled:</label>
					<s:checkbox name="auditType.scheduled" />
				</li>
				<li><label>Has Safety Professional:</label>
					<s:checkbox name="auditType.hasAuditor" />
				</li>
				<li><label>Contractor Can View:</label>
					<s:checkbox name="auditType.canContractorView" />
				</li>
				<li><label>Contractor Can Edit:</label>
					<s:checkbox name="auditType.canContractorEdit" />
				</li>
				<li>
					<label>Permission to Edit:</label>
					<s:select name="editPerm" list="@com.picsauditing.access.OpPerms@values()" listValue="description" 
						headerKey="" headerValue="None" listKey="name()" value="editPerm"/>
					<div class="fieldhelp">
						<h3>Permission to Edit</h3>
						<p>For Operators and PICS Admins this will restrict the ability to edit this audit
						type to that permission.  Selecting 'none' will cancel this if you have already selected
						a permission.</p>
					</div>
				</li>				
				<li><label>Set Workflow:</label>
					<s:select list="workFlowList" name="workFlowID" listKey="id" listValue="name" value="auditType.workFlow.id" 
					headerKey="0" headerValue="- Select Workflow -" />
				</li>
				<li>
					<label>Required By Operator:</label>
					<s:textfield name="operatorID" value="%{auditType.account.id}" />
					<div class="fieldhelp">
						<h3>Required By Operator</h3>
						<p>Add the operator or corporateID only if requested by 1 account. We may be removing this field soon in favor of Audit Type Rules.</p>
					</div>
				</li>
				<li><label>Months to Expire:</label>
					<s:textfield name="auditType.monthsToExpire" /> 
				</li>
				<li><label>Email Template:</label>
					<s:select list="templateList" name="emailTemplateID" 
						headerKey="" headerValue="- Email Template -"
						listKey="id" listValue="templateName" />
					<div class="fieldhelp">
						<h3>Email Template</h3>
						<p>This is the template that will be sent to the contractor when the audit is completed.</p>
					</div>
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" name="button" value="Save"/>
			<input type="button" class="picsbutton" value="Copy" onclick="copyAuditType(<s:property value="id"/>)"/>
			<s:if test="auditType.id > 0 && auditType.categories.size == 0">
				<input id="deleteButton" type="submit" class="picsbutton negative" name="button" value="Delete"/>
			</s:if>
			<input type="submit" class="picsbutton" name="button" value="UpdateAllAudits"/>
		</fieldset>
	</div>
</s:form>

<div id="wfStepVisual" style="border: 1px solid black; margin-top: 10px;">
<s:iterator value="adjMatrix" id="adjID" status="adjStatus">
	<s:property value="nodes.get(#adjStatus.index)" />
</s:iterator>
</div>

<s:if test="id > 0">
	<div>
		<ul id="list" class="list">
		<s:iterator value="auditType.topCategories">
		    <li id="item_<s:property value="id"/>" title="Drag and drop to change order"><s:property value="number"/>. <a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name.trim().length() == 0 ? 'empty' : name"/> </a></li>
		</s:iterator>
		</ul>
		
		<a id="manage_audit_types_add_new_category" class="add" href="ManageCategory.action?button=AddNew&parentID=<s:property value="auditType.id"/>&category.auditType.id=<s:property value="auditType.id"/>">Add New Category</a>
		<div id="list-info"></div>
	</div>
	<s:if test="auditType.categories.size > 1">
		<div class="info">Drag and drop categories to change their order</div>
		<br clear="all" />
	</s:if>
	<pics:permission perm="ManageAuditTypeRules">
		<div>
			<a class="edit" href="ManageAuditTypeHierarchy.action?id=<s:property value="auditType.id"/>">Manage Hierarchy</a>
			<h3>Related Rules</h3>
			<s:if test="relatedRules.size() == 0">
				<div class="alert">
					There are no rules for this audit type. <a href="AuditTypeRuleEditor.action?button=edit&rule.auditType.id=<s:property value="auditType.id"/>&rule.auditType.auditName=<s:property value="auditType.auditName"/>">Click here to create a rule for <s:property value="auditType.auditName"/></a>
				</div>
			</s:if>
			<s:else>
				<s:if test="relatedRules.size() >= 50">
					<div class="alert">
						There are too many rules to display here. <a href="AuditTypeRuleSearch.action?filter.auditType=<s:property value="auditType.auditName"/>&filter.auditTypeID=<s:property value="auditType.id"/>">Click here to view all rules for <s:property value="auditType.auditName"/>.</a>
					</div>
				</s:if>
				<table class="report">
					<s:set name="ruleURL" value="'AuditTypeRuleEditor.action'"/>
					<s:set name="categoryRule" value="false"/>
					<s:include value="rules/audit_rule_header.jsp"/>
					<s:iterator value="relatedRules" id="r">
						<s:include value="rules/audit_rule_view.jsp"/>
					</s:iterator>
				</table>
			</s:else>
		</div>
	</pics:permission>
</s:if>

<div id="copy_audit" class="thinking"></div>

</body>
</html>