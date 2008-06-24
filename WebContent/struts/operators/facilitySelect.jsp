<%@ taglib prefix="s" uri="/struts-tags" %>
<s:if test="shouldIncludePICS == true">
<s:select 
       name="accountId"
       cssClass="blueSmall"
	   headerKey="1100"
	   headerValue="PICS Employees"
       list="facilities"
       listKey="id"
       listValue="name"
       />
</s:if>
<s:else>
<s:select 
       name="accountId"
       cssClass="blueSmall"
       list="facilities"
       listKey="id"
       listValue="name"
       />      
</s:else>       


