<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_basic</s:param>
    <s:param name="header_title">${section_title}: Basic Progress Bar</s:param>

    <s:param name="description">
Progress bars are used when conveying progress for longer tasks. They are color-coded to indicate status (e.g. completed, pending, expiring, overdue, etc.)
    </s:param>

    <s:param name="example_url">
        progress-bars/basic-progress-bar/_basic-progress-bar-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">progress-bars</s:param>

    <s:param name="html_code">
&lt;div class="progress progress-primary"&gt;
    &lt;div class="progress-bar" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 80%"&gt;&lt;/div&gt;
&lt;/div>

&lt;div class="progress progress-primary"&gt;
    &lt;div class="progress-bar" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 100%"&gt;
        &lt;span&gt;Submitted&lt;/span&gt;
    &lt;/div&gt;
&lt;/div&gt;

&lt;div class="progress progress-info"&gt;...&lt;/div&gt;
&lt;div class="progress progress-success"&gt;...&lt;/div&gt;
&lt;div class="progress progress-warning"&gt;...&lt;/div&gt;
&lt;div class="progress progress-danger"&gt;...&lt;/div&gt;
    </s:param>
</s:include>

