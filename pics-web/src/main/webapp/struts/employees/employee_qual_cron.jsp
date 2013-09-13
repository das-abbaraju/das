<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Employee Details</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
</head>
<body>
<s:include value="../actionMessages.jsp" />

<s:form>
	EmployeeID: <s:textfield name="employeeID" />
	<s:submit name="button" value="employee" />
</s:form>

</body>
</html>
