<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="ManageReports" method="favorites" var="recommend_favorites_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Get Started</s:param>
</s:include>

<div class="row">
	<div class="col-md-6 col-md-offset-3">
		<div class="alert alert-info clearfix">
			<h4><s:text name="ManageReports.getStarted.welcome.header" /></h4>

			<p><s:text name="ManageReports.getStarted.welcome.instructions" /></p>
		</div>
	</div>
</div>
<div class="row">
	<div class="col-md-6 col-md-offset-3">
		<form name="recommend_favorites" action="${recommend_favorites_url}" class="form-horizontal" role="form">

			<div class="form-group">
				<label class="col-md-3 control-label"><s:text name="ManageReports.getStarted.userTypeLabel" /></label>
	            <div class="col-md-6">
	                <select id="user_type" name="userType" class="form-control select2Min">
	                	<option value=""><s:text name="ManageReports.getStarted.userTypePlaceholder" /></option>
	                	<s:iterator value="userTypes" var="user_type">
	                		<option value="${user_type}">${user_type.name}</option>`
                		</s:iterator>
	                </select>
	            </div>
			</div>
			<div class="form-group">
		        <div class="col-md-6 col-md-offset-3 form-actions">
		            <button id="go_to_favorites" name="goToFavorites" type="submit" class="btn btn-primary" disabled="disabled"><s:text name="ManageReports.getStarted.buttonLabel" /></button>
		        </div>
		    </div>
		</form>
	</div>
</div>
