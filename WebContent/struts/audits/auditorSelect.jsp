<%@ taglib prefix="s" uri="/struts-tags" %>
<s:select 
       name="%{controlName}"
       cssClass="forms"
	   headerKey="0"
	   headerValue="%{@com.picsauditing.jpa.entities.User@DEFAULT_AUDITOR}"
       list="auditors"
       listKey="id"
       listValue="name"
       value="presetValue"
       />
