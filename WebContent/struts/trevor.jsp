<%@ taglib prefix="s" uri="/struts-tags"%>
<% // This is a blank jsp page created for struts ajax calls %>
<s:include value="actionMessages.jsp" />
<s:property value="co.contractorAccount.name"/>
<s:property value="co.operatorAccount.name"/>
<s:iterator value="list">
<s:property/><br/>
</s:iterator>
