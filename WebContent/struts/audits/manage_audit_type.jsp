<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js"></script>
<script type="text/javascript">
$(function(){
	var sortList = $('#list').sortable({
		update: function() {
			$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="auditType.id"/>&type=AuditType', 
				sortList.sortable('serialize'), 
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
});
</script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<s:if test="auditType.id > 0">
	<div><a href="AuditOperator.action?aID=<s:property value="auditType.id"/>">Edit Operator Access</a></div>
</s:if>
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
				</li>
				<li><label>Class:</label>
					<s:select list="classList" name="auditType.classType" />
				</li>
				<li><label>Description:</label>
					<s:textfield name="auditType.description"></s:textfield>
				</li>
				<li><label>Has Requirements:</label>
					<s:checkbox name="auditType.hasRequirements" />
				</li>
				<li><label>Must Verify:</label>
					<s:checkbox name="auditType.mustVerify" />
				</li>
				<li><label>Has Multiple:</label>
					<s:checkbox name="auditType.hasMultiple" />
				</li>
				<li><label>Can Renew:</label>
					<s:checkbox name="auditType.renewable" />
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
					<label>Required By Operator:</label>
					<s:textfield name="operatorID" value="%{auditType.account.id}" />
					<div class="fieldhelp">
						<h3>Required By Operator</h3>
						<p>Add the operator or corporateID only if requested by 1 account.</p>
					</div>
				</li>
				<li><label>Months to Expire:</label>
					<s:textfield name="auditType.monthsToExpire" /> 
				</li>
				<li><label>Order:</label>
					<s:textfield name="auditType.displayOrder" />
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
		<br clear="all">
	</div>
	<br clear="all">
	<div>
		<input type="submit" class="picsbutton positive" name="button" value="Save"/>
		<s:if test="auditType.id > 0 && auditType.categories.size == 0">
			<input id="deleteButton" type="submit" class="picsbutton negative" name="button" value="Delete"/>
		</s:if>
		<input type="submit" class="picsbutton" name="button" value="UpdateAllAudits"/>
	</div>
</s:form>

<s:if test="id > 0">
	<div>
		<ul id="list">
		<s:iterator value="auditType.categories">
		    <li id="item_<s:property value="id"/>" title="Drag and drop to change order"><s:property value="number"/>. <a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="category.trim().length() == 0 ? 'empty' : category"/> </a></li>
		</s:iterator>
		</ul>
		
		<a id="manage_audit_types_add_new_category" href="ManageCategory.action?button=AddNew&parentID=<s:property value="auditType.id"/>&category.auditType.id=<s:property value="auditType.id"/>">Add New Category</a>
		<div id="list-info"></div>
	</div>
	<s:if test="auditType.categories.size > 1">
		<div class="info">Drag and drop categories to change their order</div>
		<br clear="all" />
	</s:if>
</s:if>
</body>
</html>