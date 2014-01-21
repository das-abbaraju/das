<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_stacked</s:param>
    <s:param name="header_title">${section_title}: Stacked Progress Bar</s:param>

    <s:param name="description">
Used when conveying progress for a task with multiple sections or components (e.g. some completed, some still in progress)
    </s:param>

    <s:param name="example_url">
        progress-bars/stacked-progress-bar/_stacked-progress-bar-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">stacked-progress-bar</s:param>

    <s:param name="html_code">
&lt;div class="progress"&gt;
    &lt;div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100" style="width: 50%"&gt;
        &lt;span class="sr-only"&gt;50% Complete (success)&lt;/span&gt;
    &lt;/div&gt;
    &lt;div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 20%"&gt;
        &lt;span class="sr-only"&gt;20% Complete (warning)&lt;/span&gt;
    &lt;/div&gt;
    &lt;div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 10%"&gt;
        &lt;span class="sr-only"&gt;10% Complete (danger)&lt;/span&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>

    <s:param name="struts_code">
    </s:param>
</s:include>