<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
<meta name="header_gif" content="header_contractorDetails.gif" />
</head>
<body>

<br />
<br />
<table width="657" cellpadding="15" bgcolor="#F8F8F8">
<tr class="blueMain" align="left" valign="top">
<td>
<h3 class="blueHeader"><s:property value="contractor.name" /></h3>
<s:property value="contractor.address" />, <s:property
	value="contractor.city" />, <s:property value="contractor.state" /> <s:property
	value="contractor.zip" /> [<a
	href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
	target="_blank" class="redMain">map</a>]<br />
<span class="redMain">Contact:</span> <s:property
	value="contractor.contact" /><br>
<span class="redMain">Phone:</span> <s:property value="contractor.phone" /><br>
<span class="redMain">Phone 2:</span> <s:property
	value="contractor.phone2" /><br>
<span class="redMain">Fax:</span> <s:property value="contractor.fax" /><br>
<span class="redMain">Email:</span> <a
	href="mailto:<s:property value="contractor.email" />"><s:property
	value="contractor.email" /></a><br>
<span class="redMain">Website:</span> <a
	href="http://<s:property value="contractor.web_URL" />" target="_blank"><s:property
	value="contractor.web_URL" /></a><br>
<a target=_blank href="servlet/showpdf?id=<s:property value="id" />&file=brochure">Company
Brochure</a></td>
<td><span class="redMain">PICS Contractor ID:</span> <s:property value="contractor.luhnId" /><br />
<span class="redMain">PICS Status:</span> <br>
<span class="redMain">Risk Level:</span> <s:property value="contractor.contractor.riskLevel" /><br>

<span class="redMain">Facilities:</span><br />
<s:iterator value="contractor.operators">
	<s:property value="operatorAccount.name" /><br />
</s:iterator>
...<a href="con_selectFacilities.jsp?id=<s:property value="id" />">see facilities</a><br />

<s:iterator value="contractor.audits" id="audit">
	<span class="redMain"><s:property value="auditType.auditName" />:</span>
	<a href="<s:property value="id" />"><s:property value="auditStatus" /></a><br />
</s:iterator>
<a href="ConAuditList.action?id=<s:property value="id" />" class="blueMain">Audits &amp; Evaluations</a>

</td>
</tr>
</table>
<div class="blueMain" style="width: 627px; padding: 15px; background-color: #FFFFFF; text-align: left;">

<s:property value="contractor.contractor.descriptionHTML" escape="false" /></div>
</body>
</html>
