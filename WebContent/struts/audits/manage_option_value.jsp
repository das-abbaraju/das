<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Option Values</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<pics:permission perm="ManageAudits" type="Edit">
<style type="text/css">
	.optionNumber {
		background-image: url('js/jquery/dataTables/images/sort_both.png');
		background-repeat: no-repeat;
		background-position: center left;
		cursor: move;
	}
</style>
<script type="text/javascript">
function setupSortable() {
	var sortList = $('#optionValues table.report tbody').sortable({
		helper: function(e, tr) {
		  var $originals = tr.children();
		  var $helper = tr.clone();
		  $helper.children().each(function(index) {
			  $(this).width($originals.eq(index).width())
		  });
		  
		  return $helper;
		},
		update: function() {
			$('#optionValues-info').load('OrderAuditChildrenAjax.action?id=<s:property value="group.id"/>&type=AuditOptionValue', 
				sortList.sortable('serialize').replace(/\[|\]/g,''), 
				function() {
					startThinking({div: optionValues, message: "Loading updated list..."});
					$('#optionValues').load('ManageOptionValue!listAjax.action?group=<s:property value="group.id" />', function() {
						setupSortable();
					});
				}
			);
		},
		disabled: ($('#optionValues table.report tbody').children().length <= 1)
	}).disableSelection();
	
	if ($('#optionValues table.report tbody').children().length <= 1)
		$('#optionValues table.report tbody td').removeClass('optionNumber');
}

$(function() {
	setupSortable();
	
	$(window).bind('hashchange', function(){
		startThinking({div: 'editForm', message: 'Loading Option Value...'});
		$('#editForm').load('ManageOptionValue!editAjax.action?group=<s:property value="group.id" />', location.hash.substring(1));
		$('html, body').animate({ scrollTop: $('a.add').offset().top });
	});
	
	$('a.add').click(function(e) {
		e.preventDefault();
		$('#editForm').load($(this).attr('href'));
	});
});
</script>
</pics:permission>
</head>
<body>
<h1>Manage Option Value<span class="sub"><s:property value="group.name" /></span></h1>

<s:include value="../config_environment.jsp" />

<s:include value="../actionMessages.jsp" />
<table style="width: 100%;">
	<tr>
		<td style="width: 50%;">
			<s:if test="question != null">
				<a href="ManageQuestion.action?id=<s:property value="question.id" />&groupID=<s:property value="group.id" />">&lt;&lt; Back to Manage Question</a><br />
			</s:if>
			<a href="ManageOptionGroup.action">&lt;&lt; Back to Manage Option Group</a><br />
			<div id="optionValues">
				<s:if test="group != null">
					<s:include value="manage_option_value_list.jsp" />
				</s:if>
			</div>
			<div id="optionValues-info"></div>
		</td>
		<td style="padding-left: 20px; vertical-align: top;">
			<pics:permission perm="ManageAudits" type="Edit">
				<a href="ManageOptionValue!editAjax.action?group=<s:property value="group.id" /><s:property value="question != null ? '&question=' + question.id : ''" />" class="add">Add New Option Value</a>
				<div id="editForm"></div>
			</pics:permission>
		</td>
	</tr>
</table>
</body>
</html>