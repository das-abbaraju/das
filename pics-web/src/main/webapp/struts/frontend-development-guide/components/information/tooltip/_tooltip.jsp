<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_tooltip</s:param>
    <s:param name="header_title">${section_title}: Tooltip</s:param>

    <s:param name="description">
Convention of usage is to be determined.
    </s:param>

    <s:param name="example_url">
        information/tooltip/_tooltip-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">tooltip</s:param>

    <s:param name="html_code">
&lt;div class="toolip-container col-md-1 col-xs-1"&gt;
   &lt;i
        class="icon-info-sign icon-large"
        title="This is a sentence that explains the purpose of the form field"
        data-toggle="tooltip"
        data-container="body"&gt;
        data-placement="right"
        data-original-title="This is a sentence that explains the purpose of the form field."
    &lt;/i&gt;
&lt;/div&gt;
    </s:param>
</s:include>