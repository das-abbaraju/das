<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="header_title">Image</s:param>
    <s:param name="section_id">image</s:param>

    <s:param name="description">
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta.
    </s:param>

    <s:param name="example_url">
        image/_image-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">images</s:param>

    <s:param name="html_code">
&lt;img src="..." alt="..." class="img-rounded"&gt;
&lt;img src="..." alt="..." class="img-circle"&gt;
&lt;img src="..." alt="..." class="img-thumbnail"&gt;
    </s:param>

<%--     <s:param name="struts_code">
&lt;s:include value="/struts/employee-guard/employee/photo/_photo.jsp"&gt;
    &lt;s:url action="employee" method="photo" var="image_url"&gt;
        &lt;s:param name="id"&gt;0&lt;/s:param&gt;
    &lt;/s:url&gt;
    &lt;s:set var="alt_text"&gt;Profile photo&lt;/s:set&gt;
&lt;/s:include&gt;

_photo.jsp:

&lt;figure class="employee-image img-thumbnail"&gt;
    &lt;tw:input inputName="photo" type="file" /&gt;

    &lt;img src="${image_url}" class="img-responsive" alt="${alt_text}" /&gt;

    &lt;div class="overlay-container"&gt;
        &lt;div class="overlay"&gt;&lt;/div&gt;
        &lt;span class="edit-text"&gt;&lt;strong&gt;Select to edit...&lt;/strong&gt;&lt;/span&gt;
    &lt;/div&gt;
&lt;/figure&gt;
    </s:param> --%>
</s:include>