<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>

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
					<td><a class="add"
							href="#"
							onclick="saveCertQ(<s:property value="id"/>,<s:property value="caoID"/>,'',0,<s:property value="catDataID" />); return false;">Attach</a>
					</td>
					<td>
						<a class="insurance"
							href="#"
							onclick="showCertUpload(<s:property value="contractor.id"/>,<s:property value="id"/>,0);return false;"
							>
							<span></span><s:date name="creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /> - <s:property value="description" />
						</a>
						<br/>
						<table class="inner">
							<s:iterator value="caos">
								<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
								<tr>
									<td style="font-size:10px"><nobr><s:property value="audit.auditType.name"/></nobr></td>
									<td style="font-size:10px"><nobr><s:property value="operator.name"/></nobr></td>
									<td style="font-size:10px"><nobr><s:date name="audit.expiresDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></nobr></td>
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