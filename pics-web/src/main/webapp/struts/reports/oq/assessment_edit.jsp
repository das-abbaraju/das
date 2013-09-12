<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
	<title>
		<s:text name="AssessmentCenterEdit.title" />
	</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css?v=${version}" />
	
	<s:include value="../../jquery.jsp" />
	<script type="text/javascript">
		function changeCountrySubdivision(country) {
			$('#countrySubdivision_li').load('CountrySubdivisionListAjax.action',{countryString: $('#centerCountry').val(), countrySubdivisionString: '<s:property value="center.countrySubdivision.isoCode"/>'});
		}
		
		function countryChanged(country) {
			changeCountrySubdivision(country);
		}
		
		$(function() {
			changeCountrySubdivision($("#centerCountry").val());
			$('.datepicker').datepicker();
			
			$('#centerCountry').live('change', function() {
			   countryChanged($(this).val()); 
			});
		});
	</script>
</head>
<body>
	<s:include value="assessmentHeader.jsp" />
	
	<s:form action="AssessmentCenterSave">
		<div>
			<s:submit cssClass="picsbutton positive" value="%{getText('button.Save')}" />
		</div>
		<br clear="all" />
		<s:hidden name="id" />
		<fieldset class="form">
			<h2 class="formLegend">
				<s:text name="AssessmentCenterEdit.Details" />
			</h2>
			<ol>
				<li>
					<s:textfield name="center.name" size="35" theme="form" />
				</li>
				<s:if test="id > 0">
					<li>
						<label>
							<s:text name="global.ContactPrimary" />
						</label>
						<s:select
							list="users"
							name="contact"
							listKey="id"
							listValue="name" 
							headerKey=""
							headerValue="- %{getText('FacilitiesEdit.SelectAUser')} -"  
							value="%{center.primaryContact.id}"
						/>
						<s:url var="add_user" action="UsersManage" method="add">
							<s:param name="account" value="%{center.id}" />
							<s:param name="isActive" value="Yes" />
							<s:param name="isGroup" value="No" />
							<s:param name="userIsGroup" value="No" />
						</s:url>
						<a href="${add_user}">
							<s:text name="FacilitiesEdit.AddUser" />
						</a>
					</li>
				</s:if>				
			</ol>
		</fieldset>
		<fieldset class="form">
			<h2 class="formLegend">
				<s:text name="global.PrimaryAddress" />
			</h2>
			<ol>
				<li>
					<s:textfield name="center.address" size="35" theme="form" />
				</li>
				<li>
					<s:textfield name="center.city" size="20" theme="form" />
				</li>
				<li>
					<s:select
						list="countryList"
						id="centerCountry"
						name="center.country" 
						listKey="isoCode"
						listValue="name"
						headerKey=""
						headerValue="- %{getTextNullSafe('Country')} -"
					/>
				</li>
				<li id="countrySubdivision_li"></li>
				<li>
					<s:textfield name="center.zip" size="7" theme="form" />
				</li>
				<li>
					<s:textfield name="center.phone" theme="form" />
				</li>
				<li><s:textfield name="center.fax" theme="form" />
				</li>
				<li> 
					<s:textfield name="center.webUrl" size="30" theme="form" />
				</li>
			</ol>
		</fieldset>
		<fieldset class="form">
			<h2 class="formLegend">
				<s:text name="FacilitiesEdit.CompanyIdentification" />
			</h2>
			<ol>
				<li>
					<s:textarea name="center.description" cols="40" rows="15" theme="form" />
				</li>
			</ol>
			</fieldset>
			<fieldset class="form">
			<s:if test="permissions.admin">
				<h2 class="formLegend">Admin Fields</h2>
				<ol>
					<li>
						<s:select list="statusList" name="center.status" theme="form" />
					</li>
					<li>
						<s:textarea name="center.reason" rows="3" cols="25" theme="form" />
					</li>
				</ol>
			</s:if>
		</fieldset>
		<fieldset class="form submit">
			<s:submit cssClass="picsbutton positive" value="%{getText('button.Save')}" />
		</fieldset>
	</s:form>
</body>