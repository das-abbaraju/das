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
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />


<s:form id="save">
	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{category.auditType.auditTypeID}" />
	<s:hidden name="category.auditType.auditTypeID" />
	<fieldset>
	<legend><span>Category</span></legend>
		<ol>
			<li><label>ID:</label>
				<s:if test="category.id > 0">
					<s:property value="category.id" />
				</s:if>
					<s:else>NEW</s:else>
			</li>
			<li><label>Category Name:</label>
				<s:textfield name="category.category" size="30" />
			</li>
		</ol>
	</fieldset>
	<fieldset class="submit">
		<div class="buttons">
			<button class="positive" name="button" type="submit" value="save">Save</button>
				<s:if test="category.subCategories.size == 0">
					<button name="button" class="negative" type="submit" value="delete">Delete</button>
				</s:if>
		</div>
	</fieldset>
</s:form>

<s:if test="id != 0">
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
	<a href="AuditCat.action?catID=<s:property value="category.id" />">Preview Category</a>&nbsp;&nbsp;
	<a href="ManageSubCategory.action?button=AddNew&parentID=<s:property value="category.id"/>&subCategory.category.id=<s:property value="category.id"/>">Add
		New Sub Category</a> <script type="text/javascript">
			//<![CDATA[Sortable.create("list",{onUpdate:function(){new Ajax.Updater('list-info', 'OrderAuditChildrenAjax.action?id=<s:property value="category.id"></s:property>&type=AuditCategory', {asynchronous:true, evalScripts:true, onComplete:function(request){new Effect.Highlight("list",{});}, parameters:Sortable.serialize("list")})}})	//]]>
	</script>
	<div id="list-info"></div>
	</div>
</s:if>
</body>
</html>