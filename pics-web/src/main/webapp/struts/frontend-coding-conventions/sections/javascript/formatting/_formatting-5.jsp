<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
List undefined variables together on the last line.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-5</s:param>

    <s:param name="example_code">
BAD:
var foo = 2,
    foobar,
    bar = 3,
    barfoo;

GOOD:
var foo = 2,
    bar = 3,
    foobar, barfoo;
    </s:param>
</s:include>