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
	<a class="blueMain" href="ManageAuditType.action">Top</a>
	<s:if test="auditType != null">
		&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain" href="ManageAuditType.action?id=<s:property value="auditType.auditTypeID"/>"><s:property value="auditType.auditName"/></a>&nbsp;&nbsp;&gt;&nbsp;
		<s:if test="category == null">
		<s:select list="auditType.categories" value="category.id" 
			listKey="id" listValue="category"
			headerKey="0" headerValue="- Category -"
			onchange="selectCrumb(this, 'ManageCategory');">
		</s:select>
		</s:if>
		<s:if test="category != null && category.id > 0">
			<a class="blueMain" href="ManageCategory.action?id=<s:property value="category.id"/>"><s:property value="category.category"/></a>&nbsp;&nbsp;&gt;&nbsp;
			<s:if test="subCategory == null">			
			<s:select list="category.subCategories" value="subCategory.id" 
				listKey="id" listValue="subCategory"
				headerKey="0" headerValue="- SubCategory -"
				onchange="selectCrumb(this, 'ManageSubCategory');">
			</s:select>
			</s:if>				
			<s:if test="subCategory != null">
				<a class="blueMain" href="ManageSubCategory.action?id=<s:property value="subCategory.id"/>"><s:property value="subCategory.subCategory"/></a>&nbsp;&nbsp;&gt;&nbsp;
				<s:if test="question == null">
				<s:select list="subCategory.questions" value="question.questionID" 
					listKey="questionID" listValue="question.length()>50 ? question.substring(0,47) + '...' : question"
					headerKey="0" headerValue="- Question -"
					onchange="selectCrumb(this, 'ManageQuestion');">
				</s:select>
				</s:if>
			</s:if>
			<s:if test="question != null">
				<a class="blueMain" href="ManageQuestion.action?id=<s:property value="question.questionID"/>"><s:property value="question.question"/></a>				
			</s:if>
		</s:if>
	</s:if>
</s:form>
