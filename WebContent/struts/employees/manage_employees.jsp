<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min.js"></script>

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />

<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />

<script type="text/javascript">
var employeeID = <s:property value="employee.id > 0 ? employee.id : 0"/>;

$(function() {
	$('.datepicker').datepicker({
			changeMonth: true,
			changeYear:true,
			yearRange: '1940:2039',
			showOn: 'button',
			buttonImage: 'images/icon_calendar.gif',
			buttonImageOnly: true,
			buttonText: 'Choose a date...',
			constrainInput: true,
			showAnim: 'fadeIn'
		});
	<s:if test="employee.id != 0">
		<s:if test="employee.active">
			$('#termDate').hide();
		</s:if>
		<s:else>
			$('#termDate').show();
		</s:else>
	</s:if>
	<s:else>
		$('#termDate').hide();
	</s:else>
});

function show(id) {
	$.getJSON('ManageEmployeesAjax.action',
			{employeeID: id, button: 'load'},
			function (json, result) {
				alert(json);
			}
		);
}

function addJobRole(id) {
	startThinking({div: 'thinking_roles', message: '<s:text name="%{scope}.message.AjaxLoad" />'});
	$('#employee_role').load('ManageEmployees!addRoleAjax.action', {'employee.id': employeeID, childID: id});
}

function addJobSite(selection) {
	var id = $(selection).val();
	var name = $(selection).find('option[value=' + id + ']').text().trim().split(":");
	
	startThinking({div: 'employee_site', message: '<s:text name="%{scope}.message.AjaxLoad" />'});
	$('#employee_site').load('ManageEmployees!addSiteAjax.action', {'employee.id': employeeID, 
		'op.id': id, 'op.name' : name[0]});
}

function removeJobRole(id) {
	var remove = confirm('<s:text name="%{scope}.confirm.RemoveRole" />');

	if (remove) {
		startThinking({div: 'thinking_roles', message: '<s:text name="%{scope}.message.AjaxLoad" />'})
		$('#employee_role').load('ManageEmployees!removeRoleAjax.action', {'employee.id': employeeID, childID: id});
	}

	return false;
}

function removeJobSite(id) {
	var remove = confirm('<s:text name="%{scope}.confirm.RemoveProject" />');

	if (remove) {
		startThinking({div: 'thinking_sites', message: '<s:text name="%{scope}.message.AjaxLoad" />'});
		$('#employee_site').load('ManageEmployees!removeSiteAjax.action', {'employee.id': employeeID, childID: id});
		$.unblockUI();
	}

	return false;
}

function newJobSite() {
	startThinking({div: 'thinking_sites', message: '<s:text name="%{scope}.message.AjaxLoad" />'})
	$('#employee_site').load('ManageEmployees!newSiteAjax.action?' + $('#newJobSiteForm input').serialize(), {'employee.id': employeeID});
}

function editAssignedSites(id) {
	startThinking({div: 'thinking_sites', message: '<s:text name="%{scope}.message.AjaxLoad" />'})
	$('#employee_site').load('ManageEmployees!editSiteAjax.action?' + $('#siteForm_' + id).serialize(), 
			{'employee.id': employeeID, childID: id});

	$.unblockUI();
	return false;
}
function showUpload(){
	url = 'EmployeePhotoUploadAjax.action?employeeID='+employeeID;
	title = '<s:text name="%{scope}.message.UploadPhoto" />';
	pars = 'scrollbars=yes,resizable=yes,width=900,height=700,toolbar=0,directories=0,menubar=0';
	photoUpload = window.open(url,title,pars);
	photoUpload.focus();
}

function getSite(id) {
	$('#siteEditBox').load('ManageEmployees!getSiteAjax.action', {'employee.id': employeeID, childID: id});
	$.blockUI({ message: $('#siteEditBox') });
}

$(function() {
	$.mask.definitions['S']='[X0-9]';
	$('input.ssn').mask('SSS-SS-SSSS');
	$('input.date').mask('99/99/9999');

	$('#employees').dataTable({
			aoColumns: [
		            {bVisible: false},
		            null,
		            null,
		            null,
		            null,
		            null
				],
			aaSorting: [[1, 'asc']],
			bJQueryUi: true,
			bStateSave: true,
			bLengthChange: false,
			oLanguage: {
				sSearch:"Search",
				sLengthMenu: '_MENU_', 
				sInfo:"_START_ to _END_ of _TOTAL_",
				sInfoEmpty:"",
				sInfoFiltered:"(filtered from _MAX_)" },
			fnRowCallback: function( nRow, aData, iDisplayIndex ) {
				if (aData[0] == employeeID)
					$(nRow).not('.highlight').addClass('highlight');

				return nRow;
			}
		});
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
	$('#locationSuggest').autocomplete(<s:property value="previousLocationsJSON" escape="false"/>);
	$('#titleSuggest').autocomplete(<s:property value="previousTitlesJSON" escape="false"/>);
});

