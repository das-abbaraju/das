<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<label>Requested By User:</label>
<s:select list="getUsersList(newContractor.requestedBy.id)" listKey="id" listValue="name"
	id="requestedUser" name="requestedUser" value="%{requestedUser}"
	headerKey="0" headerValue="- Other -" onchange="checkUserOther();" />
<input type="text" name="requestedOther" id="requestedOther" size="20"
	<s:if test="newContractor.requestedByUser != null && newContractor.requestedBy.users != null">style="display:none;"</s:if>
	value="<s:property value="newContractor.requestedByUserOther" />" />