<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
var api;
function closePage() {
	window.opener.location.reload();
	self.close();
}
jQuery(document).ready(function(){
	api = jQuery('#cropPhoto').Jcrop({
		onChange: showCoords,
		onSelect: showCoords,
		aspectRatio: 1,
		minSize: [150,150]
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
	
	$('#step'+id).addClass('current');
	if(id==2){
		$('.uploadStep').fadeOut();
		$('.cropStep').show();
	} else if(id==1){
		$('.uploadStep').show();
		$('.cropStep').fadeOut();
	} else{
		$('.uploadStep').fadeOut();
		$('.cropStep').fadeOut();	
	}
}

</script>

</head>
<body>
<br />
<h1>Upload Photo
<span class="sub"><s:property value="employee.displayName"/></span>
</h1>
<div id="internalnavcontainer">
	<ul id="navlist">
		<li><a id="step1" <s:if test="step>=1">href="#"onclick="setStep(1); return false;"</s:if>>Step 1: Upload</a></li>
		<li><a id="step2"<s:if test="step==1">class="inactive"</s:if> <s:if test="step>=2">href="#" onclick="setStep(2); return false;"</s:if>>Step 2: Finish</a></li>
	</ul>
</div>
<a class="picsbutton" href="ManageEmployees.action?employee.id=<s:property value="employee.id"/>">&lt;&lt; Back to Manage Employee</a>
<s:include value="../actionMessages.jsp" />
<s:if test="step==2 & employee.photo == null">
</s:if>
<br />
<s:if test="showSavePhoto()">
	<img id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employeeID"/>" />
</s:if>
<s:form enctype="multipart/form-data" method="POST">
		<input type="hidden" name="step" value="<s:property value="step"/>" />
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
		
		<fieldset class="form submit">
			<button class="picsbutton positive uploadStep" onclick="checkPhoto();" name="button" value="Upload" type="submit">Upload Photo</button>
			<s:if test="showSavePhoto()">
				<button class="picsbutton positive cropStep" name="button" type="submit" value="Save">Crop Photo</button>
				<button class="picsbutton negative cropStep" name="button" type="submit" value="Delete"
					onclick="return confirm('Are you sure you want to delete this photo? This action cannot be undone.');">Delete Photo</button>
			</s:if>
		</fieldset>
</s:form>
</body>
</html>
