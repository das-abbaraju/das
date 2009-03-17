<%@ taglib prefix="s" uri="/struts-tags"%>
<ul>
	<s:iterator value="data">
		<li id="<s:property value="get('id')"/>"><s:property value="get('name')" /> - <s:property value="get('accountName')" /></li>
	</s:iterator>
</ul>
