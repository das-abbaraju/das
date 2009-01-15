<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Payment Options</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>

<s:include value="conHeader.jsp"></s:include>
<s:form id="save" method="POST" enctype="multipart/form-data">
<div class="buttons">
	<!-- <button class="positive" name="button" type="submit" value="Save">Save</button>  -->
	<input type="submit" class="positive" name="button" value="Save"/>
</div>
<br clear="all" />
<s:hidden name="id" />
	<fieldset class="form">
	<legend><span>Details</span></legend>
	<ol>
		<li><label>Name:</label>
			<s:textfield name="contractor.name" size="35" />
		</li>
		<li><label>Username:</label>
			<s:textfield name="contractor.username" size="20" />
				<pics:permission perm="SwitchUser">
					<a href="login.jsp?switchUser=<s:property value="contractor.username"/>">Switch User</a>							
				</pics:permission>
		</li>
		<li><label>Change Password:</label>
			<s:password name="password1" size="15" />
		</li>
		<li><label>Confirm Password:</label>
			<s:password name="password2" size="15" />
		</li>
		<li><label>Date Created:</label>
			<s:date name="contractor.dateCreated" format="MMM d, yyyy" />
		</li>
		<li><label>First Login:</label>
			<s:date name="contractor.getAccountDate" format="MMM d, yyyy" />
		</li>
		<li><label>Last Login:</label>
			<s:date name="contractor.lastLogin" format="MMM d, yyyy" />
		</li>
	</ol>
	</fieldset>
	<br clear="all">
</s:form>

</body>
</html>
