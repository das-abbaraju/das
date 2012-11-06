<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<h1>
	<s:property value="contractor.name" />

	<span class="sub">
		<s:property value="subHeading" escape="false" />
	</span>
</h1>
<div class="alert">
	<s:text name="ContractorView.ContractorNeedsToAddOperators" />
</div>

<%-- Contractor Info --%>
<div class="panel_placeholder">
	<div class="panel">
		<div class="panel_header">
			<s:text name="ContractorView.ContractorInfo" />
		</div>
		<div class="panel_content">
			<h4>
				<s:property value="contractor.name" />
				
				<s:if test="contractor.dbaName.length() > 0">
					<br/>
					<s:text name="ContractorAccount.dbaName.short" />
					<s:property value="contractor.dbaName" />
				</s:if>
			</h4>
			
			<p>
				<s:text name="ContractorAccount.id" />:
				<strong><s:property value="contractor.id" /></strong>
			</p>
			
			<pics:permission perm="PicsScore">
				<p>
					<s:text name="ContractorAccount.score" />:
					<strong><s:property value="contractor.score" /></strong>
				</p>
			</pics:permission>
			
			<p>
				<s:text name="ContractorView.MemberSince" />:
				<strong><s:date name="contractor.membershipDate" /></strong>&nbsp;&nbsp;
				<a class="pdf" href="ContractorCertificate.action?id=<s:property value="contractor.id" />"><s:text name="ContractorDashboard.DownloadCertificate" /></a>
			</p>
			
			<p>
				<s:text name="global.CSR" />:
				<strong>
					<s:property value="contractor.auditor.name" />
					/
					<s:property value="contractor.auditor.phone" />
                                   <span id="CSRNote">(<s:text name="ContractorView.ContractorDashboard.CSRCallNote" />)</span>
					/
				</strong>
				<s:text name="ProfileEdit.u.fax" />:
				<s:property value="contractor.auditor.fax" />
				/ 
				<a href="mailto:<s:property value="contractor.auditor.email"/>" class="email">
					<s:property value="contractor.auditor.email"/>
				</a>
			</p>
			
			<p>
				<s:text name="global.SafetyRisk" />:
				<strong>
					<s:if test="contractor.safetyRisk != null">
						<s:text name="%{contractor.safetyRisk.i18nKey}" />
					</s:if>
					<s:else>
						<s:text name="ContractorAccount.safetyRisk.missing" />
					</s:else>
				</strong>
			</p>
			
			<s:if test="contractor.materialSupplier && contractor.productRisk != null">
				<p>
					<s:text name="global.ProductRisk" />:
					<strong><s:text name="%{contractor.productRisk.i18nKey}" /></strong>
				</p>
			</s:if>
			
			<s:if test="contractor.transportationServices && contractor.transportationRisk != null">
				<p>
					<s:text name="global.TransportationRisk" />:
					<strong><s:text name="%{contractor.transportationRisk.i18nKey}" /></strong>
				</p>
			</s:if>
			
			<p>
				<s:text name="ContractorAccount.type" />:
				<s:property value="commaSeparatedContractorTypes" /> 
			</p>
			
			<s:if test="(permissions.admin || permissions.operatorCorporate) && contractor.generalContractorOperatorAccounts.size > 0">
				<p>
					<s:text name="ContractorView.SubcontractingUnder" />:
					<s:iterator value="contractor.generalContractorOperatorAccounts" status="gc_index">
						<strong>
							<s:property value="name" /><s:if test="!#gc_index.last">, </s:if>
						</strong>
					</s:iterator>
				</p>
			</s:if>
			
			<s:if test="hasOperatorTags">
				<s:if test= "contractor.operatorTags.size() > 0 || operatorTags.size() > 0">
					<div>
						<span><s:text name="OperatorTags.title" />: </span>
						<div id="conoperator_tags">
							<s:include value="contractorOperator_tags.jsp" />
						</div>
					</div>
				</s:if>
			</s:if>
			
			<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
				<div>
					<span id="contractor_operator_numbers_label">
						<s:text name="ContractorOperatorNumber" />:
					</span>
					<div id="contractor_operator_numbers">
						<s:include value="/struts/contractors/third-party-identifier/_identifier-table.jsp" />
					</div>
				</div>
			</s:if>
			
			<div class="clear"></div>
		</div>
	</div>
</div>

<%-- Contact Info --%>
<div class="panel_placeholder">
	<div class="panel">
		<div class="panel_header">
			<s:text name="ContractorView.ContactInfo" />
		</div>
		<div class="panel_content">
			<p>
				<s:text name="global.Address" />:
				[<a
					href="http://www.mapquest.com/maps/map.adp?country=<s:property value="contractor.country.isoCode" />&city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
					target="_blank">
						<s:text name="ContractorView.ShowMap" />
				</a>]
				<br/>
				<span class="street-address">
					<s:property value="contractor.address" />
				</span>
				<br />
				<span class="locality">
					<s:property value="contractor.city" />
				</span>,
				<span class="region">
					<s:property value="contractor.state" />
				</span>
				<span class="postal-code">
					<s:property value="contractor.zip" />
				</span>
				<br />
				<span class="region">
					<s:property value="contractor.country.name" />
				</span>
			</p>
			
			<div class="telecommunications">
				<p class="tel">
					<s:text name="ContractorView.MainPhone" />:
					<span class="value"><s:property value="contractor.phone" /></span>
				</p>
				
				<s:if test="!isStringEmpty(contractor.fax)">
					<p class="tel">
						<s:text name="ContractorEdit.PrimaryAddress.CompanyFaxMain" />:
						<span class="value"><s:property value="contractor.fax" /></span>
					</p>
				</s:if>
				
				<s:if test="contractor.webUrl.length() > 0">
					<p class="url">
						<s:text name="ContractorAccount.webUrl" />:
						<strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong>
					</p>
				</s:if>
				
				<s:iterator value="contractor.getUsersByRole('ContractorAdmin')">
					<p class="contact">
						<s:if test="contractor.primaryContact.id == id">
							<s:text name="global.ContactPrimary" />
						</s:if>
						<s:else>
							<s:text name="global.Contact" />
						</s:else>:
						<span class="value"><s:property value="name" /></span>
					</p>
					<p class="tel">
						&nbsp;&nbsp;
						<s:text name="User.email" />:
						<a href="mailto:<s:property value="email" />" class="email"><s:property value="email" /></a>
						
						<s:if test="phone.length() > 0">
							/ <s:text name="User.phone" />: <s:property value="phone" />
						</s:if>
						
						<s:if test="fax.length() > 0">
							/ <s:text name="User.fax" />: <s:property value="fax" />
						</s:if>
					</p>
				</s:iterator>
			</div>
			
			<div class="clear"></div>
		</div>
	</div>
</div>

<%-- Description --%>
<div class="panel_placeholder">
	<div class="panel">
		<div class="panel_header">
			<s:text name="global.Description" />
		</div>
		<div class="panel_content">
			<s:if test="showLogo">
				<img class="contractor_logo" src="ContractorLogo.action?id=<s:property value="id"/>"/>
			</s:if>
			
			<span id="description"><s:property value="contractor.descriptionHTML" /></span>
			
			<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false">
				<p class="web">
					<strong>
						<a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK"><s:text name="ContractorEdit.CompanyIdentification.CompanyBrochure" /></a>
					</strong>
				</p>
			</s:if>
			
			<s:if test="contractor.trades.size() > 0">
				<s:include value="../trades/contractor_trade_cloud.jsp"/>
			</s:if>
			
			<div class="clear"></div>
		</div>
	</div>
				
						</div>