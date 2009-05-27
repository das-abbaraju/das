<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="certificates.size > 0">
	<table class="report" id="choose_certs<s:property value="caoID"/>">
		<thead>
			<tr>
				<th>Uploaded</th>
				<th>Certificate</th>
				<th>Used By</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="certificates">
				<tr>
					<td><s:date name="creationDate" format="M/d/yy" /></td>
					<td>
						<a class="insurance"
							href="#"
							onclick="saveCert(<s:property value="id"/>,<s:property value="caoID"/>); return false;"
							>
							<span></span><s:property value="description" />
						</a>
					</td>
					<td>
						<table class="inner">
							<s:iterator value="caos">
								<s:if test="!permissions.operatorCorporate || !permissions.insuranceOperatorID == permissions.accountID">
								<tr>
									<td style="font-size:10px"><nobr><s:property value="audit.auditType.auditName"/></nobr></td>
									<td style="font-size:10px"><nobr><s:property value="operator.name"/></td>
									<td style="font-size:10px"><nobr><s:date name="audit.expiresDate" format="M/d/yy"/></nobr></td>
								</tr>
								</s:if>
							</s:iterator>
						</table>
					</td>
				</tr>
			</s:iterator>
			<tr>
				<td colspan="3" class="center"><a href="#" class="add" onclick="showCertUpload(<s:property value="contractor.id" />, 0, <s:property value="caoID"/>); return false;" title="Opens in new window (please disable your popup blocker)">Upload New Certificate</a></td>
			</tr>
		</tbody>
	</table>
</s:if>
<s:else>
	<script type="text/javascript">
		showCertUpload(<s:property value="contractor.id"/>,0,<s:property value="caoID"/>);
	</script>
</s:else>