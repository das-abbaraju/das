<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<h1>
	${contractor.name}

	<span class="sub">
		${subHeading}
	</span>
</h1>

<s:url action="NewContractorSearch" method="add" var="new_contractor_search_add">
	<s:param name="contractor">${contractor.id}</s:param>
</s:url>
<s:text name="ContractorView.AddContractorToDatabase">
	<s:param>
		${new_contractor_search_add}
	</s:param>
</s:text>
<div class="clear"></div>

<table id="contractor_dashboard">
	<tr>
		<td style="vertical-align:top; width: 48%">
			<%-- Description--%>
			<div class="panel_placeholder">
				<div class="panel">
					<div class="panel_header">
						<s:text name="global.Description" />
					</div>
					<div class="panel_content">
						<s:if test="showLogo">
							<img class="contractor_logo" src="ContractorLogo.action?id=${id}"/>
						</s:if>
						
						<span id="description">${contractor.descriptionHTML}</span>
						
						<s:if test="!isStringEmpty(contractor.brochureFile)">
							<p class="web">
								<strong>
									<s:url action="DownloadContractorFile" var="download_contractor_file">
										<s:param name="id">
											${id}
										</s:param>
									</s:url>
									<a href="${download_contractor_file}" target="_BLANK">
										<s:text name="ContractorEdit.CompanyIdentification.CompanyBrochure" />
									</a>
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
		</td>
		<td width="15px"></td>
		<td style="vertical-align:top; width: 48%">
			<%-- Contractor Info --%>
			<div class="panel_placeholder">
				<div class="panel">
					<div class="panel_header">
						<s:text name="ContractorView.ContractorInfo" />
					</div>
					<div class="panel_content">
						<h4>
							${contractor.name}
							
							<s:if test="!isStringEmpty(contractor.dbaName)">
								<br/>
								<s:text name="ContractorAccount.dbaName.short" />
								${contractor.dbaName}
							</s:if>
						</h4>
						
						<p>
							<s:text name="ContractorAccount.id" />:
							<strong>${contractor.id}</strong>
						</p>
						
						<pics:permission perm="PicsScore">
							<p>
								<s:text name="ContractorAccount.score" />:
								<strong>${contractor.score}</strong>
							</p>
						</pics:permission>
						
						<p>
							<s:text name="ContractorView.MemberSince" />:
							<strong><s:date name="contractor.membershipDate" /></strong>&nbsp;&nbsp;
						</p>
			
						<p>
							<s:text name="ContractorAccount.type" />:
							${commaSeparatedContractorTypes}
						</p>
						
						<s:if test="(permissions.admin || permissions.operatorCorporate) && contractor.generalContractorOperatorAccounts.size > 0">
							<p>
								<s:text name="ContractorView.SubcontractingUnder" />:
								<s:iterator value="contractor.generalContractorOperatorAccounts" status="gc_index" var="contractor_gc_operator">
									<strong>
										${contractor_gc_operator.name}<s:if test="!#gc_index.last">, </s:if>
									</strong>
								</s:iterator>
							</p>
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
							<s:url value="http://www.mapquest.com/maps/map.adp" var="mapquest_address_link">
								<s:param name="country">
									${contractor.country.isoCode}
								</s:param>
								<s:param name="city">
									${contractor.city}
								</s:param>
								<s:param name="state">
									${contractor.countrySubdivision.isoCode}
								</s:param>
								<s:param name="address">
									${contractor.address}
								</s:param>
								<s:param name="zip">
									${contractor.zip}
								</s:param>
								<s:param name="zoom">
									5
								</s:param>
							</s:url>
							<s:text name="global.Address" />:
							[<a href="${mapquest_address_link}" target="_blank">
								<s:text name="ContractorView.ShowMap" />
							</a>]
							<br/>
							<span class="street-address">
								${contractor.address}
							</span>
							<br />
							<span class="locality">
								${contractor.city}
							</span>,
							<span class="region">
								${contractor.countrySubdivision.isoCode}
							</span>
							<span class="postal-code">
								${contractor.zip}
							</span>
							<br />
							<span class="region">
								${contractor.country.name}
							</span>
						</p>
						
						<div class="telecommunications">
							<s:iterator value="contractor.getUsersByRole('ContractorAdmin')" var="contractor_user">
								<p class="contact">
									<s:if test="contractor.primaryContact.id == id">
										<s:text name="global.ContactPrimary" />
									</s:if>
									<s:else>
										<s:text name="global.Contact" />
									</s:else>:
									<span class="value">${contractor_user.name}</span>
								</p>
							</s:iterator>
						</div>
						
						<div class="clear"></div>
					</div>
				</div>
			</div>
		</td>
	</tr>
</table>