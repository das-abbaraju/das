<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<title><s:text name="global.Login" /></title>
<meta name="help" content="Logging_In">

<section class="login-form">
    <s:include value="login/_login-form.jsp"/>
</section>

<a id="registration_link" href="Registration.action">Are you a contractor not registered with PICS yet?</a>

<div id="newsfeed_wrapper">
    <div id="newsfeed"></div>
</div>