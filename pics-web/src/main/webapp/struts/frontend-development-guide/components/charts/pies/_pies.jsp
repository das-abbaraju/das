<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="header_title">${section_title}: Pie</s:param>
    <s:param name="section_id">${section_id_prefix}_pie</s:param>

    <s:param name="description">
Description unavailable
    </s:param>

    <s:param name="example_url">
        charts/pies/_pies-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">pies</s:param>

    <s:param name="html_code">
&lt;div data-widget-type="GoogleChart" data-url="js/operator/single-series.json" data-chart-type="Pie" data-style-type="Basic"&gt;&lt;/div&gt;

&lt;div data-widget-type="GoogleChart" data-url="js/operator/single-series.json" data-chart-type="Pie" data-style-type="Flags"&gt;&lt;/div&gt;
    </s:param>
</s:include>