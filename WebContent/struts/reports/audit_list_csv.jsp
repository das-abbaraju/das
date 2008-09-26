<%@ taglib prefix="s" uri="/struts-tags"%>Contractor ID,Name,Industry,Trade
<s:iterator value="data"><s:property 
value="get('id')" />,"<s:property 
value="get('name')" escape="false" />","<s:property 
value="get('industry')" escape="false" />","<s:property 
value="get('main_trade')" escape="false" />"
</s:iterator>
