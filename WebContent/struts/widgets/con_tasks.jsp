<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
	<body>
		<label>
			<s:if test="openTasks.size == 0">
				<s:text name="ContractorStats.message.NoMoreTasks" />
			</s:if>
			
			<s:if test="openTasks.size == 1">
				<s:text name="ContractorStats.message.OneMoreTask" />
			</s:if>
			
			<s:if test="openTasks.size > 1">
				<s:text name="ContractorStats.message.MoreTasks" >
					<s:param value="%{openTasks.size}" />
				</s:text>
			</s:if>
		</label>
		
		<ol>
			<s:iterator value="openTasks">
				<li>
					<s:property escape="false"/>
				</li>
			</s:iterator>
		</ol>
		
		<s:if test="reminderTask">
			<label><s:text name="global.Reminder" />:</label>
			<s:text name="ContractorStats.message.FacilityReminder" />
			
			<s:if test="suggestedOperators.size() > 0">
				<s:text name="ContractorStats.message.SuggestFiveOperators" />:
				<s:iterator value="suggestedOperators" status="stat">
					<a href="ContractorFacilities.action?id=<s:property value="contractor.id"/>&operator.name=<s:property value="get('name')"/>"><s:property value="get('name')"/></a>
					<s:if test="!#stat.last">, </s:if>
				</s:iterator> 
			</s:if>
			
			<s:text name="ContractorStats.link.UpdateYourFacilityList" >
				<s:param value="%{contractor.id}" />
			</s:text>
		</s:if>
		
		<s:if test="showAgreement">
			<script type="text/javascript">
				$(function() {
					$('a[rel*="facebox"]').facebox({
						loading_image : 'loading.gif',
						close_image : 'closelabel.gif'
					});
				});
			</script>
		</s:if>
	</body>
</html>