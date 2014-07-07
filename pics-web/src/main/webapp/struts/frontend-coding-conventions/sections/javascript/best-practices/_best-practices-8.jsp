<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Create objects, arrays, and regular expressions using literals instead of constructors, unless the arguments to the constructor are dynamic.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-8</s:param>

    <s:param name="example_code">
BAD:
var myObj = new Object();
myObj.prop = 5;

var myArray = new Array('element1', 'element2', 'element3');

var myRegExp = new RegExp('^something$');

GOOD:
var myObj = {
    prop: 5
};

var myArray = [
    'element1',
    'element2',
    'element3'
];

var myRegExp = /^something$/;

var searchString = prompt('Enter a search string:'),
    myRegExp = new RegExp(searchString, 'g'); 
    </s:param>
</s:include>