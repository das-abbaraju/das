<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid inline and internal styling--that is, do not include CSS anywhere in HTML.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">css-best-practices-3</s:param>

    <s:param name="example_code">
BAD:
&lt;style&gt;
    div {
        color: #fff
    }
&lt;/style&gt;
&lt;div style="color: red;"&gt; &lt;!-- Also bad --&gt;
    </s:param>
</s:include>