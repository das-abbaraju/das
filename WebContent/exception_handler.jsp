<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<jsp:include page="/struts/jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="/css/forms.css" />
<script type="text/javascript">
    $(document).ready(function() {
        $('#response_form').submit(function() {
        	var user_message = $("textarea#user_message").val();
        	var to_address = "errors@picsauditing.com";
        	var from_address = $("#from_address").val();
        	var user_name = $("#user_name").val();
        	var dataString = 'priority=5&user_message=' + user_message + '&to_address=' + to_address + '&from_address=' + from_address + '&user_name=' + user_name;
        	$.ajax({
        		type: "POST",
        		url: "ExceptionAction!sendExceptionEmail.action",
        		data: dataString,
        			success: function() {
        				$('#response_form').html("<div id='message1'></div>");
        				$('#message1').html("<h3>Response Submitted!</h3>")
        				.append("<h5>Thank you for your assistance.</h5>")
        				.hide()
        				.fadeIn(1500);
        				$('#backButton').fadeIn(1500);
        				$("#reportButton").attr('disabled', true);
        			}
        		});
        	return false;
        });
    });
</script>
<title>PICS Error</title>
</head>

<body>
<div class="error">
	Oops!! An unexpected error just occurred.<br>
</div>

<s:if test="debugging">
	<p><s:property value="exceptionStack"/></p>
</s:if>
<s:else>
	<form id="response_form" method="post" action="" style="width:450px;">
		<fieldset class="form" >
			<h2 class="formLegend">Please help us by reporting this error</h2>
			<div>
				<div style="padding:2ex;">
					<s:if test="!permissions.loggedIn">
						<label style="width:5em;"><span>Name:</span></label>
						<input type="text" id="user_name" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
						<br/>
						<label style="width:5em;"><span>Email:</span></label>
						<input type="text" id="from_address" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
						<br/>
					</s:if>
					<label style="width:5em;">Optional:</label>Please tell us what you were trying to do:<br/>
					<label style="width:5em;">&nbsp;</label>
					<div>
						<textarea id="user_message" name="user_message" rows="3" cols="40" style="color:#464646;font-size:12px;font-weight:bold;"></textarea>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form submit">
			<input class="picsbutton" type="button" value="&lt;&lt; Back" onclick="window.history.back().back()" />
			<input id="reportButton" class="picsbutton" type="submit" value="Report to PICS Engineers" />
		</fieldset>
	</form>
	<input id="backButton" class="picsbutton" style="float:left; display:none;" type="button" value="&lt;&lt; Back" onclick="window.history.back().back()" />
</s:else>

</body>
</html>
