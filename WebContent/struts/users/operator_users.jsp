<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<label>Requested By User:</label>
<s:select list="getUsersList(opID)" listKey="id" listValue="name"
	name="requestedUser" value="newContractor.requestedByUser.id"
	headerKey="0" headerValue="- Other-" onchange="checkUserOther();" />
<span class="redMain">*</span>
<s:textfield name="newContractor.requestedByUserOther"
	id="requestedByOtherUser" size="20" />