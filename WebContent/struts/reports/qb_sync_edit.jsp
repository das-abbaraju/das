<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
<head>
<title>QuickBooks Sync Edit</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>QuickBooks Sync Edit</h1>

<s:include value="../actionMessages.jsp" />

<s:form id="save">
	<table width="100%">
		<tr>
			<td style="vertical-align: top; width: 100%; padding-left: 10px;">
				<fieldset class="form">
					<ol>
						<li>
							<label>IDs:</label> <s:textfield name="ids" />
	                        <pics:fieldhelp title="IDs">
	                            <p>
	                                Enter the ID(s) here.
	                            </p>
	                        </pics:fieldhelp>
						</li>
						<li>
							<label>QB Entry Type</label> 
							<s:radio list="#{'Account':'Account','Invoice':'Invoice'}" name="type" value="type" theme="pics" cssClass="inline" />
	                        <pics:fieldhelp title="Account or Invoice">
	                            <p>
	                                Is the ID(s) for contractor accounts or invoices?
	                            </p>
	                        </pics:fieldhelp>
						</li>
						<li>
							<label>Syncronize with QB?</label> 
							<s:radio list="#{true:'Yes',false:'No'}" name="needSync" value="needSync" theme="pics" cssClass="inline" />
	                        <pics:fieldhelp title="Syncronize?">
	                            <p>
	                                If Yes, this will be available to be synced by the web connector. If No, this will be ignored. 
	                            </p>
	                        </pics:fieldhelp>
						</li>
						<li>
							<label>Clear the List ID?</label> 
							<s:radio list="#{true:'Yes',false:'No'}" name="clearListID" value="clearListID" theme="pics" cssClass="inline" />
	                        <pics:fieldhelp title="Clear the List ID">
	                            <p>
	                                If Yes, this will wipe out the QB List ID. Otherwise it does nothing.
	                            </p>
	                        </pics:fieldhelp>
						</li>
					</ol>
			</td>
		</tr>
		<tr>
			<button name="button" class="save" value="save">Save</button>
		</tr>
	</table>
</s:form>

</body>
</html>
