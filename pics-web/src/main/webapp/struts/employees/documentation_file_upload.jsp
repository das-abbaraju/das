<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:url var="employee_skills_training" action="EmployeeSkillsTraining">
	<s:param name="employee">
		${employee.id}
	</s:param>
</s:url>

<head>
	<title>
		<s:text name="EmployeeDocumentationFileUpload.title"/>
	</title>
	<link rel="stylesheet" href="css/forms.css?v=${version}">
	<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css?v=${version}">
</head>
<body>
<div class="${actionName}-page page" id="${actionName}_${methodName}_page">
	<h1>
		<s:text name="EmployeeDocumentationFileUpload.title"/>
			<span class="sub">
				${employee.name}
			</span>
	</h1>

	<s:include value="../_action-messages.jsp"/>

	<form class="form" enctype="multipart/form-data" method="POST">
		<input type="hidden" name="employee" value="${employee.id}"/>
		<input type="hidden" name="competency" value="${competency.id}"/>

		<fieldset class="form">
			<h2 class="formLegend" title="${competency.description}">
				${competency.label}
			</h2>
			<ol>
				<li>
					<label><s:text name="global.ExpirationDate"/></label>
					<input type="text" name="expiration" class="datepicker"/>
				</li>
				<li>
					<s:file name="file"/>
				</li>
			</ol>
		</fieldset>
		<fieldset class="submit">
			<s:submit method="save"
			          cssClass="picsbutton positive"
			          value="%{getText('button.Upload')}"
					/>
			<input type="button"
			       class="picsbutton"
			       data-url="${employee_skills_training}"
			       id="cancel_button"
			       value="<s:text name="button.Cancel" />"
					/>
		</fieldset>
	</form>
</div>
<script type="text/javascript">
	$(function () {
		$('#cancel_button').on('click', function () {
			window.location.href = $(this).attr('data-url');
		});

		$('.datepicker').datepicker({
			changeMonth: true,
			changeYear: true,
			yearRange: '1940:2039',
			showOn: 'button',
			buttonImage: 'images/icon_calendar.gif',
			buttonImageOnly: true,
			buttonText: translate('JS.ChooseADate'),
			constrainInput: true,
			showAnim: 'fadeIn'
		});
	});
</script>
</body>