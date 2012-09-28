<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />

<h3>
	<s:text name="ProfileEdit.label.ApiKey" />
</h3>
<div id="UserApiKey__div">	
	<button class="picsbutton positive">
		<s:text name="ProfileEdit.button.GenerateApiKey" />
	</button>
	<s:textfield cssClass="apikey" name="user.ApiKey" readonly="true" size="32" />
</div>