<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
    <title><s:property value="report.summary"/></title>
    <!-- <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme.css"> -->
    <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-custom.css?v=${version}">
</head>
<body>
	<div id="reportTable">
		<table>
			<tr>
				<Th>Report Name</Th>
				<th></th>
			</tr>
		    <s:iterator value="reportsByUser">
			    <tr>
			    	<td>
			    		<s:property value="name" /> created by <s:property value="user.createdBy.name" />
		    		</td>
					<td>
						<a href="ManageReports!deleteReport.action?reportID=<s:property value="id" />">remove</a>
					</td>
				</tr>
			</s:iterator>
		</table>
	</div>
</body>
