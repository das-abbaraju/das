<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<head>
	<title>
		<s:text name="EditEmailTemplate.title" />
	</title>
	
	<s:include value="../jquery.jsp"/>
	<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js?v=${version}"></script>
	<script type="text/javascript" src="js/mass_mailer.js?v=<s:property value="version"/>"></script>
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	
	<script type="text/javascript">
		$(function(){
			<s:if test="type==null">
				changeType(); 
			</s:if>
			
			<s:if test="templateID > 0">
				getTemplate(<s:property value="templateID" />);
			</s:if>
			
			$(window).bind('hashchange', function() {
				var state = $.bbq.getState();
				if(state.template !== undefined) {
					getTemplate(state.template);
				}
			});
			
			$('#emailTemplateTable').delegate('.remove', 'click', function(e) {
				e.preventDefault();
				
				var deleteMe = confirm(translate("JS.MassMailer.DeleteTemplate"));
				if (!deleteMe)
					return;
				
				$('#messages').html('');
				$('#messages').load('EmailTemplateSave!delete.action', { template: $(this).data('id') }, 
					function(response, status) {
						if (status=='success')
							$('#li_template'+id).fadeOut();
					}
				);
				
				type = $('#changeType').val();
				$('#emailTemplateTable').load('EditEmailTemplateAjax.action', {type: type});
			});
			
			$('#changeType').live('change', function() {
			    changeType();
			});
		});
		
		function changeType(){	
			type = $('#changeType').val();
			typeTranslated = $('#changeType option[value=' + $('#changeType').val() + ']').text();
			$('#tempTitle').text(translate('JS.EditEmailTemplate.EditingTemplate', [typeTranslated]));
			$('#emailTemplateTable').load('EditEmailTemplateAjax.action', {type: type});
		}
		
		function getTemplate(templateID) {
		    editEmail();
			$('#menu_selector').fadeIn();
			$('#buttonSave').attr({'disabled':'disabled'});	
			$('draftEdit').fadeIn(1000);
			
			if (!type) {
			    type = $('#changeType').val();
			}
			
			var data = {
				button: 'MailEditorAjax',
				templateID: templateID == 'blank' ? -1 : templateID,
				type: type,
				editTemplate: true
			};

			$('#draftEmail').html('<img src="images/ajax_process2.gif" />');
			$('#draftEmail').load('MailEditorAjax.action', data);
		}
	</script>
	<style type="text/css">
		#templateBody {
			color: black;
			background-color: #FAFAFA;
			width: 100%;
		}
		
		#emailPreview {
			color: black;
			background-color: #EEE;
			width: 100%;
			padding: 10px;
		}
		
		#templateChooser {
			padding: 0px;
			list-style-type: none;
			margin: 0px;
		}
		
		#templateChooser li {
			padding: 10px;
			margin: 10px;
		}
		
		#templateChooser a {
			background-color: #EEE;
			padding: 10px;
			text-decoration: none;
			border: 1px solid #DDD;
		}
		
		#templateChooser a:hover {
			background-color: #FFF;
			padding: 10px;
			text-decoration: none;
			border: 1px solid #A84D10;
		}
		
		#menu_selector {
			margin-bottom: 10px;
			border-bottom: 2px dotted #A84D10;
			padding: 20px;
		}
	</style>
</head>
<body>
	<h1 id="tempTitle">
		<s:text name="EditEmailTemplate.EditingTemplates" />
	</h1>
	
	<div id="messages">
		<s:include value="../actionMessages.jsp" />
	</div>
	<s:text name="EditEmailTemplate.TemplateType" />:
	<s:select
		id="changeType"
		name="type"
		list="#{'Audit':getTextNullSafe('ListType.Audit'), 'Contractor':getTextNullSafe('ListType.Contractor'), 'User':getTextNullSafe('ListType.User')}"
	/>
	<table id="emailTemplateTable">	
		<s:include value="editTemplateList.jsp" />
	</table>
</body>