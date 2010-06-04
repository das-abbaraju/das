<%@ taglib prefix="s" uri="/struts-tags"%>
<% // This is a blank jsp page created for struts ajax calls %>
<s:include value="actionMessages.jsp" />
<s:property value="co.contractorAccount.name"/>
<s:property value="co.operatorAccount.name"/>
<s:form>
<s:hidden name="id"></s:hidden>
<s:iterator value="co.flagDatas">
Data: <s:property value="id"/> <s:property value="flag"/> <br />
</s:iterator>
<s:iterator value="co.overrides">
Override: <s:property value="id"/> <s:property value="forceflag"/> <br />
</s:iterator>

<button name="button">Delete</button>
</s:form>
