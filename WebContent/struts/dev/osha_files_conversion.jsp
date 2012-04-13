<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>OSHA File Conversion</title>
<script type="text/javascript">
	function generateShellScript() {
		// startThinking({div:'status_'+oshaId});
		var data= {};
		$.getJSON('OshaFileConversionAjax.action', data, function(json){
				$('#results').html('Count ='+json.count+'<br/>File = '+json.filename);
				// stopThinking({div:'status_'+oshaId});
			
			}, function() { alert('failed...'); }
		);

		return false;
	}
</script>
</head>
<body>
<form name="osha" id="osha" >
	<input type="button" value="Generate" onclick="generateShellScript();">
	</form>
	<div id="results">
	No results yet.
	</div>
</body>
</html>