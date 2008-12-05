<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
function selectCrumb(selector, url) {
	var id = $F(selector);
	if (id > 0) {
		window.location = url + '.action?id=' + id;
	}
}
</script>
<style>
<!--
#breadcrumbs {

}
-->
</style>

<h1>Manage Audit Types
	<s:if test="auditType != null"><span class="sub"><s:property value="auditType.auditName"/></span></s:if>
</h1>
<div id="breadcrumbs">
	<a class="blueMain" href="ManageAuditType.action">Top</a>
	<s:if test="auditType != null">
		<s:if test="category == null">
			&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain current" href="ManageAuditType.action?id=<s:property value="auditType.auditTypeID"/>"><s:property value="auditType.auditName"/></a>
		</s:if>
		<s:else>
			&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain" href="ManageAuditType.action?id=<s:property value="auditType.auditTypeID"/>"><s:property value="auditType.auditName"/></a>
		</s:else>
		<s:if test="category != null">
			<s:if test="subCategory == null">
				&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain current" href="ManageCategory.action?id=<s:property value="category.id"/>"><s:property value="category.category"/></a>
			</s:if>
			<s:else>
				&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain" href="ManageCategory.action?id=<s:property value="category.id"/>"><s:property value="category.category"/></a>
			</s:else>
			<s:if test="subCategory != null">
				<s:if test="question == null">
					&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain current" href="ManageSubCategory.action?id=<s:property value="subCategory.id"/>"><s:property value="subCategory.subCategory"/></a>
				</s:if>
				<s:else>
					&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain" href="ManageSubCategory.action?id=<s:property value="subCategory.id"/>"><s:property value="subCategory.subCategory"/></a>
				</s:else>
				<s:if test="question != null">
					&nbsp;&gt;&nbsp;&nbsp;<a class="blueSmall" href="ManageQuestion.action?id=<s:property value="question.id"/>"><s:property value="question.question"/></a>				
				</s:if>
			</s:if>
		</s:if>
	</s:if>
</div>

<s:include value="../actionMessages.jsp" />
