<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Format CSS rules with opening braces on the same line as the selector and 4-space tabs before each declaration.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">css-formatting-4</s:param>

    <s:param name="example_code">
BAD:
.myClass
{
    color: blue;
}

GOOD:
.myClass {
    color: blue;    
}
    </s:param>
</s:include>