<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="a" value="auditData" />
<s:set name="q" value="auditData.question" />
<s:set name="parentAnswer" value="auditData.parentAnswer" />
<s:if test="#q.allowMultipleAnswers">
	<s:set name="parentAnswer" value="auditData" />
</s:if>

<s:include value="audit_cat_edit.jsp"></s:include>
