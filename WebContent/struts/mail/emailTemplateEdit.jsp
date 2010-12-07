<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Email Template Editor</title>

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/mass_mailer.js?v=<s:property value="version"/>"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<script type="text/javascript">
var type;
$(function(){
	<s:if test="type==null">
		changeType('Audit');
		type = $('#changeType').val();
	</s:if>
	$(window).bind('hashchange', function() {
		var state = $.bbq.getState();
		if(state.template !== undefined) {
			editEmail();
			$('#menu_selector').fadeIn();
			$('#buttonSave').attr({'disabled':'disabled'});	
			$('draftEdit').fadeIn(1000);
			var data = {
				button: 'MailEditorAjax',
				templateID: state.template == 'blank' ? -1 : state.template,
				type: type,
				editTemplate: true
			};

			$('#draftEmail').html('<img src="images/ajax_process2.gif" />');
			$('#draftEmail').load('MailEditorAjax.action', data);
		}
	});
});
function changeType(type){	
	$('#tempTitle').text('Editing '+type+' Templates');
	$('#emailTemplateTable').load('EditEmailTemplateAjax.action', {type: type});
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
<h1 id="tempTitle">Editing Templates</h1>
<div id="messages">
	<s:include value="../actionMessages.jsp" />
</div>
Template Type: <s:select id="changeType" list="{'Audit', 'Contractor', 'User'}" onchange="changeType($(this).val()); return false;" />
<table id="emailTemplateTable">	
	<s:include value="editTemplateList.jsp" />
</table>
</body>
</html>