<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Clear Document Data</s:param>
</s:include>

<s:include value="../actionMessages.jsp"></s:include>

<style type="text/css">
td.waiting {
	background-color: gray;
}
td.success {
	background-color: green;
}
td.fail {
	background-color: red;
}
</style>

<s:form id="save" method="POST" enctype="multipart/form-data">

<fieldset class="form">
    <ol>
        <li>
            <label>
                Audit Type Slug:
            </label>
            <s:textfield name="slug" size="35"/>
        </li>
    </ol>
</fieldset>
<fieldset class="form submit">
    <s:submit cssClass="picsbutton positive" method="clear" value="Clear"/>
</fieldset>
</s:form>
