<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Separate each style rule by a blank line.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">css-formatting-5</s:param>

    <s:param name="example_code">
GOOD:
.myClass {
    …
}

.myOtherClass {
    ...
}
    </s:param>
</s:include>