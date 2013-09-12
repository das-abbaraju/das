<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:property value="account.name" /> <s:text name="%{scope}.ContractorNotes.title" /></title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
		
		<script type="text/javascript">
			var accountID = '<s:property value="id"/>';
			var accountType = '<s:property value="account.type"/>';
		</script>

		<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js?v=${version}"></script>
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css?v=${version}" />
		<script type="text/javascript">
		$(function() {
			$('.fancybox').fancybox({
				frameWidth:  640,
				frameHeight: 480,
				hideOnContentClick: false
			});
			
			$('#queue_table').delegate('.remove', 'click', function(e) {
				e.preventDefault();
				var id = $(this).closest('tr').attr('id').split('_')[1];
				
				if (confirm(translate('JS.EmailQueueList.confirm.DeleteEmail'))) {
					$.ajax({
						url: 'EmailQueueList!delete.action',
						data: {id: id},
						success: function() {
							$('#tr_'+id).fadeOut();
						}
					});
				}
			});
		});
		</script>

	</head>
	<body>
		<s:if test="account.contractor">
			<s:include value="../contractors/conHeader.jsp" />
		</s:if>
		<s:else>
			<s:include value="../operators/opHeader.jsp" />
		</s:else>
		
		<h3><s:text name="global.Notes" /></h3>
		
		<div id="notesList">
			<s:include value="account_notes_notes.jsp"></s:include>
		</div>
		<br />
		
		<s:if test="account.contractor">
			<h3><s:text name="ContractorNotes.emailHistory" /></h3>
			
			<div id="notesList">
				<s:include value="account_notes_email.jsp"></s:include>
			</div>
		</s:if>
	</body>
</html>