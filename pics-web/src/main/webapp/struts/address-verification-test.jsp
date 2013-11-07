<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:form method="POST" name="address">
    <ul>
        <li>Address 1 <s:textfield type="text" name="address.addressLine1" id="address.addressLine1" /></li>
        <li>Address 2 <s:textfield type="text" name="address.addressLine2" id="address.addressLine2" /></li>
        <li>City <s:textfield type="text" name="address.city" id="address.city" /></li>
        <li>State <s:textfield type="text" name="address.stateOrProvince" id="address.stateOrProvince" /></li>
        <li>ZipCode <s:textfield type="text" name="address.zipOrPostalCode" id="address.zipOrPostalCode" /></li>
        <li>Country <s:textfield type="text" name="address.country" id="address.country" /></li>
        <li><s:submit value="Verify" /></li>
        <li>Status: <s:property value="address.resultStatus" /></li>
        <li>Status Description: <s:property value="address.statusDescription" /></li>
    </ul>
</s:form>