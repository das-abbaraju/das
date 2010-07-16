<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:iterator value="accountList">	
	<s:property value="get('name')" />|<s:property value="get('id')" />
</s:iterator>