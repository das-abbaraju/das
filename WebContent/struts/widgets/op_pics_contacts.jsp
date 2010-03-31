<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<body>

<s:iterator value="contacts.keySet()" id="key">
	<p><label><s:property value="#key.description" /><s:if test="contacts.get(#key).size() > 1">s</s:if>:</label></p>
	<ul style="list-style-type: none">
		<s:iterator value="contacts.get(#key)" id="managers">
			<li><a href="mailto:<s:property value="#managers.email" />" title="Email <s:property value="#managers.name" />"><s:property value="#managers.name" /></a>
				(<s:property value="#managers.phone" />)</li>
		</s:iterator>
	</ul>
</s:iterator>

<p><label>General Inquiries:</label> 1-800-506-PICS (7427)</p>

</body>
</html>
