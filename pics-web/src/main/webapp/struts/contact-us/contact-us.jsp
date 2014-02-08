<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="call_tooltip" value="getText('Contact.Call')" />
<s:set var="chat_tooltip" value="getText('Contact.Chat')" />
<s:set var="message_tooltip" value="getText('Contact.Message')" />
<s:set var="mibew_language_code" value="getText('Mibew.LanguageCode')" />

<%-- Url --%>
<s:url value="http://www.picsauditing.com/company/office-locations/" var="website_contact_page_url" />
<s:url action="ContactUs" method="sendMessage" var="send_message_url" />
<s:url value="https://chat.picsorganizer.com/client.php" var="mibew_href">
    <s:param name="locale">${mibew_language_code}</s:param>
    <s:param name="style">PICS</s:param>
    <s:param name="name">${User.name}</s:param>
    <s:param name="email">${User.email}</s:param>
    <s:param name="url">${requestURL}</s:param>
    <s:param name="referrer">${referer}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="Contact.title" /></s:param>
</s:include>

<div class="row contact-methods">
    <div class="col-md-4 text-center">
        <div class="row">
            <div class="col-xs-3 col-sm-3 col-md-12 method-icon">
                <i class="icon-phone" data-toggle="tooltip" data-placement="top" title="" data-original-title="${call_tooltip}" data-container="body"></i>
            </div>
            <div class="col-xs-9 col-sm-9 col-md-12">
                <div class="phone-number">
                    <p>${contactUsInfo.csrPhoneNumber}</p>
                    <p>x ${contactUsInfo.csrPhoneNumberExtension}</p>
                </div>

                <div class="gray-block">
                    <p><s:text name="Contact.YourCSRIs" /></p>
                    <p>${contactUsInfo.csrName}</p>
                </div>

                <a href="${website_contact_page_url}" target="_blank"><s:text name="Contact.AdditionalNumbers" /></a>
            </div>
        </div>
    </div>
    <div class="col-md-4 text-center">
        <div class="row chat-row">
            <div class="col-xs-3 col-sm-3 col-md-12 method-icon">
                <i class="icon-comments" data-toggle="tooltip" data-placement="top" title="" data-original-title="${chat_tooltip}" data-container="body"></i>
            </div>
            <div class="col-xs-9 col-sm-9 col-md-12">
                <a href="${mibew_href}" class="btn btn-primary btn-lg chat-link" target="_blank" role="button"><s:text name="Contact.ChatNow" /></a>
            </div>
        </div>
    </div>
    <div class="col-md-4 text-center">
        <div class="row">
            <div class="col-xs-3 col-sm-3 col-md-12 method-icon">
                <i class="icon-envelope-alt" data-toggle="tooltip" data-placement="top" title="" data-original-title="${message_tooltip}" data-container="body"></i>
            </div>
            <div class="col-xs-9 col-sm-9 col-md-12 message-container">
                <div class="gray-block"><s:text name="Contact.MessageYourCSR" /></div>
                <form action="${send_message_url}" method="post" class="message-form text-left" role="form">
                    <div class="form-group">
                        <label name="subject" class="control-label"><s:text name="Contact.Subject" /></label>
                        <input name="subject" class="form-control" type="text" tabindex="1">
                    </div>
                    <div class="form-group">
                        <label name="message" class="control-label"><s:text name="Contact.Message" /></label>
                        <div>
                            <textarea name="message" class="form-control" tabindex="2"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="form-actions">
                            <button name="send" type="submit" class="btn btn-default" tabindex="3"><s:text name="Contact.SendMessage" /></button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>