<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- controls form to be needing reschedule --%>
<s:if test="needsReschedulingFee">
    <s:set var="reschedule_fee_class">reschedule-fee</s:set>
</s:if>
<s:else>
    <s:set var="reschedule_fee_class"></s:set>
</s:else>


	<title><s:text name="ScheduleAudit.title" /></title>
	
	<meta name="help" content="Scheduling_Audits">
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit/schedule_audit.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/timeentry/jquery.timeentry.css?v=${version}" />
	
	<s:include value="../jquery.jsp"></s:include>
	
	<script src="js/jquery/timeentry/jquery.timeentry.min.js?v=${version}" type="text/javascript"></script>
	<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?v=3.6&sensor=false&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>

    <script type="text/javascript" src="js/audit/schedule_audit_edit.js?v=<s:property value="version"/>"></script>

	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
        <s:include value="../contractors/conHeader.jsp" />
        
        <h2>
            <s:text name="ScheduleAudit.label.Reschedule">
                <s:param><s:text name="%{conAudit.auditType.getI18nKey('name')}" /></s:param>
            </s:text>
        </h2>

        <s:if test="needsReschedulingFee">
        <div class="alert">
            <s:text name="ScheduleAudit.message.ReschedulingWarning">
                <s:param value="%{conAudit.contractorAccount.country.getAmount(rescheduling)}" />
                <s:param value="%{conAudit.contractorAccount.country.currency.symbol}" />
            </s:text>
        </div>
        </s:if>

        <s:form cssClass="schedule-audit-form schedule-audit-edit-form %{#reschedule_fee_class}">
        	<br />
	        <div>
				<fieldset>
		            <a href="MySchedule.action?currentUserID=<s:property value="conAudit.auditor.id"/>" class="picsbutton" target="_BLANK" title="<s:text name="global.NewWindow" />">
		                <s:text name="ScheduleAudit.button.OpenAuditorSchedule">
		                    <s:param value="%{conAudit.auditor.name}" />
		                </s:text>
		            </a>
		        
					<pics:permission perm="AuditEdit" type="Edit">
						<s:if test="conAudit.scheduledDate != null">
			                    <s:submit cssClass="picsbutton negative" method="cancelAudit" value="%{getText('button.cancelAudit')}" />
				        </s:if>
			        </pics:permission>
	           	</fieldset>
	        </div>
	        <br />
        
            <s:hidden name="conID" value="%{conAudit.contractorAccount.id}" />
            <s:hidden name="auditID" />
            
            <fieldset class="form">
                <h2 class="formLegend"><s:text name="ScheduleAudit.label.DateTime" /></h2>
                
                <ol>
                    <li>
                        <label><s:text name="ScheduleAudit.label.AuditDate" />:</label>
                        <input type="text" name="scheduledDateDay" id="scheduledDateDay" data-date="<s:date name="conAudit.scheduledDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />" value="<s:date name="conAudit.scheduledDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />" />
                        <s:date name="conAudit.scheduledDate" nice="true" />
                    </li>
                    
                    <s:if test="needsReschedulingFee">
                        <input type="hidden" name="feeOverride" value="false" />
                        
                        <div id="needsReschedulingFee" class="alert">
                            <s:text name="ScheduleAudit.message.ReschedulingWarning">
                                <s:param value="%{conAudit.contractorAccount.country.getAmount(rescheduling)}" />
                                <s:param value="%{conAudit.contractorAccount.country.currency.symbol}" />
                            </s:text>
                            <br />
                            <input type="button" class="btn success reschedule-fee-continue" value="<s:text name="button.Continue" />" />
                            
                            <s:if test="permissions.userId == 1029 || permissions.userId == 935 || permissions.userId == 11503 || permissions.userId == 38048">
                                <!-- This option is available for Mina, Harvey, Gary, and Rick only -->
                                <input type="button" class="btn reschedule-fee-override" value="<s:text name="ScheduleAudit.button.OverrideFee" />" />
                            </s:if>
                        </div>
                    </s:if>
                    
                    <li>
                        <label><s:text name="ScheduleAudit.label.AuditTime" />:</label>
                        <s:textfield name="scheduledDateTime" id="scheduledDateTime" value="%{formatDate(conAudit.scheduledDate, 'h:mm a')}" cssClass="time"/>
                        <s:property value="permissions.timezone.displayName"/>
                    </li>
                    <li>
                        <label><s:text name="global.SafetyProfessional" />:</label>
                        <s:select list="safetyList" listKey="id" listValue="name" name="auditor.id" value="conAudit.auditor.id"/>
                    </li>
                    <li>
                        <label><s:text name="global.Location" />:</label>
                        <s:radio
                            name="conAudit.conductedOnsite"
                            list="#{false: getText('ScheduleAudit.message.Web'), true: getText('ScheduleAudit.message.OnSite')}"
                            theme="pics"
                            cssClass="inline"
                        />
                    </li>
                    
                </ol>
            </fieldset>
            
            <fieldset class="form">
                <h2 class="formLegend"><s:text name="global.Location" /></h2>
                
                <ol style="float: left;">
                    <li>
                        <s:text name="ScheduleAudit.message.AddressConducted" />
                    </li>
                    <li>
                        <input type="button" class="update-location-contractor-info" value="<s:text name="ScheduleAudit.button.UseContractorInfo" />"/>
                    </li>
                    <li>
                        <s:textfield id="conAudit_address" name="conAudit.address" theme="form" />
                    </li>
                    <li>
                        <s:textfield id="conAudit_address2" name="conAudit.address2" theme="form" />
                    </li>
                    <li class="calculatedAddress">
                        <s:textfield id="conAudit_city" name="conAudit.city" theme="form" />
                    </li>
                    <li class="calculatedAddress">
                        <label><s:text name="CountrySubdivision" />:</label>
                        <s:select id="conAudit_countrySubdivision" name="conAudit.countrySubdivision" list="countrySubdivisionList" listKey="isoCode" listValue="name" headerKey="" headerValue=" - Country Subdivision - "/>
                    </li>
                    <li>
                        <s:textfield id="conAudit_zip" name="conAudit.zip" size="10" theme="form" />
                    </li>
                    <li class="calculatedAddress">
                        <label><s:text name="Country" />:</label>
                        <s:select id="conAudit_country" name="conAudit.country" list="countryList" listKey="isoCode" listValue="name" headerKey="" headerValue=" - Country - "/>
                    </li>
                    <li class="calculatedAddress">
                        <s:textfield id="conAudit_latitude" name="conAudit.latitude" size="10" theme="form" />
                    </li>
                    <li class="calculatedAddress">
                        <s:textfield id="conAudit_longitude" name="conAudit.longitude" size="10" theme="form" />
                    </li>
                    <li id="unverifiedLI" style="display: none;">
                        <s:checkbox id="unverifiedCheckbox" onchange="$('#submitButton').toggle()" name="unverifiedCheckbox" />
                        <s:text name="ScheduleAudit.message.AddressIsCorrect" />
                    </li>
                </ol>
                
                <div id="mappreview"></div>
            </fieldset>
            
            <fieldset class="form">
                <h2 class="formLegend"><s:text name="ScheduleAudit.label.ContactPerson" /></h2>
                
                <ol>
                    <li>
                        <s:text name="ScheduleAudit.message.PrimaryRepresentative" />
                    </li>
                    <li>
                        <label><s:text name="User.name" />:</label>
                        <s:textfield name="conAudit.contractorContact" />
                    </li>
                    <li>
                        <label><s:text name="User.email" />:</label>
                        <s:textfield name="conAudit.phone2" />
                    </li>
                    <li>
                        <label><s:text name="User.phone" />:</label>
                        <s:textfield name="conAudit.phone" />
                    </li>
                </ol>
            </fieldset>
            
            <fieldset class="form submit">
                <div>
                    <s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
                </div>
            </fieldset>
        </s:form>
    </div>