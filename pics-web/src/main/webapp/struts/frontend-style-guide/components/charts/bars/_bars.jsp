<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">${section_title}: Bar</s:param>
    <s:param name="section_id">${section_id_prefix}_bar</s:param>

    <s:param name="description">
Description unavailable
    </s:param>

    <s:param name="example_url">
        charts/bars/_bars-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">bars</s:param>

    <s:param name="html_code">
&lt;div data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row.json" data-chart-type="Bar" data-style-type="Basic">&lt;/div&gt;

&lt;div data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row.json" data-chart-type="Bar" data-style-type="StackedFlags"&gt;&lt;/div&gt;

&lt;div data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row.json" data-chart-type="Bar" data-style-type="Basic"&gt;&lt;/div&gt;

&lt;div data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row.json" data-chart-type="Bar" data-style-type="Flags"&gt;&lt;/div&gt;
    </s:param>
</s:include>