function showExcelUpload() {
	url = 'ManageEmployeesUpload.action?accountID=<s:property value="account.id" />';
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}
</script>

<style>
div.dataTables_filter { width: 65%; }
div.dataTables_length { width: 35%; }
.newJobSite { display: none; }
</style>
</head>
<body>
	<s:if test="auditID > 0">
		<div class="info"><a href="Audit.action?auditID=<s:property value="auditID" />"><s:text name="%{scope}.message.ReturnTo" /> <s:text name="AuditType.99.name" /></a></div>
	</s:if>
	<h1><s:property value="account.name" /><span class="sub"><s:text name="%{scope}.title" /></span></h1>
	<s:include value="../actionMessages.jsp"/>

	<s:if test="account.employees.size() == 0 && employee == null">
		<div class="info">
			<s:text name="%{scope}.message.NoEmployees">
				<s:param><s:text name="%{scope}.link.Add" /></s:param>
			</s:text>
		</div>
	</s:if>

	<a href="?id=<s:property value="account.id"/>&button=Add" class="add"><s:text name="%{scope}.link.Add" /></a><br />
	<a href="#" onclick="showExcelUpload(); return false;" class="add"><s:text name="%{scope}.link.Import" /></a>

	<table>
		<tr>
			<s:if test="account.employees.size() > 0">
				<td style="vertical-align:top; width: 25%;">
					<table class="report" id="employees">
						<thead>
							<tr>
								<th>id</th>
								<th>Last Name</th>
								<th>First Name</th>
								<th>Title</th>
								<th>Classification</th>
								<th><s:text name="%{scope}.message.Profile" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="account.employees" id="e">
								<tr>
									<td><s:property value="#e.id"/></td>
									<td><a href="?employee.id=<s:property value="#e.id" />"><s:property value="#e.lastName"/></a></td>
									<td><a href="?employee.id=<s:property value="#e.id" />"><s:property value="#e.firstName"/></a></td>
									<td><s:property value="#e.title"/></td>
									<td><s:property value="#e.classification"/></td>
									<td class="center"><a href="EmployeeDetail.action?employee.id=<s:property value="#e.id" />"><s:text name="%{scope}.link.View" /></a></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</td>
				
				<td style="width: 20px;"></td>
			</s:if>
			<s:if test="employee != null">
				<td style="vertical-align:top;">
					<s:if test="employee.id > 0">
						<a href="EmployeeDetail.action?employee.id=<s:property value="employee.id" />"><s:text name="%{scope}.link.ViewProfile" /></a>
						<a href="#" class="help cluetip" rel="#cluetip1" title="View Profile/Assigned Tasks"></a>
						<div id="cluetip1"><s:text name="%{scope}.help.ProfileInfo" /></div>
						<br clear="all" />
					</s:if>
					<s:form id="employeeForm">
						<s:hidden name="id"/>
						<s:hidden name="employee.id"/>
						<s:if test="!selectRolesSites">
							<fieldset class="form">
								<h2 class="formLegend"><s:text name="%{scope}.header.EmployeeDetails" /></h2>
								<ol>
									<li<s:if test="employee.firstName == null || employee.firstName == ''"> class="required"</s:if>><label>First Name:</label>
										<s:textfield name="employee.firstName" />
										<pics:fieldhelp title="First Name">
											<p>The first given name of the employee. This can include a middle initial or middle name if needed to differentiate between employees.</p>
											<h5>Examples:</h5>
											<ul>
												<li>John</li>
												<li>John Q.</li>
												<li>John Quincy</li>
											</ul>
										</pics:fieldhelp>
									</li>
									<li<s:if test="employee.lastName == null || employee.lastName == ''"> class="required"</s:if>><label>Last Name:</label>
										<s:textfield name="employee.lastName"/>
										<pics:fieldhelp title="Last Name">
											<p>The last name (aka family name) of the employee.</p>
										</pics:fieldhelp>
									</li>
									<li><label>Title:</label>
										<s:textfield id="titleSuggest" name="employee.title"/>
										<pics:fieldhelp title="Title">
										<p>The optional title of the employee.</p>
										<h5>Examples:</h5>
										<ul>
											<li>President</li>
											<li>Senior Engineer</li>
											<li>Apprentice</li>
										</ul>
										<p>Suggestions are based on common titles from all companies located in PICS Organizer.</p>
										</pics:fieldhelp>
									</li>
									<li><label>Birth Date:</label>
										<s:textfield name="employee.birthDate" value="%{maskDateFormat(employee.birthDate)}" cssClass="datepicker"/>
										<pics:fieldhelp title="Birth Date">
										<p>Optional date of birth field. Included for future use.</p>
										</pics:fieldhelp>
									</li>
									<li><label>Classification:</label>
										<s:select name="employee.classification" 
											list="@com.picsauditing.jpa.entities.EmployeeClassification@values()" 
											listValue="description" />
									</li>
									<li><label>Active</label>
										<s:checkbox name="employee.active" onclick="$('#termDate').toggle();"/>
										<pics:fieldhelp title="Active">
										<p>Unchecking this box will remove this employee from most reports. Uncheck this once the person no longer works for your company.</p>
										</pics:fieldhelp>
									</li>
									<li><label>Hire Date:</label>
										<s:textfield name="employee.hireDate" value="%{maskDateFormat(employee.hireDate)}" cssClass="datepicker"/>
										<pics:fieldhelp title="Hire Date">
										<p>The date (or best approximation) the employee first started working for this company.</p>
										</pics:fieldhelp>
									</li>
									<li id="termDate"><label>Termination Date:</label>
										<s:textfield name="employee.fireDate" value="%{maskDateFormat(employee.fireDate)}" cssClass="datepicker"/>
									</li>
									<s:if test="employee.id > 0">
										<s:if test="employee.photo.length() > 0">
											<li><label>Photo:</label>
												<a href="EmployeePhotoUpload.action?employeeID=<s:property value="employee.id"/>" class="edit"><img 
													id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employee.id"/>" 
													style="width: 25px; height: 25px; vertical-align: bottom;" /></a>
											</li>
										</s:if>
										<s:else>
											<li><label>Upload Photo:</label>
												<a href="EmployeePhotoUpload.action?employeeID=<s:property value="employee.id"/>" class="add">Add </a>
											</li>
										</s:else>
									</s:if>
									<li><label>Email:</label>
										<s:textfield name="employee.email"/>
										<pics:fieldhelp title="Email">
										<p>The employee's primary work email address. This optional field is included for future use.
										PICS will not SPAM email addresses or share this address without your permission.</p>
										</pics:fieldhelp>
									</li>
									<li><label>Phone #:</label>
										<s:textfield name="employee.phone"/>
										<pics:fieldhelp title="Phone">
										<p>The employee's primary work phone. This field is optional.</p>
										</pics:fieldhelp>
									</li>
									<li><label>TWIC Card Expiration:</label>
										<s:textfield name="employee.twicExpiration" value="%{maskDateFormat(employee.twicExpiration)}" cssClass="datepicker"/>
										<pics:fieldhelp title="TWIC">
											<p>The expiration date of the employee's TWIC Card if available. If the card is in transit, please provide an estimate of the expiration date.</p>
										</pics:fieldhelp>
									</li>
									<li><label>SSN:</label>
										<s:textfield name="ssn" cssClass="ssn"/>
										<pics:fieldhelp title="Social Security Number">
										<p>The employee's Social Security Number issued by the United States. Leave blank if employee does not work in the USA.
										This field is NOT used directly by PICS. However some third party data providers require this number. You can always add it later if needed.</p>
										</pics:fieldhelp>
									</li>
									<li><label>Location:</label>
										<s:textfield name="employee.location" id="locationSuggest"/>
										<pics:fieldhelp title="Location">
										<p>The employee's primary work location. This could one of your own work locations or the location of one of your clients.</p>
										<h5>Examples:</h5>
										<ul>
											<li>Dallas</li>
											<li>Building C</li>
										</ul>
										<p>Suggestions based on common locations of other employees will appear after you start to type.</p>
										</pics:fieldhelp>
									</li>
								</ol>
							</fieldset>
						</s:if>
						<s:if test="employee.id > 0">
							<s:if test="employee.account.requiresCompetencyReview && (unusedJobRoles.size() + employee.employeeRoles.size()) > 0">
								<fieldset class="form">
									<h2 class="formLegend">Job Roles</h2>
									<div id="employee_role">
										<s:include value="manage_employee_roles.jsp" />
									</div>
								</fieldset>
							</s:if>
							<div id="employee_site">
								<s:include value="manage_employee_sites.jsp" />
							</div>
						</s:if>
						<s:if test="!selectRolesSites">
							<fieldset class="form submit">
								<s:if test="auditID > 0 && employee.id == 0"><s:hidden name="button" value="Continue" /></s:if>
								<s:submit action="ManageEmployees!save" cssClass="picsbutton positive" value="%{auditID > 0 && employee.id == 0 ? getText('button.Continue') : getText('button.Save')}" />
								<s:submit action="ManageEmployees!delete" cssClass="picsbutton negative" onclick="return confirm('%{getText(scope + '.confirm.DeleteEmployee')}');" value="%{getText('button.Delete')}" />
							</fieldset>
						</s:if>
						<s:else>
							<fieldset class="form submit" style="text-align: center;">
								<a href="ManageEmployees.action?employee.id=<s:property value="employee.id" />" class="picsbutton"><s:text name="%{scope}.message.ViewComplete" /></a>
							</fieldset>
						</s:else>
					</s:form>
				</td>
			</s:if>
		</tr>
	</table>
	<div id="siteEditBox" style="display: none;"></div>
</body>
</html>