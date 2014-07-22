<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="CONTRACTOR.SKILL.CREATE.ADD_SKILL" /></s:param>
</s:include>

<div class="col-md-8">
	<s:include value="/struts/employee-guard/contractor/skill/_create-form.jsp" />
</div>