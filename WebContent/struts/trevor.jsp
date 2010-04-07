<%@ taglib prefix="s" uri="/struts-tags"%>
<% // This is a blank jsp page created for struts ajax calls %>
<s:include value="actionMessages.jsp" />
<s:form>
Source File: <s:textfield name="fileFrom" /><br />
Destination File: <s:textfield name="fileTo" /><br />
<button name="button">Move</button>
</s:form>
<s:property value="output" escape="false" />
