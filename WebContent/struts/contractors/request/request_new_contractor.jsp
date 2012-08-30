<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:text name="RequestNewContractor.title" />
	</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	
	<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js?v=${version}"></script>
	<script type="text/javascript" src="js/jquery/scrollTo/jquery.scrollTo-min.js?v=${version}"></script>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<h1>
			<s:text name="RequestNewContractor.title" />
		</h1>
		
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
		
		<s:form action="RequestNewContractorAccount" validate="true">
			<s:hidden name="requestedContractor" />
			<s:hidden name="requestRelationship" />
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.CompanyInformation" />
				</h2>
				
				<ol>
					<li>
						<s:textfield
							cssClass="checkReq"
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
							name="requestedContractor.primaryContact.name"
							theme="formhelp"
							required="true"
						/>
						<div id="match_contact"></div>
					</li>
					<li>
						<s:textfield
							cssClass="checkReq"
							name="requestedContractor.primaryContact.phone"
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
							name="requestedContractor.primaryContact.email"
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
					<li id="countrySubdivision_li">
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
							cssClass="show-address"
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
							id="operatorsList"
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
							<s:include value="../../users/operator_users.jsp" />
						</s:if>
					</li>
					<li>
						<a href="#email_preview" class="preview fancybox">
							<s:text name="RequestNewContractor.PreviewEmail" />
						</a>
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
					<li id="loadTagsList">
						<label>
							<s:text name="RequestNewContractor.OperatorTags" />
						</label>
						<div class="fieldhelp">
							<h3>
								<s:text name="RequestNewContractor.OperatorTags" />
							</h3>
							<s:text name="RequestNewContractor.OperatorTags.fieldhelp" />
						</div>
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
									id="status" 
									list="@com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus@values()" 
									listValue="getText(i18nKey)"
									name="requestedContractor.firstRegistrationRequest.status"
								/>
							</li>
							<li id="holdDateLi">
								<s:textfield
									id="holdDate"
									name="requestedContractor.firstRegistrationRequest.followUpDate"
									cssClass="datepicker"
									size="10"
									theme="formhelp"
								/>
							</li>
							<li id="reasonDeclinedLi">
								<s:textarea name="requestedContractor.reasonForDecline" id="reasonForDecline" theme="formhelp" />
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
									<s:date name="requestedContractor.firstRegistrationRequest.followUpDate" format="%{getText('date.short')}"/>
								</li>
							</s:if>
							
							<s:if test="status.closedUnsuccessful">
								<li>
									<label>
										<s:text name="RequestNewContractor.label.reasonForDecline" />:
									</label>
									<s:property value="requestedContractor.reasonForDecline" />
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