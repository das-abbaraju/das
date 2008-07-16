<%@ taglib prefix="s" uri="/struts-tags"%>
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
		<a href="ManageAuditType.action?id=<s:property value="auditType.auditTypeID"/>" style="text-decoration: none;">&lt;</a>
		<s:select list="auditType.categories" value="category.id" 
			listKey="id" listValue="category"
			headerKey="0" headerValue="- Category -"
			onchange="selectCrumb(this, 'ManageCategory');"></s:select>
		<s:if test="category != null">
			<a href="ManageCategory.action?id=<s:property value="category.id"/>" style="text-decoration: none;">&lt;</a>
			<s:select list="category.subCategories" value="subCategory.id" 
				listKey="id" listValue="subCategory"
				headerKey="0" headerValue="- SubCategory -"
				onchange="selectCrumb(this, 'ManageSubCategory');"></s:select>
			<s:if test="subCategory != null">
				<a href="ManageSubCategory.action?id=<s:property value="subCategory.id"/>" style="text-decoration: none;">&lt;</a>
				<s:select list="subCategory.questions" value="question.questionID" 
					listKey="questionID" listValue="question.length()>50 ? question.substring(0,47) + '...' : question"
					headerKey="0" headerValue="- Question -"
					onchange="selectCrumb(this, 'ManageQuestion');"></s:select>
			</s:if>
		</s:if>
	</s:if>
</s:form>
