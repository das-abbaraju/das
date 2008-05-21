<%@ taglib prefix="s" uri="/struts-tags"%>
<p><label>Account Name:</label> <a href="ContractorView.action"><s:property value="contractor.name" /></a></p>
<p><label>Address:</label> <s:property value="contractor.address" /><br />
<s:property value="contractor.city" />, <s:property value="contractor.state" /> <s:property value="contractor.zip" /></p>
<p><label>Account Since:</label> <s:date name="contractor.accountDate" format="MMM d, yyyy" /></p>
<p><label>Primary Trade:</label> <s:property value="contractor.mainTrade" /></p>
<a href="contractor_edit.jsp">Edit Account Info</a>

<p><label>Facility Count:</label> <s:property value="operators.size" /></p>

