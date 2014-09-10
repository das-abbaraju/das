<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_stacked</s:param>
    <s:param name="header_title">${section_title}: Stacked Progress Bar</s:param>

    <s:param name="description">
Progress bars are used when conveying progress for a task that has multiple sections or components (e.g. some completed, some still in progress)
    </s:param>

    <s:param name="example_url">
        progress-bars/stacked-progress-bar/_stacked-progress-bar-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">stacked-progress-bar</s:param>

    <s:param name="html_code">
&lt;div class="progress"&gt;
    &lt;div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100" style="width: 50%"&gt;&lt;/div&gt;
    &lt;div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 20%"&gt;&lt;/div&gt;
    &lt;div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 10%"&gt;&lt;/div&gt;
&lt;/div&gt;

&lt;div class="progress"&gt;
    &lt;div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100" style="width: 50%"&gt;
        &lt;span&gt;27&lt;/span&gt;
    &lt;/div&gt;
    &lt;div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 15%"&gt;
        &lt;span&gt;8&lt;/span&gt;
    &lt;/div&gt;
    &lt;div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100" style="width: 35%"&gt;
        &lt;span&gt;19&lt;/span&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>

    <s:param name="struts_code">
    </s:param>
</s:include>