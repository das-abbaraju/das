<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:iterator value="departmentList">	
	<s:property value="get('department')" />
</s:iterator>