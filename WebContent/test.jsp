<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>
<title>Ajax Test</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
	alert('Starting simple GET Request');
	$.ajax({
		type: 'GET',
		beforeSend: function (xhr) {
			alert('beforeSend: ' + xhr);
		},
		complete: function(xhr, textStatus) {
			alert('complete: status=' + textStatus);
		},
		error: function(xhr, textStatus, errorThrown) {
			alert('error: status=' + textStatus + "\n\n" + errorThrown);
		},
		success: function(data, textStatus, xhr) {
			alert('success: status=' + textStatus);
			$('#test').html(data);
		},
		url: 'simpleAjax.txt'
	});
	alert('Starting GET Request');
	$.ajax({
		type: 'GET',
		beforeSend: function (xhr) {
			alert('beforeSend: ' + xhr);
		},
		complete: function(xhr, textStatus) {
			alert('complete: status=' + textStatus);
		},
		error: function(xhr, textStatus, errorThrown) {
			alert('error: status=' + textStatus + "\n\n" + errorThrown);
		},
		success: function(data, textStatus, xhr) {
			alert('success: status=' + textStatus);
			$('#test').html(data);
		},
		data: {type: 'get'},
		url: 'testAjax.jsp'
	});
	alert('Starting POST Request');
	$.ajax({
		type: 'POST',
		beforeSend: function (xhr) {
			alert('beforeSend: ' + xhr);
		},
		complete: function(xhr, textStatus) {
			alert('complete: status=' + textStatus);
		},
		error: function(xhr, textStatus, errorThrown) {
			alert('error: status=' + textStatus + "\n\n" + errorThrown);
		},
		success: function(data, textStatus, xhr) {
			alert('success: status=' + textStatus);
			$('#test').html(data);
		},
		data: {type: 'post'},
		url: 'testAjax.jsp'
	});
});
</script>
</head>
<body>
<h1> Ajax Test</h1>

<div id="test"></div>


</body>
</html>
