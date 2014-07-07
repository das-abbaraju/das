<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Use ternaries only in assignment statements and only when the expressions are simple.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-6</s:param>

    <s:param name="example_code">
BAD:
foo > 5 && bar < 10 ? foo() : bar ? foobar() : barfoo();

GOOD:
var (foo > 10) ? foobar() : barfoo();
    </s:param>
</s:include>