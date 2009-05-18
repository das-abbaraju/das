<%@ taglib prefix="s" uri="/struts-tags"%>
<ul>
	<s:iterator value="results">
		<li id="<s:property value="naic" />"><s:property value="companyName" /> (<s:property value="naic" />)</li>
	</s:iterator>
</ul>
