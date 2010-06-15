<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<label>Requested By User:</label>
<s:select id="requestedUser" list="getUsersList(opID)" listKey="id" listValue="name"
	name="requestedUser" value="getPrimaryContact(opID).id"
	headerKey="0" headerValue="- Other -" onclick="checkUserOther();" />
<span class="redMain">*</span>
<s:textfield name="newContractor.requestedByUserOther"
	id="requestedOther" size="20" />