<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:iterator value="availableSet.days">

    <s:if test="isNeedsExpediteFee(key)">
        <s:set var="expidite" value="true" />
    </s:if>
    <s:else>
        <s:set var="expidite" value="false" />
    </s:else>
	<div class="cal_day <s:if test="#expidite">rush_date</s:if>">
        <s:if test="expidite">
            <p>
               <s:text name="ScheduleAudit.RushAudit.Name" /><br />
               <s:text name="ScheduleAudit.RushAudit.Fee" />
            </p>
        </s:if>
        <h4><s:date name="key" format="EE, MMM d" /></h4>
        <div class="cal_times">
            <s:iterator value="value" var="auditorAvailability">
                <a href="ScheduleAudit!selectTime.action?auditID=<s:property value="conAudit.id"/>&selectedTimeZone=<s:property value="selectedTimeZone" />&timeSelected=<s:date 
                    name="startDate" format="%{@com.picsauditing.actions.audits.ScheduleAudit@DATE_FORMAT}" />"<s:if test="isNeedsExpediteFee(startDate)"> class="expedite"</s:if> data-date="<s:date name="startDate" format="yyyy-MM-dd" />">
                    <s:text name="ScheduleAudit.link.DateSelector2">
                        <s:param value="%{#auditorAvailability.getTimeZoneStartDate(getSelectedTimeZone())}" />
                        <s:param value="%{#auditorAvailability.getTimeZoneEndDate(getSelectedTimeZone())}" />
                    </s:text>
                </a>
                <br/>
            </s:iterator>
        </div>
    </div>
</s:iterator>

<s:if test="availableSet.days.size() == 0">
    <div class="info">
        <s:text name="ScheduleAudit.message.NoTimeslotsLeft" />
    </div>
</s:if>

<s:if test="availableSet.latest != null">
    <script type="text/javascript">
		startDate = '<s:date name="availableSet.latest" format="%{getText('date.short')}" />';
		<s:if test="availableSet.days.size() > 0">
			$('#show_next').removeAttr("disabled");
		</s:if>
	</script>
</s:if>