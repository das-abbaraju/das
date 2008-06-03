<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:iterator value="categories">
	<s:if test="catDataID == id || catDataID == 0">
		<h2>Category <s:property value="category.number"/> - <s:property value="category.category"/></h2>
		<s:if test="catDataID > 0">
			<div>View:
			<s:if test="canEdit">
				<a href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>">Switch to Edit Mode</a>
			</s:if>
			<s:if test="canVerify">
				<a href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>">Switch to Verify Mode</a>
			</s:if>
			</div>
		</s:if>
		<table class="audit">
		<s:iterator value="category.subCategories">
			<tr class="subCategory">
				<td colspan="3">Sub Category <s:property value="category.number"/>.<s:property value="number"/> - 
				<s:property value="subCategory"/></td>
			</tr>
			<s:iterator value="questions">
				<s:if test="title.length() > 0">
					<tr class="group1">
						<td class="groupTitle" colspan="3"><s:property value="title"/></td>
					</tr>
				</s:if>
				<tr class="group1">
					<td class="right"><s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;</td>
					<td class="question"><s:property value="question"/>
					<br>&nbsp;&nbsp;&nbsp;
					<span class="answer"><s:property value="answer"/></span>
					<s:if test="isCorrect.equals('Yes')">
						<span class="verified">Verified on <s:date value="dateVerified" /></span>
					</s:if>
					<s:if test="isCorrect.equals('Yes')">
						<span class="unverified">Inaccurate Data</span>
					</s:if>
					
					<td></td>
				</tr>
			</s:iterator>
		</s:iterator>
		</table>
	</s:if>
</s:iterator>

</body>
</html>
