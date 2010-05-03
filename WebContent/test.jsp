<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>
<title>Ajax Test</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript">

function doSimpleGet() {
	$('#test').append('Starting simple GET Request');
	$.ajax({
		type: 'GET',
		beforeSend: function (xhr) {
			$('#test').append('beforeSend: ' + xhr + '<br/>');
		},
		complete: function(xhr, textStatus) {
			$('#test').append('complete: status=' + textStatus + '<hr/>');
		},
		error: function(xhr, textStatus, errorThrown) {
			$('#test').append('error: status=' + textStatus + '<br/>' + errorThrown + '<br/>');
		},
		success: function(data, textStatus, xhr) {
			$('#test').append(data + '<br/><br/>');
		},
		url: 'simpleAjax.txt'
	});
}

function doGet() {
	$('#test').append('Starting GET Request');
	$.ajax({
		type: 'GET',
		beforeSend: function (xhr) {
			$('#test').append('beforeSend: ' + xhr + '<br/>');
		},
		complete: function(xhr, textStatus) {
			$('#test').append('complete: status=' + textStatus + '<hr/>');
		},
		error: function(xhr, textStatus, errorThrown) {
			$('#test').append('error: status=' + textStatus + '<br/>' + errorThrown + '<br/>');
		},
		success: function(data, textStatus, xhr) {
			$('#test').append(data + '<br/><br/>');
		},
		data: {type: 'get'},
		url: 'testAjax.jsp'
	});
}

function doPost() {
	$('#test').append('Starting POST Request');
	$.ajax({
		type: 'POST',
		beforeSend: function (xhr) {
			$('#test').append('beforeSend: ' + xhr + '<br/>');
		},
		complete: function(xhr, textStatus) {
			$('#test').append('complete: status=' + textStatus + '<hr/>');
		},
		error: function(xhr, textStatus, errorThrown) {
			$('#test').append('error: status=' + textStatus + '<br/>' + errorThrown + '<br/>');
		},
		success: function(data, textStatus, xhr) {
			$('#test').append(data + '<br/><br/>');
		},
		data: {type: 'post'},
		url: 'testAjax.jsp'
	});
}

function doPermissions() {
	$('#test').append('Starting Permissions Request');
	$.ajax({
		type: 'json',
		beforeSend: function (xhr) {
			$('#test').append('beforeSend: ' + xhr + '<br/>');
		},
		complete: function(xhr, textStatus) {
			$('#test').append('complete: status=' + textStatus + '<hr/>');
		},
		error: function(xhr, textStatus, errorThrown) {
			$('#test').append('error: status=' + textStatus + '<br/>' + errorThrown + '<br/>');
		},
		success: function(data, textStatus, xhr) {
			$('#test').append(data + '<br/><br/>');
		},
		data: {type: 'post'},
		url: 'LoginAjax.action'
	});
}
</script>
</head>
<body>
<h1> Ajax Test</h1>

<input type="button" onclick="doSimpleGet()" value="Simple Get"/>
<input type="button" onclick="doGet()" value="Get"/>
<input type="button" onclick="doPost()" value="Post"/>
<input type="button" onclick="doPermissions()" value="Permissions"/>
<input type="button" onclick="$('#test').html('')" value="Clear"/>
<br/>
<br/>
<br/>
<div id="test"></div>

</body>
</html>
