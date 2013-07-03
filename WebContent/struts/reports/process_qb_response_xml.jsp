<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
<head>
    <title>QuickBooks Sync Edit</title>
    <link rel="stylesheet" type="text/css" media="screen"
          href="css/reports.css?v=<s:property value="version"/>"/>
    <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
    <link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>"/>
</head>
<body>
<h1>QuickBooks Sync Edit</h1>

<s:include value="../actionMessages.jsp"/>

<s:form id="save">

    <fieldset class="form">

        <label>Paste QBResponse.xml here:</label> <s:textarea name="qbresponsexml"/>
        <pics:fieldhelp title="qbresponsexml">
            <p>
                Paste QBResponse.xml here
            </p>
        </pics:fieldhelp>

        <button name="button" class="save" value="save">Save</button>
    </fieldset>
</s:form>

</body>
</html>
