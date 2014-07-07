<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid building HTML inside JavaScript code. If unavoidable, then construct it using a joined array.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-12</s:param>

    <s:param name="example_code">
BAD:
var $list = $('&lt;ul&gt;&lt;li&gt;' + items[0].name + '&lt;/li&gt;&lt;li&gt;' + items[1].name + '&lt;/li&gt;&lt;ul&gt;');

GOOD:
var $list = $([
    '&lt;ul&gt;',
        '&lt;li&gt;',
            items[0].name,
        '&lt;/li&gt;',
        '&lt;li&gt;',
            items[1].name,
        '&lt;/li&gt;',
    '&lt;/ul&gt;'
].join(''));
    </s:param>
</s:include>