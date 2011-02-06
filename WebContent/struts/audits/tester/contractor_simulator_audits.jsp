<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:iterator value="audits">
	<p><a href="#"
		onclick="fillCategories(<s:property value="id" />); return false;"><s:property
		value="name" /></a></p>
</s:iterator>
