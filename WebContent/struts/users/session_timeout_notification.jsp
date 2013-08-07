<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="session-timeout-notification alert navbar-fixed-top">
    <button type="button" class="close" data-dismiss="alert">×</button>
    <h4><s:text name="User.session.title" /></h4>
    <p>
    	<s:text name="User.session.message" />
    	<strong class="time">0</strong>
    	<s:text name ="User.session.message2" />
    	<a href="#"><s:text name="User.session.link" /></a>
    </p>
</div>