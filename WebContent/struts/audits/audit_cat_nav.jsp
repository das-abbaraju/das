<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="footerNav">
	<tr>
		<td colspan="3">&nbsp;</td>
		<td class="current" height="30"><a class="category_list"
			href="Audit.action?auditID=<s:property value="auditID"/>">Up to
		Category List</a> | <a
			href="Audit.action?auditID=<s:property value="auditID"/>&mode=View">View
		All</a></td>
		<td colspan="3"></td>
	</tr>
	<s:if test="catDataID > 0">
		<tr>
			<td class="arrows"></td>
			<td class="previous"><s:if test="previousCategory != null">
				<a class="prev"
					href="Audit.action?auditID=<s:property value="auditID"/>#categoryID=<s:property value="previousCategory.id"/>&mode=<s:property value="mode"/>"><s:property
					value="previousCategory.category.name" /></a>
			</s:if> <s:else>
				<a class="prev" href="Audit.action?auditID=<s:property value="auditID"/>">START</a>
			</s:else></td>
			<td style="width: 20%;" class="previous"><s:if test="previousAudit">
				<a href="Audit.action?auditID=<s:property value="previousAudit.id"/>&mode=<s:property value="mode"/>"
				><s:property value="previousAudit.auditFor" />&nbsp;<s:property
					value="currentCategory.category.name" /></a>
			</s:if></td>
			<td class="current"><a class="currentCat"
				href="Audit.action?auditID=<s:property value="auditID"/>#categoryID=<s:property value="currentCategory.id"/>&mode=<s:property value="mode"/>"> <s:property
				value="conAudit.auditFor" />&nbsp;<s:property
				value="currentCategory.category.name" /></a></td>

			<td style="width: 20%;" class="next"><s:if test="nextAudit != null">
				<a href="Audit.action?auditID=<s:property value="nextAudit.id"/>&mode=<s:property value="mode"/>"
				> <s:property value="nextAudit.auditFor" />&nbsp;<s:property
					value="currentCategory.category.name" /></a>
			</s:if></td>
			<td class="next"><s:if test="nextCategory != null">
				<a class="nxt"
					href="Audit.action?auditID=<s:property value="auditID"/>#categoryID=<s:property value="nextCategory.id"/>&mode=<s:property value="mode"/>"><s:property
					value="nextCategory.category.name" /></a>
			</s:if> <s:else>
				<a class="nxt" href="Audit.action?auditID=<s:property value="auditID"/>">FINISH</a>
			</s:else></td>
			<td class="arrows"></td>
		</tr>
	</s:if>
</table>
