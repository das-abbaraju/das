<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:iterator value="dataList" id="result">
<s:if test="#result.getReturnType().equals('account')"><s:property value="#result.getReturnType()"/>|<s:property value="#result.type"/>|<s:property value="#result.id"/>|<s:property value="#result.name"/></s:if>
<s:if test="#result.getReturnType().equals('user')"><s:property value="#result.getReturnType()"/>|User<s:if test="#result.isGroup()"> Group</s:if>|<s:property value="#result.id"/>|<s:property value="#result.name"/>|<s:property value="#result.account.name"/></s:if>
<s:if test="#result.getReturnType().equals('employee')"><s:property value="#result.getReturnType()"/>|Employee|<s:property value="#result.id"/>|<s:property value="#result.displayName"/>|<s:property value="#result.account.name"/></s:if>
</s:iterator>