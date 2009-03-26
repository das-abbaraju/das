<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects,dragdrop,controls" type="text/javascript"></script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<s:if test="auditType.id > 0">
	<div><a href="AuditOperator.action?aID=<s:property value="auditType.id"/>">Edit Operator Access</a></div>
</s:if>
<table>
	<tr>
		<td style="vertical-align: top">
		<s:form id="save">
		<s:hidden name="id"></s:hidden>
			<div>
				<fieldset class="form">
				<legend><span>Audit Type</span></legend>
					<ol>
						<li><label>ID:</label>
							<s:if test="auditType.id > 0"><s:property value="auditType.id" /></s:if>
							<s:else>NEW</s:else>
						</li>
						<li><label>Name:</label>
							<s:textfield name="auditType.auditName"></s:textfield>
						</li>
						<li><label>Class:</label>
							<s:select list="classList" name="auditType.classType"></s:select>
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
						<li><label>Is Scheduled:</label>
							<s:checkbox name="auditType.scheduled" />
						</li>
						<li><label>Has Auditor:</label>
							<s:checkbox name="auditType.hasAuditor" />
						</li>
						<li><label>Contractor Can View:</label>
							<s:checkbox name="auditType.canContractorView" />
						</li>
						<li><label>Contractor Can Edit:</label>
							<s:checkbox name="auditType.canContractorEdit" />
						</li>
						<li><label>Months to Expire:</label>
							<s:textfield name="auditType.monthsToExpire" /> 
						</li>
						<li><label>Order:</label>
							<s:textfield name="auditType.displayOrder" />
						</li>
					</ol>
				</fieldset>
				<br clear="all">
			</div>
			<br clear="all">
			<div class="buttons">
				<input type="submit" class="picsbutton positive" name="button" value="Save"/>
				<s:if test="auditType.id > 0 && auditType.categories.size == 0">
					<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
				</s:if>
				<input type="submit" class="picsbutton" name="button" value="UpdateAllAudits"/>
			</div>
		</s:form>
		</td>
		<s:if test="id > 0">
		<td style="vertical-align: top">
			<div>
				<ul id="list">
				<s:iterator value="auditType.categories">
				    <li id="item_<s:property value="id"/>" title="Drag and drop to change order"><s:property value="number"/>. <a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="category"/> </a></li>
				</s:iterator>
				</ul>
				
				<a href="ManageCategory.action?button=AddNew&parentID=<s:property value="auditType.id"/>&category.auditType.id=<s:property value="auditType.id"/>">Add New Category</a>
				<script type="text/javascript">
				//<![CDATA[
				Sortable.create("list", 
					{onUpdate:function(){new Ajax.Updater('list-info', 'OrderAuditChildrenAjax.action?id=<s:property value="auditType.id"></s:property>&type=AuditType', {asynchronous:true, evalScripts:true, onComplete:function(request){new Effect.Highlight("list",{});}, parameters:Sortable.serialize("list")})}})
				//]]>
				</script>
				<div id="list-info"></div>
			</div>
			<s:if test="auditType.categories.size > 1">
				<div id="info">Drag and drop categories to change their order</div>
				<br clear="all" />
			</s:if>
		</td>
		</s:if>
	</tr>
</table>
</body>
</html>