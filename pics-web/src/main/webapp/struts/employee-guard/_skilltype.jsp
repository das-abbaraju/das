<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="SKILL_TYPE">${param.skillType}</s:set>
<s:if test="#SKILL_TYPE !=null && #SKILL_TYPE== 'Certification'">
  <s:text name="SKILL_TYPE_CERTIFICATION" />
</s:if>
<s:elseif test="#SKILL_TYPE !=null && #SKILL_TYPE == 'Training'">
  <s:text name="SKILL_TYPE_TRAINING" />
</s:elseif>
