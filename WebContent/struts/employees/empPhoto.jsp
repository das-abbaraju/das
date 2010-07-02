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
	setStep();

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
function setStep(){
	var step = <s:property value="showSavePhoto()"/>;
	if(step){
		$('#step2').addClass('current');
	} else {
		$('#step1').addClass('current');
	}
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
					<li><a id="step1" href="">Step 1: Upload</a></li>
					<li><a id="step2" href="">Step 2: Crop</a></li>
				</ul>
			</div>
			<s:include value="../actionMessages.jsp" />
			<img id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employeeID"/>" />
			<s:if test="employee.photo.length() > 0">
				Photo is saved and ready to use!
			</s:if>
			
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
						<label>Photo:</label>
						<input type="hidden" name="employeeID" value="<s:property value="employeeID"/>"/>
						<div id="photoError"></div>
						<s:file id="file" name="file" value="%{file}" size="50"></s:file><br /><br />
						<button class="picsbutton positive" onclick="checkPhoto();" name="button" value="Upload" type="submit">Upload Photo</button>
						<button class="picsbutton" onclick="closePage(); return false;" title="Cancel and return to previous page">Close Page</button>
						<s:if test="showSavePhoto()"><button class="picsbutton positive" name="button" type="submit" value="Save">Save Profile Photo</button></s:if>
					</div>
				</div>
			</s:form>
			<br clear="all" />
		</div>
	</div>
</div>
</body>
</html>