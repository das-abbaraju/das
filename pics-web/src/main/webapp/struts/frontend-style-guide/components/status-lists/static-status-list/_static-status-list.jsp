<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_static</s:param>
    <s:param name="header_title">${section_title}: Static Status List</s:param>

    <s:param name="description">
Static status lists are similar to interactive status lists, except they are not clickable and do not link to other pages. Note that interactive elements are rounded and static elements are squared.
    </s:param>

    <s:param name="example_url">
        status-lists/static-status-list/_static-status-list-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">static-status-list</s:param>

    <s:param name="html_code">
&lt;div class="list-group skill-list"&gt;
    &lt;div class="list-group-item danger operator-skill"&gt;
        &lt;div class="row"&gt;
            &lt;div class="col-xs-10 col-sm-11 col-md-10"&gt;
                &lt;i class="icon-minus-sign-alt"&gt;&lt;/i&gt;Expired or Incomplete
            &lt;/div&gt;
            &lt;div class="col-xs-2 col-sm-1 col-md-2 text-center"&gt;
                &lt;i class="icon-map-marker" data-toggle="tooltip" data-placement="top" title="" data-original-title="Skill required due to assignment." data-container="body"&gt;&lt;/i&gt;
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
    &lt;div class="list-group-item warning operator-skill"&gt;
        &lt;div class="row"&gt;
            &lt;div class="col-xs-10 col-sm-11 col-md-10"&gt;
                &lt;i class="icon-warning-sign"&gt;&lt;/i&gt;Expiring
            &lt;/div&gt;
            &lt;div class="col-xs-2 col-sm-1 col-md-2 text-center"&gt;
                &lt;i class="icon-map-marker" data-toggle="tooltip" data-placement="top" title="" data-original-title="Skill required due to assignment." data-container="body"&gt;&lt;/i&gt;
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
    &lt;div class="list-group-item success operator-skill"&gt;
        &lt;div class="row"&gt;
            &lt;div class="col-xs-10 col-sm-11 col-md-10"&gt;
                &lt;i class="icon-ok-sign"&gt;&lt;/i&gt;Approved
            &lt;/div&gt;
            &lt;div class="col-xs-2 col-sm-1 col-md-2 text-center"&gt;
                &lt;i class="icon-map-marker" data-toggle="tooltip" data-placement="top" title="" data-original-title="Skill required due to assignment." data-container="body"&gt;&lt;/i&gt;
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>
</s:include>

