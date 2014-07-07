<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">${section_title}: Delete Modal</s:param>
    <s:param name="section_id">${section_id_prefix}_delete</s:param>

    <s:param name="description">
Modals are used for user confirmation (i.e. save, delete…).
    </s:param>

    <s:param name="example_url">
        modals/delete-modal/_delete-modal-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">delete-modal</s:param>

    <s:param name="html_code">
&lt;div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true"&gt;
    &lt;div class="modal-dialog"&gt;
        &lt;div class="modal-content"&gt;
            &lt;div class="modal-header"&gt;
                &lt;button type="button" class="close" data-dismiss="modal" aria-hidden="true"&gt;&times;&lt;/button&gt;
                &lt;h4 class="modal-title"&gt;Delete Something&lt;/h4&gt;
            &lt;/div&gt;
            &lt;div class="modal-body"&gt;
                &lt;p&gt;Message regarding what will be lost by the delete action.&lt;/p&gt;
            &lt;/div&gt;
            &lt;div class="modal-footer"&gt;
                &lt;button type="button" class="btn btn-default" data-dismiss="modal"&gt;Cancel&lt;/button&gt;
                &lt;a href="#" class="btn btn-danger"&gt;Delete&lt;/a&gt;
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>

<%--     <s:param name="struts_code">
&lt;s:include value="/struts/employee-guard/_delete-confirmation.jsp"&gt;
    &lt;s:param name="delete_url"&gt;${operator_project_delete_url}&lt;/s:param&gt;
    &lt;s:param name="modal_title"&gt;Delete Project&lt;/s:param&gt;
    &lt;s:param name="modal_message"&gt;
        Deleting will remove the project and its assigned job roles, assigned companies, and employee assignments.
    &lt;/s:param&gt;
&lt;/s:include&gt;
    </s:param> --%>
</s:include>