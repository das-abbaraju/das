<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Follow the principles of unobtrusive JavaScript: Avoid inline and internal scripting--that is, do not include JavaScript anywhere in HTML. 
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-26</s:param>

    <s:param name="example_code">
BAD:
&lt;body&gt;
    &lt;div onclick="doSomething()"&gt; &lt;!-- Bad --&gt;
    &lt;script&gt; &lt;!-- Also bad --&gt;
        ... JavaScript code ...
    &lt;/script&gt;
&lt;/body&gt;
    </s:param>
</s:include>