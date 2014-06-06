<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid making assignments, incrementing or producing other side effects within conditional expressions.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-24</s:param>

    <s:param name="example_code">
BAD:
if (myVar = 3) {
    ...
}

if (++myCounter > 10) {
    ...
}
    </s:param>
</s:include>