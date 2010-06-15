<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<label>Requested By User:</label>
<s:select list="getUsersList(opID)" listKey="id" listValue="name"
	id="requestedUser" name="requestedUser" headerKey="0" headerValue="- Other -" onchange="checkUserOther();" />
<input type="text" name="requestedOther" id="requestedOther" size="20"
	<s:if test="requestedUser > 0">style="display:none;"</s:if>
	value="<s:property value="newContractor.requestedByUserOther" />" />