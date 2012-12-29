<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="Report" var="report_url">
    <s:param name="report">1</s:param>
</s:url>

<s:url action="NewYearMailer" method="queueEmails" var="email_queue_url" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">2013 Email Blast</s:param>
    <s:param name="subtitle">Description can go here</s:param>
</s:include>

<div class="report">
    <div class="summary">
        <a href="${report_url}" class="name">
            Contractor List
        </a>

        <p class="description">
            Default Contractor List for PICS Employees
        </p>
    </div>
        
    <span class="number-emails">
        22,000 Entries
    </span>
</div>


<form action="${email_queue_url}" id="email_blast_form" name="email_blast_form" method="post">
    <fieldset>
        <div class="control-group">
            <label class="control-label" for="template">Template</label>
            <div class="controls">
                <select name="template" id="template">
                    <option value="annual_update_reminder">Annual Update Reminder</option>
                </select>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="to">To</label>
            <div class="controls">
                <input type="text" name="to" id="to">
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="reply">Reply To</label>
            <div class="controls">
                <select name="reply" id="reply">
                    <option value="info@picsauditing.com">info@picsauditing.com</option>
                </select>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-success" name="send">Send</button>
        </div>
    </fieldset>
</form>