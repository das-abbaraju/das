<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="availableSet.days">
	<div class="cal_day">
		<h4><s:date name="key" format="EEEE, MMM d" /></h4>
		<div class="cal_times">
		<s:iterator value="value">
			<a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>&button=select&timeSelected=<s:date 
				name="startDate" format="%{@com.picsauditing.actions.audits.ScheduleAudit@DATE_FORMAT}" />"<s:if test="isNeedsExpediteFee(startDate)"> class="expedite"</s:if>>
				
				<s:property value="formatDate(startDate, 'h:mm a')" /> to <s:property value="formatDate(endDate, 'h:mm a z')" />
			</a>
			 <br/>
		</s:iterator>
		</div>
	</div>
</s:iterator>
<s:if test="availableSet.days.size() == 0">
<div class="info"><s:text name="%{scope}.message.NoTimeslotsLeft" /></div>
</s:if>
<s:if test="availableSet.latest != null">
<script type="text/javascript">
startDate = '<s:date name="availableSet.latest" format="MM/dd/yyyy"/>';
<s:if test="availableSet.days.size() > 0">
$('#show_next').removeAttr("disabled");
</s:if>
</script>
</s:if>