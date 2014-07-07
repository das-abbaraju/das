<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
When defining objects, list only one property per line.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-3</s:param>

    <s:param name="example_code">
BAD:
var myObj = { foo: 2, bar: 5 };

GOOD:
var myObj = {
    foo: 2,
    bar: 5
};
    </s:param>
</s:include>