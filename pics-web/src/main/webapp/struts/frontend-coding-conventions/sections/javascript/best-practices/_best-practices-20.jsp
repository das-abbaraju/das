<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Use the || as a "default operator" when setting defaults.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-20</s:param>

    <s:param name="example_code">
BAD:
var foo = providedValue ? providedValue : defaultValue;

if (!providedValue) {
    foo = bar;
}

GOOD:
var foo = providedValue || defaultValue;
    </s:param>
</s:include>