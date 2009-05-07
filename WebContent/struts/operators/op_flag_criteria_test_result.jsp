<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="testCriteria.isFlagged(testValue)">
	<s:property value="testCriteria.flagColor.smallIcon" escape="false"/>
</s:if>
<s:else>
	<s:property value="@com.picsauditing.jpa.entities.FlagColor@Green.smallIcon" escape="false"/>
</s:else>
