<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="data">
<s:property value="get('name')" /> - <s:property value="get('accountName')" />|<s:property value="get('id')"/>
</s:iterator>