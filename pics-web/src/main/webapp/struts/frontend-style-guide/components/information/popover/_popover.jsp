<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_popover</s:param>
    <s:param name="header_title">${section_title}: Popover</s:param>

    <s:param name="description">
        Convention of usage is to be determined.

        <br/><br/>
        <div class="alert alert-info">
            <strong>Note</strong>
            Popovers require initialization via JavaScript e.g. <code>$(...).popover();</code> -- done inline in this example, though not recommended in practice.
        </div>
    </s:param>

    <s:param name="example_url">
        information/popover/_popover-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">popover</s:param>

    <s:param name="html_code">
&lt;div class="text-center"&gt;
    &lt;button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="left" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum."&gt;
        Popover on left
    &lt;/button&gt;

    &lt;button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="top" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum."&gt;
        Popover on top
    &lt;/button&gt;

    &lt;br&gt;&lt;br&gt;

    &lt;button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="bottom" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum."&gt;
        Popover on bottom
    &lt;/button&gt;

    &lt;button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="right" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum."&gt;
        Popover on right
    &lt;/button&gt;

    &lt;script&gt;
        setTimeout(function() {
            $('button[data-toggle="popover"]').popover('hide');
        }, 1000);
    &lt;/script&gt;
&lt;/div&gt;
    </s:param>
</s:include>