<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>


<ul>
    <li><strong>Address:</strong> <s:property value="addressResponse.addressLine1" /></li>
    <li><strong>Address:</strong> <s:property value="addressResponse.addressLine2" /></li>
    <li><strong>City:</strong> <s:property value="addressResponse.city" /></li>
    <li><strong>State:</strong> <s:property value="addressResponse.stateOrProvince" /></li>
    <li><strong>ZipCode:</strong> <s:property  value="addressResponse.zipOrPostalCode" /></li>
    <li><strong>Country:</strong> <s:property value="addressResponse.country" /></li>
    <li><strong>Status:</strong> <s:property value="addressResponse.resultStatus" /></li>
    <li><strong>Status Description:</strong> <s:property value="addressResponse.statusDescription" /></li>
    <li><strong>Confidence Percent:</strong> <s:property value="addressResponse.confidencePercent" /></li>
</ul>
<a href="AddressVerificationTest.action">Verify another address</a>
