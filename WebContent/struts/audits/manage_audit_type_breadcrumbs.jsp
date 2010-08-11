<%@ taglib prefix="s" uri="/struts-tags"%>
<h1>Manage Audit Types
	<s:if test="auditType != null"><span class="sub"><s:property value="auditType.auditName"/></span></s:if>
</h1>
<div id="breadcrumbs">
	<a class="blueMain" href="ManageAuditType.action">Top</a>
	&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain<s:if test="category == null"> current</s:if>" href="ManageAuditType.action?id=<s:property value="auditType.id"/>"><s:property value="auditType.auditName"/></a>

	<s:if test="category != null">
		<s:if test="category.id > 0">
			<s:iterator value="category.ancestors" id="current">
				&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain<s:if test="category.id == #current.id"> current</s:if>" href="ManageCategory.action?id=<s:property value="#current.id"/>"><s:property value="#current.name"/></a>
			</s:iterator>
		</s:if>
	</s:if>

	<s:if test="categoryParent != null && categoryParent.id > 0">
		<s:iterator value="categoryParent.ancestors" id="current">
			&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain<s:if test="categoryParent.id == #current.id"> current</s:if>" href="ManageCategory.action?id=<s:property value="#current.id"/>"><s:property value="#current.name"/></a>
		</s:iterator>
	</s:if>
	
	<s:if test="question != null">
		&nbsp;&gt;&nbsp;&nbsp;<a class="blueSmall" href="ManageQuestion.action?id=<s:property value="question.id"/>"><s:property value="question.name"/></a>				
	</s:if>
</div>

<s:include value="../actionMessages.jsp" />
