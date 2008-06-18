<%@ taglib prefix="s" uri="/struts-tags" %>
<s:select 
       name="%{controlName}"
       cssClass="forms"
       list="auditors"
       listKey="id"
       listValue="name"
       value="presetValue"
       multiple="true"
       size="5"
       />
