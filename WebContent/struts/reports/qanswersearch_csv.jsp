<%@ taglib prefix="s" uri="/struts-tags"%>Contractor ID,Name,<s:iterator value="questions"><s:property value="columnHeaderOrQuestion"/></s:iterator>
<s:iterator value="data"><s:property 
value="get('id')" />,"<s:property 
value="get('name')" escape="false" />"<s:iterator value="questions">,"<s:property 
value="%{get('answer' + id)}" escape="false" />"</s:iterator>
</s:iterator>
