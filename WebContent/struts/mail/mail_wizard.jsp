<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Email Wizard</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<img src="images/beta.jpg" align="right" title="This is a new feature. Please send us your feedback or suggestions." />

<h1>Email Wizard</h1>

<div class="helpOnRight">
The Email Wizard allows you to send either generic or custom emails to a large group of contractors.
</div>

Step 1: Pick a list type

Send an email to:
<ul>
<li>a single contractor</li>
<li>a list of contractors</li>
<li>a list of operators</li>
</ul>

<div id="contractor_details">
<s:include value="../reports/filters.jsp"></s:include>
</div>
<div id="audit_details">
</div>
<div id="user_details">
</div>


</body>
</html>