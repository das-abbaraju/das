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
	<s:hidden name="parentID" value="%{category.auditType.id}" />
	<s:hidden name="category.auditType.id" />
	<fieldset class="form">
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
			<li><label># of Questions:</label>
				<s:property value="category.numQuestions"/>
			</li>
			<li><label># Required:</label>
				<s:property value="category.numRequired"/>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<div class="buttons">
			<input type="submit" class="picsbutton positive" name="button" value="Save"/>
				<s:if test="category.subCategories.size == 0">
					<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
				</s:if>
			<input type="submit" class="picsbutton" name="button" value="UpdateAllAuditsCategories"/>	
		</div>
	</fieldset>
</s:form>

<s:if test="id != 0">
	<div>
		<ul id="list" title="Drag and drop to change order">
			<s:iterator value="category.subCategories">
				<li id="item_<s:property value="id"/>"
					title="Drag and drop to change order"><s:property
					value="number" />.
					<a
					href="ManageSubCategory.action?id=<s:property value="id"/>"><s:property
					value="subCategory" /></a></li>
			</s:iterator>
		</ul>
		
		<a href="AuditCat.action?catID=<s:property value="category.id" />">Preview Category</a>&nbsp;&nbsp;
		<a href="ManageSubCategory.action?button=AddNew&parentID=<s:property value="category.id"/>&subCategory.category.id=<s:property value="category.id"/>">Add
			New Sub Category</a>
		<div id="list-info"></div>
	</div>
	<script type="text/javascript">
	//<![CDATA[
	Sortable.create("list",
		{onUpdate:function(){new Ajax.Updater('list-info', 'OrderAuditChildrenAjax.action?id=<s:property value="category.id"></s:property>&type=AuditCategory', {asynchronous:true, evalScripts:true, onComplete:function(request){new Effect.Highlight("list",{});}, parameters:Sortable.serialize("list")})}})
	//]]>
	</script>
</s:if>
</body>
</html>