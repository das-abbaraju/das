<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_help_link</s:param>
    <s:param name="header_title">${section_title}: Help Link</s:param>

    <s:param name="description">
Convention of usage is to be determined.
    </s:param>

    <s:param name="example_url">
        information/help-link/_help-link-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">help-link</s:param>

    <s:param name="html_code">
&lt;div class="toolip-container col-xs-1"&gt;
   &lt;i
        class="icon-question-sign icon-large"
        title=""
        data-original-title="Title of the Help Center Section"
        data-toggle="tooltip"
        data-placement="top"
        data-container="body"&gt;
    &lt;/i&gt;
&lt;/div&gt;
    </s:param>
</s:include>