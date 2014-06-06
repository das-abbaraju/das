<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid cluttering handlers with logic. Handler functions should be named generically (e.g., onClick or handleClick), and should serve only to initiate tasks performed by other functions (whose names are more descriptive).
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-11</s:param>

    <s:param name="example_code">
BAD:
function onClick() {
    if (foo > 5) {
        ...
    }

    for (var i = 0; i < bars; i++) {
        ...
    }
}

GOOD:
function onClick() {
    doSomethingWithFoo(foo);
    doSomethingWithBars(bars);
}
    </s:param>
</s:include>