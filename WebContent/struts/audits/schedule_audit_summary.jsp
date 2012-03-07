<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
    <head>
        <title><s:text name="ScheduleAudit.title" /></title>
        <meta name="help" content="Scheduling_Audits">
        
        <link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
        
        <s:include value="../jquery.jsp"/>
    </head>
    <body>
        <s:include value="../contractors/conHeader.jsp" />
        
        <div class="noprint">
        	<button class="picsbutton" type="button" onclick="window.print();"><s:text name="button.Print" /></button>
        </div>
        
        <fieldset class="form">
            <h2 class="formLegend"><s:text name="ScheduleAudit.header.AuditTimeAndLocation" /></h2>
            
            <ol>
            	<li>
                    <label><s:text name="ScheduleAudit.label.AuditDate" />:</label>
                    <s:date name="conAudit.scheduledDate" format="EEEE, MMM d, yyyy" />
                </li>
            	<li>
                    <label><s:text name="ScheduleAudit.label.AuditTime" />:</label>
                    <s:date name="conAudit.scheduledDate" format="h:mm a z" />
                </li>
            
            	<s:if test="conAudit.conductedOnsite">
            		<li>
                        <label><s:text name="global.Location" />:</label>
                        <s:property value="conAudit.fullAddress" />
                    </li>
            	</s:if>
            	<s:else>
            		<li>
                        <label><s:text name="global.Location" />:</label>
                        <s:text name="ScheduleAudit.message.Internet" />
                        <a href="http://help.picsorganizer.com/display/contractors/Implementation+Audit" class="help">
                            <s:text name="ScheduleAudit.help.WhatIsThis" />
                        </a>
                    </li>
            		<li>
                        <label><s:text name="ScheduleAudit.label.VideoCamera" />:</label>
                        
            			<s:if test="conAudit.needsCamera">
                            <s:text name="ScheduleAudit.message.MailWebcam" />: <s:property value="conAudit.fullAddress"/>
                        </s:if>
            			<s:else>
                            <s:text name="ScheduleAudit.message.HasWebcam" />
                        </s:else>
            		</li>
            	</s:else>
                
            	<li>
            		<div class="alert">
            			<s:text name="ScheduleAudit.message.RescheduleWarning">
            				<s:param><s:date name="lastCancellationTime" /></s:param>
            				<s:param><s:property value="%{conAudit.contractorAccount.country.getAmount(rescheduling)}" /></s:param>
                            <s:param value="%{conAudit.contractorAccount.country.currency.symbol}" />
            			</s:text>
            		</div>
            	</li>
            </ol>
        </fieldset>
        
        <fieldset class="form">
            <h2 class="formLegend">PICS <s:text name="global.SafetyProfessional" /></h2>
            
            <ol>
        		<li>
                    <label><s:text name="User.name" />:</label>
                    <s:property value="conAudit.auditor.name" />
                </li>
        		<li>
                    <label><s:text name="User.email" />:</label>
                    <s:property value="conAudit.auditor.email" />
                </li>
        		<li>
                    <label><s:text name="User.phone" />:</label>
                    <s:property value="conAudit.auditor.phone" />
                </li>
        		<li>
                    <label><s:text name="User.fax" />:</label>
                    <s:property value="conAudit.auditor.fax" />
                </li>
        		<li>
        			<s:text name="ScheduleAudit.message.QuestionsConcerns">
        				<s:param><s:property value="conAudit.auditor.name" /></s:param>
        			</s:text>
        		</li>
            </ol>
        </fieldset>
        
        <fieldset class="form bottom">
            <h2 class="formLegend"><s:text name="global.ContactPrimary" /></h2>
            
            <ol>
            	<li>
                    <label><s:text name="User.name" />:</label>
                    <s:property value="conAudit.contractorContact" />
                </li>
            	<li>
                    <label><s:text name="User.email" />:</label>
                    <s:property value="conAudit.phone2" />
                </li>
            	<li>
                    <label><s:text name="User.phone" />:</label>
                    <s:property value="conAudit.phone" />
                </li>
            </ol>
        </fieldset>
    </body>
</html>