<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_interactive</s:param>
    <s:param name="header_title">${section_title}: Interactive Status List</s:param>

    <s:param name="description">
Status lists are a quick reference for item status. Icons and colors are used to easily distinguish between items that do or do not need speicial attention. Clicking on an interactive status list will open or redirect to more information. These lists are currently an EmployeeGUARD-specific component used to display Skill status. This component is only available in Bootstrap3 and Angular.
    </s:param>

    <s:param name="example_url">
        status-lists/interactive-status-list/_interactive-status-list-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">interactive-status-list</s:param>

    <s:param name="html_code">
&lt;div class="list-group skill-list"&gt;
    &lt;a href="#" class="list-group-item expired"&gt;
        &lt;i class="icon-minus-sign-alt"&gt;&lt;/i&gt;Some List Item
    &lt;/a&gt;
    &lt;a href="#" class="list-group-item expiring"&gt;
        &lt;i class="icon-warning-sign"&gt;&lt;/i&gt;Some List Item
    &lt;/a&gt;
    &lt;a href="#" class="list-group-item pending"&gt;
        &lt;i class="icon-ok-circle"&gt;&lt;/i&gt;Some List Item
    &lt;/a&gt;
    &lt;a href="#" class="list-group-item complete"&gt;
        &lt;i class="icon-ok-sign"&gt;&lt;/i&gt;Some List Item
    &lt;/a&gt;
&lt;/div&gt;
    </s:param>
</s:include>

