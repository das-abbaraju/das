<%@ taglib prefix="s" uri="/struts-tags"%>
<div style="
	border: 1px #A84D10 solid; 
	background-color: #F6F6F6;
	padding: 5px;
	margin: 3px; 
	width: 200px; 
	text-align: center;
"><a href="ContractorView.action">Show Details Page</a></div>
<a href="contractor_edit.jsp" class="edit" style="float: right">Edit Account Info</a>
<p><label>Account Name:</label> <s:property value="contractor.name" /></p>
<p><label>Address:</label> <s:property value="contractor.address" /><br />
<s:property value="contractor.city" />, <s:property value="contractor.state" /> <s:property value="contractor.zip" /></p>
<p><label>Account Since:</label> <s:date name="contractor.accountDate" format="MMM d, yyyy" /></p>
<p><label>Primary Contact:</label> <s:property value="contractor.contact" /></p>
<p><label>Primary Email:</label> <s:property value="contractor.email" /></p>
<s:if test="contractor.auditor.name != null">
	<p><label>Assigned Representative:</label> <s:property value="contractor.auditor.name" /></p>
</s:if>

