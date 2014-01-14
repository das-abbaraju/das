<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_action_buttons</s:param>
    <s:param name="header_title">${section_title}: Form Buttons</s:param>

    <s:param name="description">
When used within a form, this is how a button is to be implemented.
    </s:param>

    <s:param name="example_url">
        forms/action-button/_action-button-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">action-buttons</s:param>

    <s:param name="html_code">
&lt;div class="form-group"&gt;
    &lt;div class="col-md-9 col-md-offset-3 form-actions"&gt;
        &lt;button name="someName6" type="submit" class="btn btn-success" tabindex="5"&gt;Some Action&lt;/button&gt;
        &lt;a href="#" class="btn btn-default" tabindex="6"&gt;Some Action&lt;/a&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>
</s:include>