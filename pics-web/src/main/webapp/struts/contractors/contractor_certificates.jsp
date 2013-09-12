<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="certificates.size > 0">
	<table class="report" id="choose_certs<s:property value="caoID"/>">
		<thead>
			<tr>
				<th></th>
				<th><s:text name="Audit.certificates.Certificate" /></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="certificates">
				<s:if test="!expired">
				<tr>
					<td>
						<a href="#" class="add saveCertificate" rel="<s:property value="id"/>"><s:text name="Audit.certificates.Attach" /></a>
					</td>
					<td>
						<a href="#" class="insurance viewCertificate" rel="<s:property value="id"/>"><s:date name="creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /> - <s:property value="description" /></a>
						<br/>
						<table class="inner">
							<s:iterator value="caos">
								<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
								<tr>
									<td style="font-size:10px" class="nobr"><s:property value="audit.auditType.name"/></td>
									<td style="font-size:10px" class="nobr"><s:property value="operator.name"/></td>
									<td style="font-size:10px" class="nobr"><s:date name="audit.expiresDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
								</tr>
								</s:if>
							</s:iterator>
						</table>
					</td>
				</tr>
				</s:if>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<s:text name="Audit.certificates.NoCerts" />
</s:else>