<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="accountList.size() > 0">
Accounts<br/>
<s:iterator value="accountList" id="result">
<s:property value="get('id')" /> -- <s:property value="get('name')" /> <br/>
</s:iterator>
<br />---
</s:if>
<s:if test="employeeList.size() > 0">
Employee<br/>
<s:iterator value="employeeList" id="result">
<s:property value="get('id')" /> -- <s:property value="get('name')" /> -- <s:property value="get('accName')" /> <br/>
</s:iterator>
<br />---
</s:if>
<s:if test="userList.size() > 0">
Users<br/>
<s:iterator value="userList" id="result">
<s:property value="get('id')" /> -- <s:property value="get('name')" /> -- <s:property value="get('accName')" /> <br/>
</s:iterator>
</s:if>