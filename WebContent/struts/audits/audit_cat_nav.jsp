<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="footerNav">
	<tr>
		<td colspan="2">&nbsp;</td>
		<td class="current"><a
			href="Audit.action?auditID=<s:property value="auditID"/>">Up to
		Category List</a> / <a
			href="AuditCat.action?auditID=<s:property value="auditID"/>&mode=View">View
		All</a></td>
		<td colspan="2"></td>
	</tr>
	<s:if test="catDataID > 0">
		<tr>
			<td class="arrows" rowspan="2">&lt;&lt;</td>
			<td class="previous"><s:if test="previousCategory != null">
			<b>PREVIOUS CATEGORY</b><br />
				<a
					href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="previousCategory.id"/>&mode=<s:property value="mode"/>"><s:property
					value="previousCategory.category.category" /></a>
			</s:if> <s:else>
				<a href="Audit.action?auditID=<s:property value="auditID"/>">START</a>
			</s:else></td>
			<td class="current"><a
				href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="currentCategory.id"/>&mode=<s:property value="mode"/>"><s:property
				value="currentCategory.category.category" /></a></td>
			<td class="next"><s:if test="nextCategory != null">
			<b>NEXT CATEGORY</b><br />
				<a
					href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="nextCategory.id"/>&mode=<s:property value="mode"/>"><s:property
					value="nextCategory.category.category" /></a>
			</s:if> <s:else>
				<a href="Audit.action?auditID=<s:property value="auditID"/>">FINISH</a>
			</s:else></td>
			<td class="arrows">&gt;&gt;</td>
		</tr>
	</s:if>
</table>
