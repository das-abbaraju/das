<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
#breadcrumbs {
	width: 80%;
	border: 1px solid black;
	position: relative;
}
#breadcrumbs ul.crumbs {
	white-space: nowrap;
}
#breadcrumbs ul.crumbs li {
	font-size: 13px;
	border: 0px;
	display: inline;
	list-style-type: none;
	margin: 5px;
	position: relative;
}
#breadcrumbs ul.crumbs li ul li {
	display: block;
}

#breadcrumbs div.crumb_selector {
	position: absolute;
	top: 15px;
	background-color: white;
	border: 1px dotted black;
	z-index: 500;
}
</style>

<script type="text/javascript">
function selectCrumb(selector, url) {
	var id = $F(selector);
	if (id > 0) {
		window.location = url + '.action?id=' + id;
	}
}
</script>

<h1>Manage Audit Types</h1>

<s:form>
	<s:select list="auditTypes" value="auditType.auditTypeID" 
		listKey="auditTypeID" listValue="auditName"
		headerKey="0" headerValue="- AuditType -"
		onchange="selectCrumb(this, 'ManageAuditType');"></s:select>
	<s:if test="auditType != null">
		<s:select list="auditType.categories" value="category.id" 
			listKey="id" listValue="category"
			headerKey="0" headerValue="- Category -"
			onchange="selectCrumb(this, 'ManageCategory');"></s:select>
		<s:if test="category != null">
			<s:select list="category.subCategories" value="subCategory.id" 
				listKey="id" listValue="subCategory"
				headerKey="0" headerValue="- SubCategory -"
				onchange="selectCrumb(this, 'ManageSubCategory');"></s:select>
			<s:if test="subCategory != null">
				<s:select list="subCategory.questions" value="question.questionID" 
					listKey="questionID" listValue="question"
					headerKey="0" headerValue="- Question -"
					onchange="selectCrumb(this, 'ManageQuestion');"></s:select>
			</s:if>
		</s:if>
	</s:if>
</s:form>
