<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="#mode == 'View' || #mode == 'ViewAll'">
	<s:include value="audit_question_view.jsp"></s:include>
</s:if>
<s:if test="#mode == 'Edit'">
 	<s:if test="isCanEditCategory(#category)">
		<s:include value="audit_question_edit.jsp"></s:include>
	</s:if><s:else>
		<s:include value="audit_question_view.jsp"></s:include>
	</s:else>
</s:if>
<s:if test="#mode == 'Verify'">
	<s:include value="audit_question_edit.jsp"></s:include>
</s:if>
