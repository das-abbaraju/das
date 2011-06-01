<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<label><s:text name="ContractorRegistrationRequest.requestedByUser" />:</label>
<s:select list="getUsersList(opID)" listKey="id" listValue="name"
	id="requestedUser" name="requestedUser" headerKey="0" headerValue="- Other -" onchange="checkUserOther();" />
<s:textfield name="newContractor.requestedByUserOther" id="requestedOther" size="20" />
<div class="fieldhelp">
	<h3><s:text name="ContractorRegistrationRequest.requestedByUser" /></h3>
	<s:text name="ContractorRegistrationRequest.requestedByUser.fieldhelp" />
</div>
	
<s:if test="requestedUser > 0">
	<script type="text/javascript">
		$('input#requestedOther').hide();
	</script>
</s:if>