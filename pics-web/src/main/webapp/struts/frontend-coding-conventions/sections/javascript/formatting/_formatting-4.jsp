<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Assignment statements spanning multiple lines (such as object assignments) should be preceded by an empty line.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-4</s:param>

    <s:param name="example_code">
BAD:
var myVar = 2,
    myObj = {
        foo: 1,
        bar: 2
    };

GOOD:
var myVar = 2,

    myObj = {
        foo: 1,
        bar: 2
    };
    </s:param>
</s:include>