<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="certificates.size > 0">
	<table class="report" id="choose_certs<s:property value="caoID"/>">
		<thead>
			<tr>
				<th></th>
				<th>Certificate</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="certificates">
				<tr>
					<td>
						<a href="#" class="add saveCertificate" rel="<s:property value="id"/>">Attach</a>
					</td>
					<td>
						<a href="#" class="insurance viewCertificate" rel="<s:property value="id"/>"><s:date name="creationDate" format="M/d/yy" /> - <s:property value="description" /></a>
						<br/>
						<table class="inner">
							<s:iterator value="caos">
								<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
								<tr>
									<td style="font-size:10px" class="nobr"><s:property value="audit.auditType.auditName"/></td>
									<td style="font-size:10px" class="nobr"><s:property value="operator.name"/></td>
									<td style="font-size:10px" class="nobr"><s:date name="audit.expiresDate" format="M/d/yy"/></td>
								</tr>
								</s:if>
							</s:iterator>
						</table>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	There are no certificates attached to this contractor.
</s:else>