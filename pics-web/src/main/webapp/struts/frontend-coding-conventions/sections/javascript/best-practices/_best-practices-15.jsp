<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
If you are using a framework, prefer the functions provided by the framework to the native original. The exception is in JSPs using Struts: Avoid using Struts tags whenever possible.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-15</s:param>

    <s:param name="example_code">
When using Angular, use angular.forEach.
When using jQuery, use $.each.

(instead of the native JavaScript "for" and "for...in")
    </s:param>
</s:include>