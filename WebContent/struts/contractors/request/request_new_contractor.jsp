<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:text name="RequestNewContractor.title" />
	</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	
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
	
	<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js?v=${version}"></script>
	<script type="text/javascript" src="js/jquery/scrollTo/jquery.scrollTo-min.js?v=${version}"></script>
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
		
		<s:if test="requestedContractor.status != 'Requested'">
			<div class="info">
				<s:if test="requestedContractor.status == 'Active'">
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
			<s:hidden name="requestRelationship" />
			<s:hidden name="primaryContact" />
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.CompanyInformation" />
				</h2>
				
				<ol>
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
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="global.PrimaryAddress" />
				</h2>
				
				<ol>
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
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.RequestSummary" />
				</h2>
				
				<ol>
					<li>
						<s:select
							headerKey="0" 
							headerValue="RequestNewContractor.header.SelectAnOperator" 
							id="operator_list"
							label="ContractorAccount.requestedBy"
							list="operatorList"
							listKey="id"
							listValue="name" 
							name="requestedContractor.requestedBy"
							required="true"
							theme="formhelp"
							value="%{requestedContractor.requestedBy.id}"
						/>
					</li>
					<li id="user_list">
						<s:if test="requestedContractor.requestedBy.id > 0">
							<s:include value="operator_users.jsp" />
						</s:if>
					</li>
					<li>
						<s:textfield
							cssClass="datepicker"
							id="regDate"
							name="requestRelationship.deadline"
							required="true"
							size="10"
							theme="formhelp"
							value="%{requestRelationship.deadline != null ? getTextParameterized('short_dates', requestRelationship.deadline) : ''}"
						/>
					</li>
					<li>
						<s:textarea
							id="reasonForRegistration"
							name="requestRelationship.reasonForRegistration"
							required="true"
							theme="formhelp"
						/>
					</li>
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
					<s:if test="requestedContractor.firstRegistrationRequest.id > 0">
						<li>
							<label>
								<s:text name="RequestNewContractor.label.TimesContacted" />:
							</label>
							<s:property value="requestedContractor.firstRegistrationRequest.contactCount" />
						</li>
						<li>
							<label>
								<s:text name="RequestNewContractor.label.MatchesFound" />:
							</label>
							
							<s:if test="requestedContractor.firstRegistrationRequest.matchCount > 0 && requestedContractor.firstRegistrationRequest.status == 'Active'">
								<a href="#potentialMatches" id="getMatches">
									<s:property value="requestedContractor.firstRegistrationRequest.matchCount" />
								</a>
							</s:if>
							<s:else>
								<s:property value="requestedContractor.firstRegistrationRequest.matchCount" />
							</s:else>
						</li>
						<li>
							<label>
								<s:text name="global.Notes" />:
							</label>
							<div id="notesDiv">
								<div id="notesHere">
									<pre id="addHere"></pre>
									<s:if test="newContractor.notes.length() > 0">
										<pre id="notesPreview"><s:property value="newContractor.notes" /></pre>
									</s:if>
								</div>
							</div>
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
			
			<s:if test="requestedContractor.firstRegistrationRequest.id > 0">
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
									name="requestedContractor.firstRegistrationRequest.status"
								/>
							</li>
							<li id="hold_date">
								<s:textfield
									id="holdDate"
									label="ContractorRegistrationRequest.holdDate"
									name="requestedContractor.firstRegistrationRequest.holdDate"
									cssClass="datepicker"
									size="10"
									theme="formhelp"
								/>
							</li>
							<li id="reason_declined">
								<s:textarea
									id="reasonForDecline"
									label="ContractorRegistrationRequest.reasonForDecline"
									name="requestedContractor.firstRegistrationRequest.reasonForDecline"
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
								<s:property value="getText(requestedContractor.firstRegistrationRequest.status.I18nKey)" />
							</li>
							
							<s:if test="status.hold">
								<li>
									<label>
										<s:text name="ContractorRegistrationRequest.label.holdDate" />:
									</label>
									<s:date name="requestedContractor.firstRegistrationRequest.holdDate" format="%{getText('date.short')}"/>
								</li>
							</s:if>
							
							<s:if test="status.closedUnsuccessful">
								<li>
									<label>
										<s:text name="RequestNewContractor.label.reasonForDecline" />:
									</label>
									<s:property value="requestedContractor.firstRegistrationRequest.reasonForDecline" />
								</li>
							</s:if>
						</s:else>
					</ol>
				</fieldset>
			</s:if>
			
			<fieldset class="form submit">
				<s:submit value="%{getText('button.Save')}" method="save" cssClass="picsbutton positive" />
				
				<s:if test="contactable">
					<input
						type="button"
						class="picsbutton"
						value="<s:text name="RequestNewContractor.button.ContactedByPhone" />"
						id="phoneContact" />
					<input
						type="button"
						class="picsbutton"
						value="<s:text name="RequestNewContractor.button.EditEmail" />"
						id="emailContact" />
				</s:if>
			</fieldset>
		</s:form>
	</div>
</body>