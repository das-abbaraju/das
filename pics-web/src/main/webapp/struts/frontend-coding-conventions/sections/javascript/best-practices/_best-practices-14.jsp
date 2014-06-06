<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid excessive chaining.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-14</s:param>

    <s:param name="example_code">
BAD:
$('.some-element').click(handleClick).parents('.container').slideDown();

GOOD:
var $element = $('.some-element'),
    $parent = $element.parents('.container');

$element.click(handleClick);
$parent.slideDown();
    </s:param>
</s:include>