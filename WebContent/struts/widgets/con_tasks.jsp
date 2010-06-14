<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<body>

<label>
	<s:if test="openTasks.size == 0">
		You have no more outstanding tasks.
	</s:if>
	<s:if test="openTasks.size == 1">
		You have 1 more outstanding task.
	</s:if>
	<s:if test="openTasks.size > 1">
		You have <s:property value="openTasks.size" /> more outstanding tasks.
	</s:if>
</label>
<ol>
<s:iterator value="openTasks">
	<li><s:property escape="false"/></li>
</s:iterator>
</ol>
<s:if test="reminderTask">
	<label>Reminder : </label>Please <a href="ContractorFacilities.action?id=<s:property value="contractor.id"/>">update your facility list</a>
</s:if>
<s:if test="showAgreement">
<script type="text/javascript">
$(function() {
	$('a[rel*=facebox]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
 });
</script>
</s:if>
</body>
</html>
