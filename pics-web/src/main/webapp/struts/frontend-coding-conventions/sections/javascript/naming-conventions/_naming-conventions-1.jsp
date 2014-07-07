<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Use “that” when aliasing the "this" object, such as for use within callbacks.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-naming-conventions-1</s:param>

    <s:param name="example_code">
BAD:
var me = this;

setTimeout(function () {
    me.foo();
});

GOOD:
var that = this;

setTimeout(function () {
    that.foo();
});
    </s:param>
</s:include>