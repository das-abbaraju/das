<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Declare variables scoped to the same function using a single var keyword at the top of that function.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-2</s:param>

    <s:param name="example_code">
BAD:
var foo;
var bar;

GOOD:
var foo, bar;

GOOD:
var foo = 1,
    bar = 3;
    </s:param>
</s:include>