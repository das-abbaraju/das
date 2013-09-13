<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="/exception_handler.jsp"%>
<label for="employee">Employee:</label><s:select list="employees" listKey="id" 
	listValue="displayName" name="employeeID" value="employeeID" />