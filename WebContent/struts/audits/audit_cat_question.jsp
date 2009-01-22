<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="mode == 'View'">
	<s:include value="audit_cat_view.jsp"></s:include>
</s:if>
<s:if test="mode == 'Edit'">
	<s:include value="audit_cat_edit.jsp"></s:include>
</s:if>
<s:if test="mode == 'Verify'">
	<s:include value="audit_cat_edit.jsp"></s:include>
</s:if>
<s:if test="mode == 'ViewQ'">
	<s:include value="audit_cat_questions.jsp"></s:include>
</s:if>
