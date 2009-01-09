<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="mode == 'View'">
	<s:if test="onlyReq">
		<s:if test="hasRequirements">
			<s:include value="audit_cat_view.jsp"></s:include>
		</s:if>
	</s:if>
	<s:else>
		<s:if test="viewBlanks || answer.length() > 0">
			<s:include value="audit_cat_view.jsp"></s:include>
		</s:if>
	</s:else>
</s:if>
<s:if test="mode == 'Edit'">
	<s:if test="!onlyReq || hasRequirements">
		<s:include value="audit_cat_edit.jsp"></s:include>
	</s:if>
</s:if>
<s:if test="mode == 'Verify'">
	<s:if test="answer.length() > 0">	
		<s:include value="audit_cat_verify.jsp"></s:include>
	</s:if>
</s:if>
<s:if test="mode == 'ViewQ'">
	<s:include value="audit_cat_questions.jsp"></s:include>
</s:if>
