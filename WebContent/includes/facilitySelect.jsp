<%@ taglib prefix="s" uri="/struts-tags" %>
<s:if test="shouldIncludePICS == true">
<s:select 
       name="accountId"
       cssClass="blueSmall"
	   headerKey="1100"
	   headerValue="PICS Employees"
       list="facilities"
       listKey="[0].get('id')"
       listValue="%{[0].get('name')}"
       />
</s:if>
<s:else>
<s:select 
       name="accountId"
       cssClass="blueSmall"
       list="facilities"
       listKey="[0].get('id')"
       listValue="%{[0].get('name')}"
       />
</s:else>       


