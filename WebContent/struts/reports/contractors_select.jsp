<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="data">
<s:property value="[0].get('name')" escape="false" />
</s:iterator>