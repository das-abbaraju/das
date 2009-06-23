<%@ taglib prefix="s" uri="/struts-tags"%>
<ul>
<s:iterator value="data">
	<li><s:property value="[0].get('name')" escape="false" /></li>
</s:iterator>
</ul>