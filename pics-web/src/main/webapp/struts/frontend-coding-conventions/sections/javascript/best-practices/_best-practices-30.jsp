<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Don't pass strings to setTimeout or setInterval. Instead, pass functions.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-30</s:param>

    <s:param name="example_code">
BAD:
setTimeout('doSomething();', 1000);

GOOD:
setTimeout(doSomething, 1000);

function doSomething() {
    ...
}
    </s:param>
</s:include>