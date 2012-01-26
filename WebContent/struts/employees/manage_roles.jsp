<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>
			<s:text name="ManageJobRoles.title" />
		</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		
		<style type="text/css">
			#roleForm {
				clear: right;
			}
			
			#rolesTable td.leftCell, #rolesTable td.rightCell {
				vertical-align: top;
			}
			
			#rolesTable table.report {
				margin-right: 10px;
				margin-bottom: 10px;
			}
			
			fieldset.form label {
				width: 5em;
				margin-right: 0px;
			}
			
			td.leftCell {
				width: 50%;
			}
			
			td.rightCell {
				padding-left: 10px;
			}
		</style>
		
		<s:include value="../jquery.jsp" />
		
		<script type="text/javascript">
			var accountID = '<s:property value="account.id" />';
			
			$(function() {
				$('#roleInputBox').autocomplete('RoleSuggestAjax.action',{
					minChars: 1,
					formatItem: function(data,i,count) {
						return data[1];
					}
				});
				
				$('#roleCell').delegate('.removeCompetency', 'click', function(e) {
					e.preventDefault();
					$("a.compEditor").hide();
			
					var competencyID = $(this).closest('tr').attr('id');
					var roleID = $(this).closest('table').attr('id');
					
					$('#jobCompetencyList').load('ManageJobRoles!removeCompetency.action', {role: roleID, competency: competencyID});
				});
				
				$('#roleCell').delegate('.addCompetency', 'click', function(e) {
					e.preventDefault();
					$("a.compEditor").hide();
					
					var competencyID = $(this).closest('tr').attr('id');
					var roleID = $(this).closest('table').attr('id');
					
					$('#jobCompetencyList').load('ManageJobRoles!addCompetency.action', {role: roleID, competency: competencyID});
				});
				
				$('#roleCell').delegate('.cancelButton', 'click', function(e) {
					e.preventDefault();
					$('#roleCell').empty();
				});
				
				$('#roleCell').delegate('.deleteButton', 'click', function(e) {
					return confirm(translate('JS.ManageJobRoles.confirm.RemoveJobRole'));
				});
				
				$('#addLink').live('click', function(e) {
					e.preventDefault();
					$('#roleCell').load('ManageJobRoles!get.action');
				});
				
				$('.roleLink').live('click', function(e) {
					e.preventDefault();
					startThinking({div: 'roleCell', message: translate('JS.ManageJobRoles.message.LoadingJobRole')});
					$('#roleCell').load('ManageJobRoles!get.action', { role: $(this).attr('id'), id: accountID });
				});
			});
		</script>
	</head>
	<body>
		<h1>
			<s:property value="account.name" />
			<span class="sub">
				<s:text name="ManageJobRoles.title" />
			</span>
		</h1>
		<s:include value="../actionMessages.jsp" />
		
		<s:if test="auditID > 0">
			<div class="info">
				<s:text name="ManageJobRoles.Step1">
					<s:param>
						<s:property value="auditID" />
					</s:param>
					<s:param>
						<s:text name="AuditType.99.name" />
					</s:param>
				</s:text>
			</div>
		</s:if>
		
		<table id="rolesTable">
			<tr>
				<td class="leftCell">
					<s:if test="jobRoles.size > 0">
						<table class="report">
							<thead>
								<tr>
									<th><s:text name="ManageJobRoles.label.JobRole" /></th>
									<th><s:text name="global.Active" /></th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="jobRoles">
									<tr>
										<td><a href="#" id="<s:property value="id" />" class="roleLink<s:if test="!active"> inactive</s:if>"><s:property value="name" /></a></td>
										<td class="center"><s:if test="active"><s:text name="YesNo.Yes" /></s:if><s:else><s:text name="YesNo.No" /></s:else></td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if>
					<a href="#" id="addLink" class="add">
						<s:text name="ManageJobRoles.link.AddNewJobRole" />
					</a>
					<br />
					<div class="info">
						<a href="resources/HSECompetencyReview.pdf"><s:text name="ManageJobRoles.link.QuestionReviewPDF" /></a>
						<br />
						<s:text name="ManageJobRoles.help.QuestionReviewPDF" />
					</div>
				</td>
				<td class="rightCell">
					<div id="roleCell"></div>
				</td>
			</tr>
		</table>
	</body>
</html>
