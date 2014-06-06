<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Declare all variables that are local to a function at the top of that function, even when the variable is not initialized until later.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-7</s:param>

    <s:param name="example_code">
BAD:
function myFn() {
    var foo = 10;
    updateFoo(foo);
    var bar = getBarFromFoo(foo);
    doSomethingWithBar(bar);
}

GOOD:
function myFn() {
    var foo = 10,
        bar;

    updateFoo(foo);

    bar = getBarFromUpdatedFoo(foo);

    doSomethingWithBar(bar);
}
    </s:param>
</s:include>