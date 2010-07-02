<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
var api;
function closePage() {
	window.opener.location.reload();
	self.close();
}
jQuery(document).ready(function(){
	jQuery('#cropPhoto').Jcrop({
		onChange: showCoords,
		onSelect: showCoords,
		aspectRatio: 1
	});
	stepID =<s:property value="step"/> 
	setStep(stepID);
	
});
function showCoords(c)
{
	jQuery('#x1').val(c.x);
	jQuery('#y1').val(c.y);
	jQuery('#x2').val(c.x2);
	jQuery('#y2').val(c.y2);
	jQuery('#width').val(c.w);
	jQuery('#height').val(c.h);
}
function checkPhoto(){
	var d = $('#file').val();
	if(d==null){
		$('#photoError').html("You must upload a valid file");
		return false;
	}	
}
function setStep(id){
	for(i=1; i<=3; i++){
		$('#step'+i).removeClass('current');
	}
	$('#stepLocation').html(id);
	$('#step'+id).addClass('current');
	switch(id){
		case 3:
			$('.uploadStep').hide();
			$('.cropStep').hide();
			break;
		case 2:
			$('.uploadStep').hide();
			$('.cropStep').show();
			break;
		default:
			$('.uploadStep').show();
			$('.cropStep').hide();
	}	
}
function jDisable(){
	api.disable();
}
function jEnable(){
	/*jQuery('#cropPhoto').Jcrop({
		onChange: showCoords,
		onSelect: showCoords,
		aspectRatio: 1
	});	*/
	api.enable();
}

</script>

</head>
<body>
<br />
<div id="main">
	<div id="bodyholder">
		<div id="content">
			<h1>Upload Photo
			<span class="sub"><s:property value="employee.displayName"/></span>
			</h1>
			<div id="internalnavcontainer">
				<ul id="navlist">
					<li><a id="step1" href="#" onclick="setStep(1); return false;">Step 1: Upload</a></li>
					<li><a id="step2" href="#" onclick="setStep(2); return false;">Step 2: Crop</a></li>
					<li><a id="step3" href="#" onclick="setStep(3); return false;">Step 3: Finished</a></li>
				</ul>
			</div>
			<h2>Step <span id="stepLocation">1</span></h2> 
			<s:include value="../actionMessages.jsp" />
			<div id="info">Hey moo</div>
			<img id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employeeID"/>" />
			
		<form onsubmit="return false;">
		</form>
			<s:form enctype="multipart/form-data" method="POST">
				<div style="background-color: #F9F9F9;">
					<div class="question">
						<input type="hidden" id="x1" name="x1" value="0" />
						<input type="hidden" id="y1" name="y1" value="0" />
						<input type="hidden" id="x2" name="x2" value="0" />
						<input type="hidden" id="y2" name="y2" value="0" />
						<input type="hidden" id="width" name="width" value="0" />
						<input type="hidden" id="height" name="height" value="0" />
						
						<div class="uploadStep" >
							<label>Photo:</label>
							<input type="hidden" name="employeeID" value="<s:property value="employeeID"/>"/>
							<s:file id="file" name="file" value="%{file}" size="50"></s:file>
						</div>
						<br /><br />
						
						<button class="picsbutton positive uploadStep" onclick="checkPhoto();" name="button" value="Upload" type="submit">Upload Photo</button>
						<button class="picsbutton" onclick="closePage(); return false;" title="Cancel and return to previous page">Close Page</button>
						<s:if test="showSavePhoto()"><button class="picsbutton positive cropStep" name="button" type="submit" value="Save">Save Profile Photo</button></s:if>
					</div>
				</div>
			</s:form>
			<br clear="all" />
		</div>
	</div>
</div>
</body>
</html>