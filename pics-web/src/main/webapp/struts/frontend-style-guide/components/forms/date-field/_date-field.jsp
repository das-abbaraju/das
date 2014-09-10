<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_date_field</s:param>
    <s:param name="header_title">${section_title}: Date Field</s:param>

    <s:param name="description">
        <div class="pull-right badge badge-info">AngularJS</div>The date format throughout the site is YYYY-MM-DD.
        <br/><br/>
        <div class="alert alert-info">
            <strong>Note</strong>
            The rendering below is just to show what the date field should look like.  In practice, this should be implemented using the angular directive as shown in the markup. To see a working example, refer to the New Project page in EmployeeGUARD.
        </div>
    </s:param>

    <s:param name="example_url">
        forms/date-field/_date-field-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">date-field</s:param>

    <s:param name="html_code">
        &lt;date label="PROJECT_CREATE.FORM.START_DATE" value="project.startDate" format="milliseconds" name="startDate" opened="openDatePicker"&gt;&lt;/date&gt;
    </s:param>
</s:include>