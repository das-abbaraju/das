<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="header_title">Status List</s:param>
    <s:param name="section_id">status_list</s:param>

    <s:param name="description">
Status lists are a quick reference for item status. Icons and colors are used to easily distinguish between items that do or do not need special attention.
    </s:param>

    <s:param name="example_url">
        status-list/_status-list-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">status-list</s:param>

    <s:param name="html_code">
&lt;div class="employee_guard_contractor_employee-page"&gt;
    &lt;div class="list-group skill-list"&gt;
        &lt;a href="#" class="list-group-item expiring"&gt;
            &lt;i class="icon-warning-sign"&gt;&lt;/i&gt;Some List Item
        &lt;/a&gt;
        &lt;a href="#" class="list-group-item expired"&gt;
            &lt;i class="icon-minus-sign-alt"&gt;&lt;/i&gt;Some List Item
        &lt;/a&gt;
        &lt;a href="#" class="list-group-item pending"&gt;
            &lt;i class="icon-ok-circle"&gt;&lt;/i&gt;Some List Item
        &lt;/a&gt;
        &lt;a href="#" class="list-group-item complete"&gt;
            &lt;i class="icon-ok-sign"&gt;&lt;/i&gt;Some List Item
        &lt;/a&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>

<%--     <s:param name="struts_code">
&lt;div class="list-group skill-list"&gt;
    &lt;s:iterator var="someCollection" value="someObj"&gt;
        &lt;s:set var="status"&gt;$&#123;someObj.status.displayValue&#125;&lt;/s:set&gt;

        &lt;s:url action="#" var="status_url"&gt;
            &lt;s:param name="some_param_name"&gt;$&#123;someObj.someProp&#125;&lt;/s:param&gt;
        &lt;/s:url&gt;

        &lt;s:set var="status_icon"&gt;icon-ok-sign&lt;/s:set&gt;
        &lt;s:if test="#someObj.status.expired"&gt;
            &lt;s:set var="status_icon"&gt;icon-minus-sign-alt&lt;/s:set&gt;
        &lt;/s:if&gt;
        &lt;s:elseif test="#someObj.status.expiring"&gt;
            &lt;s:set var="status_icon"&gt;icon-warning-sign&lt;/s:set&gt;
        &lt;/s:elseif&gt;
        &lt;s:elseif test="#someObj.status.pending"&gt;
            &lt;s:set var="status_icon"&gt;icon-ok-circle&lt;/s:set&gt;
        &lt;/s:elseif&gt;
        &lt;s:elseif test="#someObj.status.complete"&gt;
            &lt;s:set var="status_icon"&gt;icon-ok-sign&lt;/s:set&gt;
        &lt;/s:elseif&gt;

        &lt;a href="$&#123;status_url&#125;" class="list-group-item $&#123;status&#125;"&gt;
            &lt;i class="$&#123;status_icon&#125;"&gt;&lt;/i&gt;$&#123;someObj.itemDescription&#125;
        &lt;/a&gt;
    &lt;/s:iterator&gt;
&lt;/div&gt;
    </s:param> --%>
</s:include>