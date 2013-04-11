<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title><s:text name="ScheduleAudit.title" /></title>
	
	<meta name="help" content="Scheduling_Audits">
	
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit/schedule_audit.css?v=<s:property value="version"/>" />
	
	<s:include value="../jquery.jsp"></s:include>
	
	<script type="text/javascript" src="//maps.googleapis.com/maps/api/js?v=3.6&sensor=false&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>
	<script type="text/javascript" src="js/audit/schedule_audit_address.js?v=<s:property value="version"/>"></script>
</head>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<s:include value="../contractors/conHeader.jsp" />
	
	<div class="info" style="clear:left"><s:text name="ScheduleAudit.message.EnterPrimaryRepresentative" /></div>
	
	<s:form cssClass="schedule-audit-form schedule-audit-address-form">
		<s:hidden name="auditID" />
        <s:hidden id="conAudit_latitude" name="conAudit.latitude" />
        <s:hidden id="conAudit_longitude" name="conAudit.longitude" />
        
        <pics:permission perm="AuditEdit" type="Edit">
	        <fieldset class="form">
	        	<h2 class="formLegend"><s:text name="ScheduleAudit.button.EditScheduleManually" /></h2>
	        	<ol>
		        	<li>
						<a class="picsbutton" href="ScheduleAudit!edit.action?auditID=<s:property value="auditID" />">
							<s:text name="ScheduleAudit.button.EditScheduleManually" />
						</a>
					</li>
				</ol>
	        </fieldset>
        </pics:permission>
		
		<fieldset class="form">
			<h2 class="formLegend"><s:text name="ScheduleAudit.label.ContactPerson" /></h2>
			<ol>
				<li>
					<label><s:text name="User.name" />:</label>
					<s:textfield name="conAudit.contractorContact" value="%{conAudit.contractorAccount.primaryContact.name}" />
				</li>
				<li>
					<label><s:text name="User.email" />:</label>
					<s:textfield name="conAudit.phone2" value="%{conAudit.contractorAccount.primaryContact.email}"/>
				</li>
				<li>
					<label><s:text name="User.phone" />:</label>
					<s:textfield name="conAudit.phone" value="%{conAudit.contractorAccount.primaryContact.phone}"/>
				</li>
			</ol>
		</fieldset>
		
		<fieldset class="form">
			<h2 class="formLegend"><s:text name="ScheduleAudit.label.EnterAuditLocation" /></h2>
            
			<ol>
				<li class="calculated-address">
					<label><s:text name="global.Address" />:</label>
					<s:textfield id="conAudit_address" name="conAudit.address" size="50" value="%{conAudit.contractorAccount.address}"/>
                    <s:text name="ScheduleAudit.message.NoPOBoxes" />
				</li>
				<li class="calculated-address">
					<label><s:text name="global.Address" /> 2:</label>
					<s:textfield id="conAudit_address2" name="conAudit.address2" value="%{conAudit.contractorAccount.address2}"/>
                    <s:text name="ScheduleAudit.message.SuiteApartment" />
				</li>

                <s:set var="countryString" value="%{conAudit.contractorAccount.country.isoCode}" />

                <li class="calculated-address">
                    <label><s:text name="Country" />:</label>
                    <s:select 
                        id="conAudit_country" 
                        name="conAudit.country"
                        value="#countryString"
                        list="countryList"
                        listKey="isoCode" 
                        listValue="name" 
                        headerKey="" 
                        headerValue=" - %{getText('Country')} - "
                        data-contractor="%{conAudit.contractorAccount.id}"
                    />
                </li>
                <li id="conAudit_countrySubdivision_li" class="calculated-address">
                    <s:include value="../contractors/_con-country.jsp"></s:include>
                </li>
                <li class="calculated-address">
					<label><s:text name="global.City" />:</label>
					<s:textfield id="conAudit_city" name="conAudit.city" value="%{conAudit.contractorAccount.city}" />
				</li>
				<li id="conAudit_zip_li" class="calculated-address">
					<label><s:text name="global.ZipPostalCode" />:</label>
					<s:textfield id="conAudit_zip" name="conAudit.zip" size="10" value="%{conAudit.contractorAccount.zip}"/>
				</li>
				<li class="verify-address-manual" style="display: none;">
					<s:checkbox id="unverifiedCheckbox" name="unverifiedCheckbox" />
					<s:text name="ScheduleAudit.message.AddressIsCorrect" />
				</li>
			</ol>
		</fieldset>
		
		<fieldset class="form submit">
			<div>
				<button id="verifyButton" class="picsbutton" type="button"><s:text name="ScheduleAudit.button.VerifyAddress" /></button>
				<s:submit id="submitButton" cssStyle="display: none;" cssClass="picsbutton positive" method="address" value="%{getText('button.Next') + ' >>'}" />
			</div>
		</fieldset>
	</s:form>
</div>
