<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="RegistrationAddClientSite" var="add_client_sites_url" />
<s:url action="Registration" method="basicEdit" var="basic_edit_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Please Confirm Your Address</s:param>
</s:include>

<title>
    <s:text name="ContractorRegistration.title" />
</title>

<p>
    17701 Cowan STE 100<br>
    Irvine, CA 92614-6061<br>
    United States
</p>

<div class="actions">
    <a href="${add_client_sites_url}" class="btn btn-success" tabindex="1">Confirm</a>
    <a href="${basic_edit_url}" class="btn btn-default" tabindex="2">Edit</a>
</div>