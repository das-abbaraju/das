<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Place single line comments above the line they refer to.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">general-best-practices-1</s:param>

    <s:param name="example_code">
BAD:
if (foo == bar) { // This is a bad comment referring to the statement to the left

GOOD:
// This is a good comment referring to the statement below
if (foo == bar) {
    </s:param>
</s:include>