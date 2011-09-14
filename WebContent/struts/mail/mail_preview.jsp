<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<label><s:text name="EmailQueue.toAddresses" />:</label> <s:property value="emailPreview.toAddresses"/> <br />
<label><s:text name="EmailQueue.ccAddresses" />:</label> <s:property value="emailPreview.ccAddresses"/> <br />
<label><s:text name="EmailQueue.subject" />:</label> <s:property value="emailPreview.subject"/> <br />

<s:textarea name="emailPreviewBody" value="%{emailPreview.body}" rows="25" cols="75"></s:textarea>
