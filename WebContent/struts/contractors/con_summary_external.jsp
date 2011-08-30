<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../jquery.jsp"/>

<s:property value="contractor.name" /><br /><br />
<s:property value="contractor.phone" /><br />
<s:property value="contractor.address" /><br />
<s:property value="contractor.city" />,
<s:property value="contractor.state" />
<s:property value="contractor.zip" /><br />
<s:property value="contractor.country" />  <br />
<s:property value="contractor.webUrl" /><br /><br />
<s:property value="contractor.description" /><br />