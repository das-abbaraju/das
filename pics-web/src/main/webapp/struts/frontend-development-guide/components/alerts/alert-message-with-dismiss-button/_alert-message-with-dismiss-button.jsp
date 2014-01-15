<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_alert_with_dismiss_button</s:param>
    <s:param name="header_title">${section_title}: Alert with Dismiss Button</s:param>

    <s:param name="description">
An optional alert that can be dismissed by clicking on the “x”, most commonly used if Canceling or ignoring will be the most likely actions. A save confirmation is a good example, seeing as it will close on its own after a specified time, but you can close the alert early if it’s in the way or you’ve acknowledged it.
    </s:param>

    <s:param name="example_url">
        alerts/alert-message-with-dismiss-button/_alert-message-with-dismiss-button-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">alert-message-with-optional-dismiss-button</s:param>

    <s:param name="html_code">
&lt;div class="alert alert-danger alert-dismissable"&gt;
    &lt;button type="button" class="close" data-dismiss="alert" aria-hidden="true"&gt;&times;&lt;/button&gt;
    &lt;strong&gt;Oh snap!&lt;/strong&gt; Something is broken.
&lt;/div&gt;
    </s:param>
</s:include>