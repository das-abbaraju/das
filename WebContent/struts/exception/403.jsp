<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<meta name="decorator" content="simple-layout"/>

<div id="exception" class="notice">
    <img src="/v7/img/logo/logo-small.png" alt="PICS" />
    
    <h1>Unauthorized</h1>
    
    <p>
        You do not currently have authorization to view this page.
    </p>
    
    <p>
        If you feel that you've reached this in error, please contact the <a href="//www.picsauditing.com/contact/">PICS Customer Service Department</a> or other PICS representative.
    </p>
    
    <%-- Temporary removal until referrer solution architected
    
    <s:if test="exception.referrer != 'empty'">
        <div class="actions clearfix">
            <a href="${exception.referrer}" class="btn pull-right">Back</a>
        </div>
    </s:if>
     --%>
</div>