<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Use for..in loops only when iterating over object keys. Use a basic for when iterating over arrays.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-19</s:param>

    <s:param name="example_code">
BAD:
var items = ['item1', 'item2', 'item3'];

for (var i in items) {
    ...
}

GOOD:
var items = [
    'item1',
    'item2',
    'item3'
];

for (var i = 0; i < items.length; i++) {
    ...
}

var myObj = {
    prop1: 'prop1',
    prop2: 'prop2'
};

for (var prop in myObj) {
    ...
}
    </s:param>
</s:include>