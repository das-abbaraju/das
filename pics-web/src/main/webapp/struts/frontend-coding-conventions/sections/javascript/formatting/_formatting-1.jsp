<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Always start curly braces on the same line as whatever they're opening.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-1</s:param>

    <s:param name="example_code">
BAD:
if (myCondition)
{
    ...
}
else
{
    ...
}

GOOD:
if (myCondition) {
    ...
} else {
    ...
}
    </s:param>
</s:include>