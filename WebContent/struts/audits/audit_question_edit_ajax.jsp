<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<s:set name="a" value="auditData" />
<s:set name="q" value="auditData.question" />
<s:set name="mode" value="mode" />
<s:set name="category" value="#q.category" />

<s:include value="audit_cat_question.jsp"></s:include>