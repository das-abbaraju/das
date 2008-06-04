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

<table class="footerNav">
<tr>
<td class="arrows">&lt;&lt;</td>
<td class="previous">
<s:if test="previousCategory != null">
	<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="previousCategory.id"/>&mode=<s:property value="mode"/>"><s:property value="previousCategory.category.category"/></a>
</s:if>
<s:else><a href="Audit.action?auditID=<s:property value="auditID"/>">START</a></s:else>
</td>
<td class="current">
	<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="currentCategory.id"/>&mode=<s:property value="mode"/>"><s:property value="currentCategory.category.category"/></a>
</td>
<td class="next">
<s:if test="nextCategory != null">
	<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="nextCategory.id"/>&mode=<s:property value="mode"/>"><s:property value="nextCategory.category.category"/></a>
</s:if>
<s:else><a href="Audit.action?auditID=<s:property value="auditID"/>">FINISH</a></s:else>
</td>
<td class="arrows">&gt;&gt;</td>
</tr>
</table>

<s:iterator value="categories">
	<s:if test="catDataID == id || catDataID == 0">
		<h2>Category <s:property value="category.number"/> - <s:property value="category.category"/></h2>
		<s:if test="catDataID > 0">
			<div>
			<s:if test="mode != 'View'">
				<a href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>">Switch to View Mode</a>
			</s:if>
			<s:if test="mode != 'Edit' && canEdit">
				<a href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Edit">Switch to Edit Mode</a>
			</s:if>
			<s:if test="mode != 'Verify' && canVerify">
				<a href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Verify">Switch to Verify Mode</a>
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
				<s:if test="mode == 'View'">
					<s:if test="viewBlanks || answer.answer.length() > 0">
						<s:include value="audit_cat_view.jsp"></s:include>
					</s:if>
				</s:if>
				<s:if test="mode == 'Edit'">
					<s:include value="audit_cat_edit.jsp"></s:include>
				</s:if>
				<s:if test="mode == 'Verify'">
					<s:if test="answer.answer.length() > 0">
						<s:include value="audit_cat_verify.jsp"></s:include>
					</s:if>
				</s:if>
			</s:iterator>
		</s:iterator>
		</table>
	</s:if>
</s:iterator>

<table class="footerNav">
<tr>
<td class="arrows">&lt;&lt;</td>
<td class="previous">
<s:if test="previousCategory != null">
	<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="previousCategory.id"/>&mode=<s:property value="mode"/>"><s:property value="previousCategory.category.category"/></a>
</s:if>
<s:else><a href="Audit.action?auditID=<s:property value="auditID"/>">START</a></s:else>
</td>
<td class="current">
	<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="currentCategory.id"/>&mode=<s:property value="mode"/>"><s:property value="currentCategory.category.category"/></a>
</td>
<td class="next">
<s:if test="nextCategory != null">
	<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="nextCategory.id"/>&mode=<s:property value="mode"/>"><s:property value="nextCategory.category.category"/></a>
</s:if>
<s:else><a href="Audit.action?auditID=<s:property value="auditID"/>">FINISH</a></s:else>
</td>
<td class="arrows">&gt;&gt;</td>
</tr>
</table>

</body>
</html>
