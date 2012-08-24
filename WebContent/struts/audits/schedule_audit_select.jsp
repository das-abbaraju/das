<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
    <title><s:text name="ScheduleAudit.title" /></title>

    <meta name="help" content="Scheduling_Audits">

    <link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit/schedule_audit.css?v=<s:property value="version"/>" />

    <s:include value="../jquery.jsp"></s:include>

    <script type="text/javascript" src="js/audit/schedule_audit_select.js?v=<s:property value="version"/>"></script>

    <script type="text/javascript">

    var auditID = <s:property value="conAudit.id"/>;
        var startDate = '<s:date name="availableSet.latest" format="%{getText('date.short')}"/>';

        $(function() {
            $('a.expedite').click(function() {
                return confirm(translate('JS.ScheduleAudit.confirm.RushAudit'));
            });
        });

        </script>
</head>
<body>
    <div id="${actionName}_${methodName}_page" class="${actionName}-page page">
        <s:include value="../contractors/conHeader.jsp" />
        
        <s:form cssClass="schedule-audit-form schedule-audit-select-form">
            <s:hidden name="auditID" />

            <fieldset class="form bottom">
                <h2 class="formLegend"><s:text name="ScheduleAudit.label.ChooseAuditTime" /></h2>

                <ol>
                    <li>
                        <s:select name="selectedTimeZone"
                            id="timezone"
                            value="selectedTimeZone.ID" 
                            theme="form" 
                            label="global.timezone"
                            list="@com.picsauditing.util.TimeZoneUtil@TIME_ZONES_SHORT" />
                    </li>
                    <li>
                        <s:text name="ScheduleAudit.message.ChooseAvailableTime" />
                    </li>
                    <li id="li_availability">
                        <s:include value="_schedule-audit-select-content.jsp"/>
                    </li>
                    <li>
                        <input type="button" id="show_next" class="picsbutton" value="<s:text name="ScheduleAudit.button.ShowMoreTimeslots" />" />
                    </li>
                </ol>
            </fieldset>
        </s:form>
    </div>
</body>