<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CSS Test Page</title>
</head>
<body>

<h1>Buttons</h1>
<h3>div input[button].picsbutton</h3>
<div>
	<input type="button" class="picsbutton positive" value="Positive" />
	<input type="button" class="picsbutton" value="Utility" />
	<input type="button" class="picsbutton negative" value="Negative" />
	<input type="button" disabled="disabled" class="picsbutton" value="Disabled" />
	<input type="button" value="Normal" />
</div>
<h3>div.buttons a.picsbutton</h3>
<div>
	<a href="#" class="picsbutton positive">Positive</a>
	<a href="#" class="picsbutton">Utility</a>
	<a href="#" class="picsbutton negative">Negative</a>
</div>
<h3>div button.picsbutton</h3>
<div>
	<button class="picsbutton positive">Positive</button>
	<button class="picsbutton">Utility</button>
	<button class="picsbutton negative">Negative</button>
	<button class="picsbutton" disabled="disabled">Utility</button>
</div>
<h3>input[button]</h3>
<div>
	<input type="button" value="Normal" />
</div>
<h3>Mixed</h3>
<div>
		<input type="button" class="picsbutton positive" value="input" />
		<a href="#" class="picsbutton positive">Link</a>
		<button class="picsbutton positive">Button</button>
</div>

<h1>Links</h1>
<h3>a</h3>
<a href="#">Link</a><br>
<a href="#" class="remove">remove</a><br>
<a href="#" class="add">add</a><br>
<a href="#" class="edit">edit</a><br>

<h1>Forms</h1>

<h1>Reports</h1>

</body>
</html>