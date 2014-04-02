<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:form method="POST" name="address">
    <ul>
        <li>Address 1 <s:textarea type="text" name="addressRequest.addressBlob" id="addressRequest.addressBlob" /></li>
        <li>ZipCode <s:textfield type="text" name="addressRequest.zipOrPostalCode" id="addressRequest.zipOrPostalCode" /></li>
        <li>Country <s:textfield type="text" name="addressRequest.country" id="addressRequest.country" /></li>
        <li><s:submit value="Verify" /></li>
    </ul>
</s:form>