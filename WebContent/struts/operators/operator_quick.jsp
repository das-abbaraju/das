<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="operator.name" /></title>
</head>
<body>
<h4><s:property value="operator.name" /></h4>

<label>Primary Contact:</label> <s:property value="operator.contact"/><br />
<label>Industry:</label> <s:property value="operator.industry.description"/><br />

</body>
</html>
