<%@ taglib prefix="s" uri="/struts-tags" %>
<s:if test="shouldIncludeHeader">
<s:select 
       name="%{controlName}"
       cssClass="forms"
	   headerKey="0"
	   headerValue="%{@com.picsauditing.jpa.entities.User@DEFAULT_AUDITOR}"
       list="auditors"
       listKey="id"
       listValue="name"
       />
</s:if>
<s:else>
<s:select 
       name="%{controlName}"
       cssClass="forms"
       list="auditors"
       listKey="id"
       listValue="name"
       />
</s:else>       
