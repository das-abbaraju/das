<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Category</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects,dragdrop,controls" type="text/javascript"></script>
<style type="text/css">
#list {
	margin:0;
	margin-top:10px;
	padding:0;
	list-style-type: none;
	width:350px;
}
#list li {
	margin:0;
	margin-bottom:4px;
	padding:5px;
	border:1px solid #888;
	cursor:move;
}
</style>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<table>
	<tr>
		<td style="vertical-align: top">
		<s:form id="save">
			<s:hidden name="id" />
			<s:hidden name="parentID" value="%{category.auditType.auditTypeID}" />
			<s:hidden name="category.auditType.auditTypeID" />
			<table class="forms">
				<tr>
					<th>ID:</th>
					<td><s:if test="category.id > 0">
						<s:property value="category.id" />
					</s:if> <s:else>NEW</s:else></td>
				</tr>
				<tr>
					<th>Category Name:</th>
					<td><s:textfield name="category.category" size="30" /></td>
				</tr>
			</table>
			<div class="buttons">
			<button class="positive" name="button" type="submit" value="save">Save</button>
			<s:if test="category.subCategories.size == 0">
				<button name="button" type="submit" value="delete">Delete</button>
			</s:if></div>

		</s:form>
		</td>
		<s:if test="id != 0">
		<td style="vertical-align: top">
			<div>
			<ul id="list">
				<s:iterator value="category.subCategories">
					<li id="item_<s:property value="id"/>"
						title="Drag and drop to change order"><s:property
						value="number" />. <a
						href="ManageSubCategory.action?id=<s:property value="id"/>"><s:property
						value="subCategory" /></a></li>
				</s:iterator>
			</ul>
			<a
				href="ManageSubCategory.action?button=AddNew&parentID=<s:property value="category.id"/>&subCategory.category.id=<s:property value="category.id"/>">Add
			New Sub Category</a> <script type="text/javascript">
	//<![CDATA[
	Sortable.create("list", 
		{onUpdate:function(){new Ajax.Updater('list-info', 'OrderAuditChildrenAjax.action?id=<s:property value="category.id"></s:property>&type=AuditCategory', {asynchronous:true, evalScripts:true, onComplete:function(request){new Effect.Highlight("list",{});}, parameters:Sortable.serialize("list")})}})
	//]]>
	</script>
			<div id="list-info"></div>
			</div>

			</td>
		</s:if>
	</tr>
</table>

</body>

</html>