<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Request New Contractor</title>

<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/notes.css" />
<s:include value="../jquery.jsp"/>
</head>
<body>

<s:form id="saveContractorForm">
	<s:hidden name="newContractor.id" />
	<fieldset class="form"><legend><span>Details</span></legend>
	<ol>
		<li><label>Name:</label> <s:property value="newContractor.name" /></li>
		<li><label for="newContractor.contact">Contact name:</label> <s:textfield name="newContractor.contact" /></li>
		<li><label for="newContractor.phone">Phone:</label> <s:textfield name="newContractor.phone" size="20" /></li>
		<li><label for="newContractor.email">Email address:</label> <s:textfield name="newContractor.email" size="30" /></li>
		<li><label for="newContractor.taxID">Tax ID:</label> <s:textfield name="u.fax" size="20" /></li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<legend><span>Primary Address</span></legend>
	<ol>
		<li><label>Address:</label>
			<s:textfield name="contractor.address" size="35" />
		</li>
		<li><label>City:</label>
			<s:textfield name="contractor.city" size="20" />
		</li>
		<li><label>Country:</label>
			<s:select list="countryList"
			name="country.isoCode" id="contractorCountry"
			listKey="isoCode" listValue="name"
			value="contractor.country.isoCode"
			onchange="countryChanged(this.value)"
			/></li>
		<li id="state_li"></li>
		<li><label>Zip:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<legend><span>User Information</span></legend>
	<ol>
		<li><label>Requested By Account:</label>
			<s:textfield name="contractor.address" size="35" />
		</li>
		<li><label>Requested By User:</label>
			<s:textfield name="contractor.city" size="20" />
		</li>
		<li><label>Deadline Date:</label>
			<s:select list="countryList"
			name="country.isoCode" id="contractorCountry"
			listKey="isoCode" listValue="name"
			value="contractor.country.isoCode"
			onchange="countryChanged(this.value)"
			/></li>
		<li><label>Last Contacted By:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
		<li><label>Date Contacted:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
		<li><label>Notes:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
		<li><label># of Contacted:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
		<li><label>HandledBy:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
		<li><label>Matches Found in PICS:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
		<li><label>Linked in PICS:</label>
			<s:textfield name="contractor.zip" size="7" />
		</li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
		<input type="submit" class="picsbutton positive" name="button" value="Send Email" />
		<input type="submit" class="picsbutton positive" name="button" value="Contacted" />
		<input type="submit" class="picsbutton positive" name="button" value="Save" />
	</div>
	</fieldset>
</s:form>

</body>
</html>
