<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<li><label>Phone: </label><s:textfield readonly="true" name="user.phone" /></li>
<li><label>Email: </label><s:textfield readonly="true" name="user.email" /></li>
<s:if test="user.fax != null && user.fax.trim().length() > 0">
	<li><label>Fax: </label><s:textfield readonly="true" name="user.fax" /></li>
</s:if>
