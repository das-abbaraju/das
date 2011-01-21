<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
Category: <s:select list="desktopCategories" headerKey="0" headerValue="- Select Category -" listKey="id" listValue="name" 
	onchange="getTable(%{auditType.id}, this.value);" name="categoryID" />