<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Option Values</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<style type="text/css">
.optionNumber {
	background-image: url('js/jquery/dataTables/images/sort_both.png');
	background-repeat: no-repeat;
	background-position: center left;
	cursor: move;
}
</style>
<s:include value="../jquery.jsp"/>
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
			$('#optionValues-info').load('OrderAuditChildrenAjax.action?id=<s:property value="type.id"/>&type=AuditOptionValue', 
				sortList.sortable('serialize').replace(/\[|\]/g,''), 
				function() {
					startThinking({div: optionValues, message: "Loading updated list..."});
					$('#optionValues').load('ManageOptionValue!listAjax.action?group=<s:property value="group.id" />', function() {
						setupSortable();
					});
				}
			);
		}
	}).disableSelection();
}
$(function() {
	setupSortable();
	
	$('a.add, a.edit').click(function(e) {
		e.preventDefault();
		$('#editForm').load($(this).attr('href'));
	});
});
</script>
</head>
<body>
<h1>Manage Option Value<span class="sub"><s:property value="group.name" /></span></h1>
<a href="ManageOptionGroup.action">&lt;&lt; Back to Manage Option Group</a>
<br />

<table style="width: 100%;">
	<tr>
		<td style="width: 50%;">
			<div id="optionValues">
				<s:include value="manage_option_value_list.jsp" />
			</div>
			<div id="optionValues-info"></div>
		</td>
		<td style="padding-left: 20px; vertical-align: top;">
			<a href="ManageOptionValue!editAjax.action" class="add">Add New Option Value</a>
			<div id="editForm"></div>
		</td>
	</tr>
</table>

</body>
</html>