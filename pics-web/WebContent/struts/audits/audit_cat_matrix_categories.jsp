<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<s:text name="AuditCategoryMatrix.label.Category" />
<s:select
	list="desktopCategories"
	headerKey="0"
	headerValue="- Select Category -"
	listKey="id"
	listValue="name" 
	onchange="getTable(%{auditType.id}, this.value);"
	name="categoryID"
/>