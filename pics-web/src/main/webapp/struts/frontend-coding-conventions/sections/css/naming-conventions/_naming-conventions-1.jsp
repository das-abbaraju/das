<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Do not use CSS class names that describe style. Instead, choose CSS class names that describe semantics--i.e., what the content is or represents.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">css-naming-conventions-1</s:param>

    <s:param name="example_code">
BAD:
.red-background {
    background-color: red;
}

GOOD:
.warning {
    background-color: red;
}
    </s:param>
</s:include>