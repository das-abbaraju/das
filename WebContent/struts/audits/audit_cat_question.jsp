<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="conAudit.auditType.classType.policy">
	<s:if test="mode == 'Edit' || (policyWithOtherOperators && (#a == null || #a.answer == null))">
		<s:include value="audit_question_edit.jsp"></s:include>
	</s:if>
	<s:else>
		<s:include value="audit_question_view.jsp"></s:include>
	</s:else>
</s:if>
<s:else>
	<s:if test="mode == 'View'">
		<s:include value="audit_question_view.jsp"></s:include>
	</s:if>
	<s:if test="mode == 'Edit'">
		<s:include value="audit_question_edit.jsp"></s:include>
	</s:if>
	<s:if test="mode == 'Verify'">
		<s:include value="audit_question_edit.jsp"></s:include>
	</s:if>
</s:else>
