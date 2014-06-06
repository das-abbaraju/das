<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">${section_title}: Save Modal</s:param>
    <s:param name="section_id">${section_id_prefix}_save</s:param>

    <s:param name="description">
Modals are used for user confirmation (i.e. save, delete…).
    </s:param>

    <s:param name="example_url">
        modals/save-modal/_save-modal-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">save-modal</s:param>

    <s:param name="html_code">
&lt;div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="saveModalLabel" aria-hidden="true"&gt;
    &lt;div class="modal-dialog"&gt;
        &lt;div class="modal-content"&gt;
            &lt;div class="modal-header"&gt;
                &lt;button type="button" class="close" data-dismiss="modal" aria-hidden="true"&gt;&times;&lt;/button&gt;
                &lt;h4 class="modal-title"&gt;Unsaved Changes&lt;/h4&gt;
            &lt;/div&gt;
            &lt;div class="modal-body"&gt;
                &lt;p&gt;You haven't saved something on this page. Would you like to save before leaving?&lt;/p&gt;
            &lt;/div&gt;
            &lt;div class="modal-footer"&gt;
                &lt;button type="button" class="btn btn-default" data-dismiss="modal"&gt;Leave Page&lt;/button&gt;
                &lt;a href="#" class="btn btn-danger"&gt;Save Changes&lt;/a&gt;
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>
</s:include>