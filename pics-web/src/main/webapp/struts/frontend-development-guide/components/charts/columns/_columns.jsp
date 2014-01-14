<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="header_title">${section_title}: Column</s:param>
    <s:param name="section_id">${section_id_prefix}_column</s:param>

    <s:param name="description">
Description unavailable
    </s:param>

    <s:param name="example_url">
        charts/columns/_columns-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">columns</s:param>

    <s:param name="html_code">
&lt;div data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row-column.json" data-chart-type="Column" data-style-type="Basic"&gt;&lt;/div&gt;
    </s:param>
</s:include>