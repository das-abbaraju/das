<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid assigning styles in JavaScript code. Instead, assign classes, and define the styles in CSS.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-25</s:param>

    <s:param name="example_code">
BAD:
$myElement.style('color', 'red');

GOOD:
myElement.addClass('warning');

In CSS:
.warning {
    color: red;
}
    </s:param>
</s:include>