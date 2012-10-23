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
			<s:url action="ReportNewRequestedContractor" var="request_report" />
			<a href="${request_report}">&lt;&lt;
				<s:text name="RequestNewContractor.link.BackToRequests" />
			</a>
		</pics:permission>
		
		<s:if test="!requestedContractor.status.requested">
			<div class="info">
				<s:if test="requestedContractor.status.active">
					<s:text name="RequestNewContractor.message.Registered">
						<s:param>
							${requestedContractor.name}
						</s:param>
						<s:param>
							<s:date name="requestedContractor.membershipDate" />
						</s:param>
					</s:text>
				</s:if>
				<s:else>
					<s:text name="RequestNewContractor.message.RequestClosed" />
				</s:else>
			</div>
		</s:if>
		
		<s:form action="RequestNewContractorAccount" validate="true" id="request_form">
			<s:hidden name="requestedContractor" />
			<s:hidden name="requestedContractor.status" />
			<s:hidden name="requestRelationship" />
			
			<s:if test="permissions.operator">
				<s:hidden name="requestRelationship.operatorAccount" value="%{permissions.accountId}" />
			</s:if>
			
			<s:hidden name="primaryContact" />
			<s:hidden name="contactType" id="contact_type_field" />
			<s:hidden name="contactNote" id="contact_note_field" />
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.CompanyInformation" />
				</h2>

				<ol>
					<s:if test="permissions.picsEmployee || requestedContractor.id == 0">
						<li>
							<s:textfield
								cssClass="checkReq"
								id="company_name"
								name="requestedContractor.name"
								size="35"
								theme="formhelp"
								required="true"
							/>
							<div id="match_name"></div>
						</li>
						<li>
							<s:textfield
								cssClass="checkReq"
								id="contact_name"
								name="primaryContact.name"
								theme="formhelp"
								required="true"
							/>
							<div id="match_contact"></div>
						</li>
						<li>
							<s:textfield
								cssClass="checkReq"
								name="primaryContact.phone"
								required="true"
								size="20"
								theme="formhelp"
							/>
							<div id="match_phone"></div>
						</li>
						<li>
							<s:textfield
								cssClass="checkReq"
								id="email"
								name="primaryContact.email"
								required="true"
								size="30"
								theme="formhelp"
							/>
							<div id="match_email"></div>
						</li>
						<li>
							<s:textfield
								cssClass="checkReq"
								id="taxID"
								maxLength="9"
								name="requestedContractor.taxId"
								size="9"
								theme="formhelp"
							/>
							<div id="match_tax"></div>
						</li>
					</s:if>
					<s:else>
						<li>
							<label>
								<s:text name="ContractorAccount.name" />
							</label>
							${requestedContractor.name}
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
							${requestedContractor.taxId}
						</li>
					</s:else>
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="global.PrimaryAddress" />
				</h2>
				
				<ol>
					<s:if test="permissions.picsEmployee || requestedContractor.id == 0">
						<li>
							<s:select
								id="country"
								list="countryList"
								listKey="isoCode" 
								listValue="name"
								name="requestedContractor.country"
								required="true"
								theme="formhelp"
								value="%{requestedContractor.country.isoCode}"
							/>
						</li>
						<li id="country_subdivision">
							<s:if test="requestedContractor.country != null">
								<s:select
									id="countrySubdivision_sel"
									label="%{requestedContractor.country.isoCode == 'CA' ? 'ContractorAccount.province' : 'ContractorAccount.countrySubdivision'}"
									list="getCountrySubdivisionList(requestedContractor.country.isoCode)"
									listKey="isoCode"
									listValue="simpleName"
									name="requestedContractor.countrySubdivision"
									required="true"
									theme="formhelp"
									value="%{requestedContractor.countrySubdivision.isoCode}"
								/>
							</s:if>
						</li>
						<li>
							<s:textfield
								name="requestedContractor.city"
								size="20"
								id="city"
								theme="formhelp"
							/>
						</li>
						<li class="address-zip">
							<s:textfield
								name="requestedContractor.address"
								size="35"
								id="address"
								theme="formhelp"
							/>
						</li>
						<li class="address-zip">
							<s:textfield
								name="requestedContractor.zip"
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
							${requestedContractor.country.name}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.countrySubdivision" />
							</label>
							${requestedContractor.countrySubdivision.simpleName}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.city" />
							</label>
							${requestedContractor.city}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.address" />
							</label>
							${requestedContractor.address}
						</li>
						<li>
							<label>
								<s:text name="ContractorAccount.zip" />
							</label>
							${requestedContractor.zip}
						</li>
					</s:else>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.RequestSummary" />
				</h2>
				
				
				<ol>
					<s:if test="requestedContractor.id > 0 && requestRelationship.operatorAccount.id > 0 && !permissions.operator">
						<s:url action="RequestNewContractorAccount" var="request_add_operator">
							<s:param name="requestedContractor">
								${requestedContractor.id}
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
									<s:iterator value="requestedContractor.operators" var="relationship" status="position">
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
													<s:param name="requestedContractor">
														${requestedContractor.id}
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
					
					<s:if test="requestedContractor.id > 0">
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
					<s:if test="requestedContractor.id > 0">
						<li>
							<label>
								<s:text name="RequestNewContractor.label.TimesContacted" />:
							</label>
							<s:property value="requestedContractor.totalContactCount" />
						</li>
						<li>
							<label>
								<s:text name="global.Notes" />:
							</label>
							<s:include value="../../notes/account_notes_embed.jsp" />
						</li>
					</s:if>
					<s:if test="requestedContractor.id == 0">
						<li>
							<div class="info">
								<s:text name="RequestNewContractor.message.AutoEmailOnSave" />
							</div>
						</li>
					</s:if>
				</ol>
			</fieldset>
			
			<s:if test="requestedContractor.id > 0">
				<fieldset class="form">
					<h2 class="formLegend">
						<s:text name="ContractorRegistrationRequest.label.status" />
					</h2>
					
					<ol>
						<s:if test="!permissions.operatorCorporate">
							<li>
								<label>
									<s:text name="ContractorRegistrationRequest.label.status" />:
								</label>
								<s:select 
									id="request_status" 
									list="@com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus@values()" 
									listValue="getText(i18nKey)"
									name="status"
									value="%{status}"
								/>
							</li>
							<li id="hold_date">
								<s:textfield
									id="holdDate"
									label="ContractorRegistrationRequest.holdDate"
									name="requestedContractor.followUpDate"
									cssClass="datepicker"
									required="true"
									size="10"
									theme="formhelp"
								/>
							</li>
							<li id="reason_declined">
								<s:textarea
									id="reasonForDecline"
									label="ContractorRegistrationRequest.reasonForDecline"
									name="requestedContractor.reason"
									required="true"
									theme="formhelp"
								/>
								<div class="fieldhelp">
									<h3>
										<s:text name="RequestNewContractor.label.reasonForDecline" />
									</h3>
									<s:text name="ContractorRegistrationRequest.help.CloseRequest" />
								</div>
							</li>
						</s:if>
						<s:else>
							<li>
								<label>
									<s:text name="ContractorRegistrationRequest.label.status" />:
								</label>
								<s:text name="%{status.I18nKey}" />
							</li>
							
							<s:if test="status.hold">
								<li>
									<label>
										<s:text name="ContractorRegistrationRequest.label.holdDate" />:
									</label>
									<s:date name="requestedContractor.followUpDate" format="%{getText('date.short')}"/>
								</li>
							</s:if>
							
							<s:if test="status.closedUnsuccessful">
								<li>
									<label>
										<s:text name="RequestNewContractor.label.reasonForDecline" />:
									</label>
									${requestedContractor.reason}
								</li>
							</s:if>
						</s:else>
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
				<s:if test="contactable">
					<input
						class="picsbutton contact phone"
						type="button"
						value="<s:text name="RequestNewContractor.button.ContactedByPhone" />"
					/>
					<input
						class="picsbutton contact email"
						type="button"
						value="<s:text name="RequestNewContractor.button.ContactedByEmail" />"
					/>
				</s:if>
			</fieldset>
		</s:form>
	</div>
	
	<div id="contact_form" class="hide">
		<fieldset class="form">
			<ol>
				<li>
					<s:select
						cssClass="contact-type"
						headerKey=""
						headerValue="- %{getText('RequestNewContractor.ContactType')} -"
						label="RequestNewContractor.ContactType"
						list="@com.picsauditing.actions.contractors.RequestNewContractorAccount$RequestContactType@values()"
						listValue="%{getText(i18nKey)}"
						name="contactType"
					/>
				</li>
				<li>
					<label>
						<s:text name="RequestNewContractor.label.AddAdditionalNotes" />
					</label>
					<s:textarea cssClass="contact-note" name="contactNote" />
				</li>
			</ol>
		</fieldset>
	</div>
</body>