<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:text name="RequestNewContractor.title" />
	</title>
	
	<s:include value="../../jquery.jsp" />
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=${version}" />
	
	<style type="text/css">
		#email_preview_modal th
		{
			font-weight: bold;
		}
		
		#email_preview_modal th,
		#email_preview_modal td
		{
			padding: 5px;
		}
		
		#email_preview_modal #email_body
		{
			background-color: #EEE;
			border: 1px solid #DDD;
		}
		
		p.matched-on
		{
			color: #A84D10 !important;
		}
	</style>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<h1>
			<s:text name="RequestNewContractor.title" />
		</h1>
		
		<s:if test="hasFieldErrors()">
			<div class="alert">
				<s:text name="RequestNewContractor.ErrorSaving" />
			</div>
		</s:if>
		
		<s:include value="../../actionMessages.jsp" />
		
		<pics:permission perm="RequestNewContractor">
			<s:url action="ReportRegistrationRequests" var="request_report" />
			<a href="${request_report}">&lt;&lt;
				<s:text name="RequestNewContractor.link.BackToRequests" />
			</a>
		</pics:permission>
		
		<s:if test="!contractor.status.requested">
			<div class="info">
				<s:if test="contractor.status.active">
					<s:text name="RequestNewContractor.message.Registered">
						<s:param>
							${contractor.name}
						</s:param>
						<s:param>
							<s:date name="contractor.membershipDate" />
						</s:param>
					</s:text>
				</s:if>
				<s:else>
					<s:text name="RequestNewContractor.message.RequestClosed" />
				</s:else>
			</div>
		</s:if>
		
		<s:form action="RequestNewContractorAccount" validate="true" id="request_form">
			<s:hidden name="contractor" />
			<s:hidden name="id" />
			<s:hidden name="contractor.status" />
			<s:hidden name="requestRelationship" />
			
			<s:if test="permissions.operator">
				<s:hidden name="requestRelationship.operatorAccount" value="%{permissions.accountId}" />
			</s:if>
			
			<s:hidden name="primaryContact" />
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.CompanyInformation" />
				</h2>
				
				<s:set name="isEditable" value="%{permissions.picsEmployee || contractor.id == 0 || contractor.requestedBy.id == permissions.accountId}" />

				<ol>
					<s:if test="#isEditable">
						<li>
							<s:textfield
								cssClass="popup-on-match"
								id="company_name"
								name="contractor.name"
								size="35"
								theme="formhelp"
								required="true"
								data-type="C"
								data-class="contractor-name"
							/>
						</li>
						<li>
							<s:textfield
								cssClass="check-matches"
								id="contact_name"
								name="primaryContact.name"
								theme="formhelp"
								required="true"
								data-type="U"
								data-class="contact-name"
							/>
						</li>
						<li class="match-found contact-name"></li>
						<li>
							<s:textfield
								cssClass="check-matches"
								name="primaryContact.phone"
								required="true"
								size="20"
								theme="formhelp"
								data-type="U"
								data-class="contact-phone"
							/>
						</li>
						<li class="match-found contact-phone"></li>
						<li>
							<s:textfield
								cssClass="check-matches"
								id="email"
								name="primaryContact.email"
								required="true"
								size="30"
								theme="formhelp"
								data-type="U"
								data-class="contact-email"
							/>
						</li>
                        <li class="match-found contact-email"></li>
                        <li>
                            <s:select
                                    list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
                                    listValue="@org.apache.commons.lang3.StringUtils@capitalize(getDisplayName(language))"
                                    listKey="%{language + (!isStringEmpty(country) ? '_' + country : '')}"
                                    name="primaryContact.locale"
                                    theme="formhelp"
                            />
                        </li>
					</s:if>
					<s:else>
						<li>
							<label>
								<s:text name="ContractorAccount.name" />
							</label>
							${contractor.name}
						</li>
						<li>
							<label>
								<s:text name="User.name" />
							</label>
							${primaryContact.name}
						</li>
						<li>
							<label>
								<s:text name="User.phone" />
							</label>
							${primaryContact.phone}
						</li>
						<li>
							<label>
								<s:text name="User.email" />
							</label>
							${primaryContact.email}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.taxId" />
							</label>
							${contractor.taxId}
						</li>
					</s:else>
				</ol>
				
				<div class="match-list hide contact-phone"></div>
				<div class="match-list hide contractor-name"></div>
				<div class="match-list hide contact-name"></div>
				<div class="match-list hide contact-email"></div>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="global.PrimaryAddress" />
				</h2>
				
				<ol>
					<s:if test="#isEditable">
						<li>
							<s:select
								id="country"
								list="countryList"
								listKey="isoCode" 
								listValue="name"
								name="contractor.country"
								required="true"
								theme="formhelp"
								value="%{contractor.country.isoCode}"
							/>
						</li>
						<li id="country_subdivision">
							<s:if test="contractor.country != null">
								<s:select
									id="countrySubdivision_sel"
									label="%{contractor.country.isoCode == 'CA' ? 'global.Province' : 'CountrySubdivision'}"
									list="getCountrySubdivisionList(contractor.country.isoCode)"
									listKey="isoCode"
									listValue="simpleName"
									name="contractor.countrySubdivision"
									required="true"
									theme="formhelp"
									value="%{contractor.countrySubdivision.isoCode}"
								/>
							</s:if>
						</li>
						<li>
							<s:textfield
								name="contractor.city"
								size="20"
								id="city"
								theme="formhelp"
							/>
						</li>
						<li class="address-zip">
							<s:textfield
								name="contractor.address"
								size="35"
								id="address"
								theme="formhelp"
							/>
						</li>
						<li class="address-zip">
							<s:textfield
								name="contractor.zip"
								size="7"
								id="zip"
								theme="formhelp"
							/>
						</li>
					</s:if>
					<s:else>
						<li>
							<label>
								<s:text name="ContractorAccount.country" />
							</label>
							${contractor.country.name}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.countrySubdivision" />
							</label>
							${contractor.countrySubdivision.simpleName}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.city" />
							</label>
							${contractor.city}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.address" />
							</label>
							${contractor.address}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.zip" />
							</label>
							${contractor.zip}
						</li>
					</s:else>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.RequestSummary" />
				</h2>
				
				
				<ol>
					<s:if test="contractor.id > 0 && requestRelationship.operatorAccount.id > 0 && !permissions.operator">
						<s:url action="RequestNewContractorAccount" var="request_add_operator">
							<s:param name="contractor">
								${contractor.id}
							</s:param>
						</s:url>
						<li>
							<a href="${request_add_operator}" class="add">
								<s:text name="ContractorFacilities.AddOperator" />
							</a>
						</li>
					</s:if>
					<s:if test="visibleRelationships.size > 1 || (visibleRelationships.size > 0 && requestRelationship.operatorAccount.id == 0)">
						<li>
							<table class="report">
								<thead>
									<tr>
										<th></th>
										<th>
											<s:text name="ContractorAccount.requestedBy" />
										</th>
										<th>
											<s:text name="ContractorOperator.requestedBy" />
										</th>
										<th>
											<s:text name="ContractorOperator.deadline" />
										</th>
										<th>
											<s:text name="ContractorOperator.reasonForRegistration" />
										</th>
										<th>
											<s:text name="button.Edit" />
										</th>
										<th>
											<s:text name="button.Remove" />
										</th>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="contractor.operators" var="relationship" status="position">
										<s:if test="relationship.operatorAccount.id == requestRelationship.operatorAccount.id">
											<s:set name="selected_row" value="'highlight'" />
										</s:if>
										<s:else>
											<s:set name="selected_row" value="" />
										</s:else>
										<tr class="${selected_row}">
											<td>
												${position.count}
											</td>
											<td>
												<s:url action="FacilitiesEdit" var="operator_edit">
													<s:param name="id">
														${relationship.operatorAccount.id}
													</s:param>
												</s:url>
												<a href="${operator_edit}">${relationship.operatorAccount.name}</a>
											</td>
											<td>
												${relationship.requestedByName}
											</td>
											<td>
												<s:date name="#relationship.deadline" format="%{getText('date.short')}" />
											</td>
											<td>
												${relationship.reasonForRegistration}
											</td>
											<td class="center">
												<s:url action="RequestNewContractorAccount" var="edit_operator_request">
													<s:param name="contractor">
														${contractor.id}
													</s:param>
													<s:param name="requestRelationship.operatorAccount">
														${relationship.operatorAccount.id}
													</s:param>
												</s:url>
												<a href="${edit_operator_request}" class="edit"></a>
											</td>
											<td class="center">
												<a href="javascript:;" class="remove"></a>
											</td>
										</tr>
									</s:iterator>
								</tbody>
							</table>
						</li>
					</s:if>
					
					<s:include value="operator_required_fields.jsp" />
					
					<s:if test="contractor.id > 0">
						<li id="tag_list">
							<s:include value="operator_tags.jsp" />
						</li>
					</s:if>
					
					<li>
						<a href="javascript:;" id="email_preview" class="preview">
							<s:text name="RequestNewContractor.PreviewEmail" />
						</a>
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.ContactSummary" />
				</h2>
				
				<ol>
					<s:if test="contractor.id > 0">
						<li>
							<label>
								<s:text name="RequestNewContractor.label.TimesContacted">
									<s:param>
										${contractor.totalContactCount}
									</s:param>
								</s:text>:
							</label>
							${contractor.contactCountByPhone} <s:text name="User.phone" />
							<br />
							${contractor.contactCountByEmail} <s:text name="User.email" />
						</li>
						<li>
							<label>
								<s:text name="global.Notes" />:
							</label>
							<s:include value="../../notes/account_notes_embed.jsp" />
						</li>
					</s:if>
					<s:if test="contractor.id == 0">
						<li>
							<div class="info">
								<s:text name="RequestNewContractor.message.AutoEmailOnSave" />
							</div>
						</li>
					</s:if>
				</ol>
			</fieldset>
			
			<s:if test="!permissions.operatorCorporate">
				<fieldset class="form">
					<h2 class="formLegend">
						<s:text name="ContractorRegistrationRequest.label.status" />
					</h2>
					
					<ol>
						<li>
							<label for="inside_sales_priority">
								<s:text name="global.Priority" />
							</label>
							<select name="contractor.insideSalesPriority" id="inside_sales_priority">
								<s:iterator value="@com.picsauditing.jpa.entities.LowMedHigh@values()" var="sales_priority">
									<s:if test="contractor.insideSalesPriority == #sales_priority">
										<s:set var="priority_selected" value="%{' selected=\"selected\"'}" />
									</s:if>
									<s:else>
										<s:set var="priority_selected" value="%{''}" />
									</s:else>
									<option value="${sales_priority}"${priority_selected}>
										<s:text name="%{i18nKey}" />
									</option>
								</s:iterator>
							</select>
						</li>
						<s:if test="contactable">
							<li>
								<label>
									<s:text name="RequestNewContractor.LogContactBy" />
								</label>
							</li>
							<li>
								<input type="hidden" id="contact_type" name="contactType" value="" />
								
								<s:iterator value="contactTypes" var="contact_type">
									<s:if test="declined">
										<s:if test="contractor.status.requested">
											<a
												href="javascript:;"
												class="picsbutton negative contact-note-required"
												data-type="${contact_type}"
												data-placeholder="<s:text name="%{note}" />">
												<s:text name="%{button}" />
											</a>
										</s:if>
										<%-- else show nothing --%>
									</s:if>
									<s:else>
										<a
											href="javascript:;"
											class="picsbutton contact-note-required"
											data-type="${contact_type}"
											data-placeholder="<s:text name="%{note}" />">
											<s:text name="%{button}" />
										</a>
									</s:else>
								</s:iterator>
							</li>
							<li id="contact_note" class="hide">
								<textarea name="contactNote" cols="80" rows="3"></textarea>
							</li>
						</s:if>
					</ol>
				</fieldset>
			</s:if>
			
			<fieldset class="form submit">
				<s:submit
					cssClass="picsbutton positive"
					id="save_request_form"
					method="save"
					value="%{getText('button.Save')}"
				/>
			</fieldset>
		</s:form>
	</div>
</body>