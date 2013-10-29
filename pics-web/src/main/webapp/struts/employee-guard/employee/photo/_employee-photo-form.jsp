<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<tw:form id="edit_employee_photo" formName="contractor_employee_edit_photo" action="${photo_edit_url}" method="post" class="form-horizontal" enctype="multipart/form-data" autocomplete="off" role="form">
	<s:include value="/struts/employee-guard/employee/photo/_photo.jsp"></s:include>
</tw:form>