<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Write semantic markup. Choose tags that describe their contents. Except for tags necessary to implement Bootstrap styles, avoid using tags strictly for styling purposes.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">html-best-practices-1</s:param>

    <s:param name="example_code">
BAD:
&lt;div class="wrapper"&gt;
    &lt;ul&gt;
        &lt;li&gt;
            some content
        &lt;/li&gt;
    &lt;/ul&gt;
&lt;/div&gt;

In CSS:
.wrapper {
    padding: 20px;
}

GOOD:
&lt;ul&gt;
    &lt;li&gt;
        Some content
    &lt;/li&gt;
&lt;/ul&gt;

In CSS:
ul {
    margin: 20px;
}
    </s:param>
</s:include>