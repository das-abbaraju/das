<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>My Google AJAX Search API Application</title>
<script type="text/javascript"
	src="http://www.google.com/jsapi?key=ABQIAAAAgozVvI8r_S5nN6njMJJ7aBRJmNx5t5cXISPRw6xt3r0G6n07ShT7w1xR_vthNMd-4I6mH3Zkx5TVjg"></script>
<script type="text/javascript">
//<![CDATA[

google.load("jquery", "1.3.2");

function OnLoad() {
	$("a").click(function() {
		alert("Hello world!");
	});
}
google.setOnLoadCallback(OnLoad);
//]]>
</script>
</head>
<body>
<div>
<a href="#">Link</a>
</div>
</body>
</html>
