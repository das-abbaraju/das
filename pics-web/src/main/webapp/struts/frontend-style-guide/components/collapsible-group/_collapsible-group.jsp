<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">Collapsible Group</s:param>
    <s:param name="section_id">collapsible-group</s:param>

    <s:param name="description">
Collapsible groups are used to maintain a clean UI by initially hiding secondary information.
    </s:param>

    <s:param name="example_url">
        collapsible-group/_collapsible-group-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">collapsible-groups-items</s:param>

    <s:param name="html_code">
&lt;div class="panel-group" id="accordion_1"&gt;
    &lt;div class="panel panel-default"&gt;
        &lt;div class="panel-heading"&gt;
            &lt;h4 class="panel-title"&gt;
                &lt;a data-toggle="collapse" data-parent="#accordion_1" href="#item_1"&gt;
                    Item 1 Heading
                &lt;/a&gt;
            &lt;/h4&gt;
        &lt;/div&gt;
        &lt;div id="item_1" class="panel-collapse collapse"&gt;
            &lt;div class="panel-body"&gt;
                Item 1 Content
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
    &lt;div class="panel panel-default"&gt;
        &lt;div class="panel-heading"&gt;
            &lt;h4 class="panel-title"&gt;
                &lt;a data-toggle="collapse" data-parent="#accordion_2" href="#item_2"&gt;
                    Item 2 Heading
                &lt;/a&gt;
            &lt;/h4&gt;
        &lt;/div&gt;
        &lt;div id="item_2" class="panel-collapse collapse"&gt;
            &lt;div class="panel-body"&gt;
                Item 2 Content
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>
</s:include>