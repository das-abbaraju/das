<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Write CSS classes in spinal-case. Write element ids in snake_case.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">css-naming-conventions-2</s:param>

    <s:param name="example_code">
GOOD:
&lt;div id="my_element_id" class="my-css-class"&gt;
    </s:param>
</s:include>