<%@ taglib prefix="s" uri="/struts-tags"%>
<ul>
	<s:iterator value="results">
		<li id="<s:property value="naic" />"><span class="companyName"><s:property value="companyName" /></span> (<s:property value="naic" />)</li>
	</s:iterator>
</ul>
