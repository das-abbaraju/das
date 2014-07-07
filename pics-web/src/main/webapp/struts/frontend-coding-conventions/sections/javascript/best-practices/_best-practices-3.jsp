<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Write no more than one statement per line.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-3</s:param>

    <s:param name="example_code">
BAD:
doSomething(); doSomethingElse();

GOOD:
doSomething();
doSomethingElse();
    </s:param>
</s:include>