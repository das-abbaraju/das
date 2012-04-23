<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<script type="text/javascript" src="js/jquery/jcrop/js/jquery.Jcrop.min.js?v=${version}"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/jcrop/css/jquery.Jcrop.css?v=${version}" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
var api;

$(function() {
	api = jQuery('#cropPhoto').Jcrop({
		onChange: showCoords,
		onSelect: showCoords,
		aspectRatio: 1,
		minSize: [150,150]
	});
	stepID = '<s:property value="step" />';
	setStep(stepID);
	
	$('#navlist').delegate('.setStep', 'click', function(e) {
		e.preventDefault();
		var stepID = $(this).attr('id').split('_')[1];
		setStep(stepID);
	});
	
	$('form').delegate('.uploadStep', 'click', function(e) {
		checkPhoto();
	});
	
	$('form').delegate('.negative', 'click', function(e) {
		return confirm(translate('JS.EmployeePhotoUpload.confirm.DeletePhoto'));
	});
});

function showCoords(c) {
	$('#x1').val(c.x);
	$('#y1').val(c.y);
	$('#x2').val(c.x2);
	$('#y2').val(c.y2);
	$('#width').val(c.w);
	$('#height').val(c.h);
}

function checkPhoto() {
	var d = $('#file').val();
	if (d == null){
		$('#photoError').html(translate("JS.EmployeePhotoUpload.message.UploadValidFile"));
		return false;
	}
}

function setStep(id) {
	for (i=1; i<=3; i++) {
		$('#step_'+i).removeClass('current');
	}
	
	$('#step_'+id).addClass('current');
	if (id == 2) {
		$('.uploadStep').fadeOut();
		$('.cropStep').show();
	} else if (id == 1) {
		$('.uploadStep').show();
		$('.cropStep').fadeOut();
	} else {
		$('.uploadStep').fadeOut();
		$('.cropStep').fadeOut();	
	}
}
</script>
</head>
<body>
<br />
<h1><s:text name="EmployeePhotoUpload.title" />
<span class="sub"><s:property value="employee.displayName"/></span>
</h1>
<div id="internalnavcontainer">
	<ul id="navlist">
		<li><a id="step_1" href="#" class="<s:if test="step >= 1">setStep</s:if>"><s:text name="EmployeePhotoUpload.list.Step1" /></a></li>
		<li><a id="step_2" href="#" class="<s:if test="step == 1">inactive</s:if><s:if test="step >= 2"> setStep</s:if>"><s:text name="EmployeePhotoUpload.list.Step2" /></a></li>
	</ul>
</div>
<a class="picsbutton" href="ManageEmployees.action?employee=<s:property value="employee.id"/>"><s:text name="EmployeePhotoUpload.link.BackToManageEmployees" /></a>
<s:include value="../actionMessages.jsp" />
<br />
<s:if test="showSavePhoto()">
	<span style="margin-top: 10px;"><img id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employee.id"/>" /></span>
</s:if>
<s:form enctype="multipart/form-data" method="POST">
	<s:hidden name="employee" value="%{employee.id}" />
	<s:hidden name="step" />
	<input type="hidden" id="x1" name="x1" value="0" />
	<input type="hidden" id="y1" name="y1" value="0" />
	<input type="hidden" id="x2" name="x2" value="0" />
	<input type="hidden" id="y2" name="y2" value="0" />
	<input type="hidden" id="width" name="width" value="0" />
	<input type="hidden" id="height" name="height" value="0" />
	
	<div class="uploadStep" style="margin-top: 10px;" >
		<label><s:text name="EmployeePhotoUpload.label.Photo" />:</label>
		<s:file id="file" name="file" value="%{file}" size="50"></s:file>
	</div>
	<br /><br />
	
	<fieldset class="form submit">
		<s:submit value="%{getText('button.Upload')}" method="upload" cssClass="picsbutton positive uploadStep" />
		<s:if test="showSavePhoto()">
			<s:if test="employee.photo == null">
				<s:submit value="%{getText('EmployeePhotoUpload.button.CropPhoto')}" method="save" cssClass="picsbutton positive cropStep" />
			</s:if>
			<s:submit value="%{getText('EmployeePhotoUpload.button.DeletePhoto')}" method="delete" cssClass="picsbutton negative cropStep" />
		</s:if>
	</fieldset>
</s:form>
</body>
</html>
