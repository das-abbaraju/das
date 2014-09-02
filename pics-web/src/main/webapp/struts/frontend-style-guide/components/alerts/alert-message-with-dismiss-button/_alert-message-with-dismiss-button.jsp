<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_alert_with_dismiss_button</s:param>
    <s:param name="header_title">${section_title}: Alert with Dismiss Button</s:param>

    <s:param name="description">
Alerts with Dismiss Buttons are used when cancelling or ignoring the alerts is the most likely action (e.g. save confirmation).
    </s:param>

    <s:param name="example_url">
        alerts/alert-message-with-dismiss-button/_alert-message-with-dismiss-button-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">alert-message-with-optional-dismiss-button</s:param>

    <s:param name="html_code">
&lt;div class="alert alert-success alert-dismissable"&gt;
    &lt;button type="button" class="close" data-dismiss="alert" aria-hidden="true"&gt;&times;&lt;/button&gt;
    &lt;strong&gt;Success!&lt;/strong&gt; Your changes have been saved.
&lt;/div&gt;
    </s:param>
</s:include>