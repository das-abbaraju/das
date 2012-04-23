<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Option Group</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css?v=${version}"/>
<style type="text/css">
.dataTables_info {
	width: 100% !important;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js?v=${version}"></script>
<pics:permission perm="ManageAudits" type="Edit">
<script type="text/javascript">
var dtable;
$(function () {
	$(window).bind('hashchange', function() {
		startThinking({div: 'editForm', message: 'Loading Option Group...'});
		$('#editForm').load('ManageOptionGroup!editAjax.action', location.hash.substring(1));
	});
	
	$('a.add').click(function(e) {
		e.preventDefault();
		$('#editForm').load($(this).attr('href'));
	});
	
	dtable = $('.report').dataTable({
		aoColumns: [
		            { bSortable: true },
		            { bSortable: true },
		            { bSortable: true },
		            { bSortable: false }
		            <pics:permission perm="ManageAudits" type="Edit">
		            , { bSortable: false }
		            </pics:permission>
		],
		bLengthChange: false,
		iDisplayLength: 15,
		sPaginationType: "full_numbers"
	});
});
</script>
</pics:permission>
</head>
<body>
<h1>Manage Audit Options<span class="sub">Option Groups</span></h1>

<s:include value="../config_environment.jsp" />

<s:include value="../actionMessages.jsp" />
<s:if test="editOnly">
	<s:if test="question != null">
		<a href="ManageQuestion.action?id=<s:property value="question.id" />">&lt;&lt; Back to Manage Question</a><br/>
	</s:if>
	<a href="ManageOptionGroup.action?question=<s:property value="question.id" />" class="preview">View all option groups</a>
	<s:include value="manage_option_group_edit.jsp" />
</s:if>
<s:else>
	<table style="width: 100%;">
		<tr>
			<td style="width: 50%;">
				<s:if test="question != null">
					<a href="ManageQuestion.action?id=<s:property value="question.id" />">&lt;&lt; Back to Manage Question</a>
				</s:if>
				<table class="report">
					<thead>
						<tr>
							<th>Name</th>
							<th>Radio</th>
							<th>Unique Code</th>
							<th>Values</th>
							<pics:permission perm="ManageAudits" type="Edit">
								<th>Edit</th>
							</pics:permission>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="all">
							<tr id="row_<s:property value="id" />">
								<td class="optionName"><s:property value="name" /></td>
								<td class="optionRadio center"><s:if test="radio"><img src="images/okCheck.gif" alt="Edit" /></s:if></td>
								<td class="optionUniqueCode"><s:property value="uniqueCode" /></td>
								<td class="center"><a href="ManageOptionValue.action?group=<s:property value="id"/>" class="preview"></a></td>
								<pics:permission perm="ManageAudits" type="Edit">
									<td><a href="#group=<s:property value="id" />" class="edit"></a></td>
								</pics:permission>
							</tr>
						</s:iterator>
						<s:if test="all.size == 0">
							<tr>
								<td colspan="6">No Option Types found</td>
							</tr>
						</s:if>
					</tbody>
				</table>
			</td>
			<td style="padding-left: 20px; vertical-align: top;">
				<pics:permission perm="ManageAudits" type="Edit">
					<a href="ManageOptionGroup!editAjax.action" class="add">Add new option group</a>
					<div id="editForm"></div>
				</pics:permission>
			</td>
		</tr>
	</table>
</s:else>
</body>
</html>