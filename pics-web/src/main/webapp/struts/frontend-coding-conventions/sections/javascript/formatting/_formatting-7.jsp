<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Enclose string values in single quotes.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-7</s:param>

    <s:param name="example_code">
BAD:
var myString = "John said, 'Hello world'";

GOOD:
var myString = 'John said, "Hello world"';
    </s:param>
</s:include>