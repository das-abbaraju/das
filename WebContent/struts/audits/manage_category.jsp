<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Category</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function(){
	var sortList = $('#list').sortable({
		update: function() {
			$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="category.id"></s:property>&type=AuditCategory', 
				sortList.sortable('serialize'), 
				function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
			);
		}
	});
});
</script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />


<s:form id="save">
	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{category.auditType.id}" />
	<s:hidden name="category.auditType.id" />
	<fieldset class="form">
	<h2 class="formLegend">Category</h2>
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
			<li><label>Apply on Question:</label>
				<s:textfield name="applyOnQuestionID" />
				<s:if test="applyOnQuestionID > 0"><a href="ManageQuestion.action?id=<s:property value="applyOnQuestionID" />">Show</a></s:if>
			</li>
			<li><label>When Answer is:</label>
				<s:textfield name="category.applyOnAnswer" />
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<div>
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
		
		<a class="preview" href="AuditCat.action?catID=<s:property value="category.id" />">Preview Category</a>&nbsp;&nbsp;
		<a class="add" href="ManageSubCategory.action?button=AddNew&parentID=<s:property value="category.id"/>&subCategory.category.id=<s:property value="category.id"/>">Add New Sub Category</a>
		<div id="list-info"></div>
	</div>
</s:if>
</body>
</html>