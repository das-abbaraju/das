<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>
			<s:text name="ReferNewClientSite.title" />
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/con_new_registration.css?v=${version}" />
		
		<s:include value="../jquery.jsp" />	
	</head>
	<body>
		<h1>
			<s:text name="ReferNewClientSite.title" />
		</h1>
		<s:include value="../actionMessages.jsp" />
		<pics:permission perm="ReferNewClientSite">
			<a href="ReportClientSiteReferrals.action">&lt;&lt;
				<s:text name="ReferNewClientSite.link.BackToReferrals" />
			</a>
		</pics:permission>
		<s:if test="newClientSite.clientSite != null || newClientSite.status != 'Active'">
			<div class="info">
				<s:if test="newClientSite.clientSite != null">
					<s:text name="ReferNewClientSite.message.Registered">
						<s:param>
							<s:property value="newClientSite.clientSite.name" />
						</s:param>
						<s:param>
							<s:date name="newClientSite.clientSite.creationDate" />
						</s:param>
					</s:text>
				</s:if>
				<s:if test="newClientSite.status != 'Active'">
					<s:text name="ReferNewClientSite.message.ReferralClosed" />
				</s:if>
			</div>
		</s:if>
		<s:form id="saveContractorForm">
			<s:hidden name="newClientSite" />
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="ReferNewClientSite.header.SourceCompanyInformation" />
				</h2>
				
				<ol>
					<li>
						<label>
							<s:text name="ReferNewClientSite.label.sourceCompanyName" />:
						</label>
						<pics:autocomplete name="newClientSite.source" action="ContractorAutocomplete" />
						
						<s:if test="newClientSite.source != null">
							<a href="ContractorView.action?id=<s:property value="newClientSite.source.id"/>">
								<s:property value="newClientSite.source.name" />
							</a>
						</s:if>
					</li>
					<li>
						<s:textfield name="newClientSite.sourceContact" theme="formhelp" />
					</li>
					<li>
						<s:textfield name="newClientSite.sourcePhone" size="20" theme="formhelp" />
					</li>
					<li>
						<s:textfield name="newClientSite.sourceEmail" size="30" id="email" theme="formhelp" />
					</li>
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="ReferNewClientSite.header.companyInformation" />
				</h2>
				
				<ol>
					<li>
						<s:textfield name="newClientSite.name" size="35" theme="formhelp" />
					</li>
					<li>
						<s:textfield name="newClientSite.contact" theme="formhelp" />
					</li>
					<li>
						<s:textfield name="newClientSite.phone" size="20" theme="formhelp" />
					</li>
					<li>
						<s:textfield name="newClientSite.email" size="30" id="email" theme="formhelp" />
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="ReferNewClientSite.header.ContactSummary" />
				</h2>
				
				<ol>
					<s:if test="newClientSite.id > 0">
						<li>
							<label>
								<s:text name="ReferNewClientSite.label.LastContactedBy" />:
							</label>
							<s:property value="newClientSite.lastContactedBy.name" /><br />
						</li>
						<li>
							<label>
								<s:text name="ReferNewClientSite.label.DateContacted" />:
							</label>
							<s:date name="newClientSite.lastContactDate" /><br />
						</li>
						<li>
							<label>
								<s:text name="ReferNewClientSite.label.TimesContacted" />:
							</label>
							<s:property value="newClientSite.contactCount" />
						</li>
						<li>
							<label>
								<s:text name="ReferNewClientSite.label.PICSClientSite" />:
							</label>
							
							<pics:autocomplete name="newClientSite.clientSite" action="OperatorAutocomplete" />
							
							<s:if test="newClientSite.clientSite != null">
								<a href="FacilitiesEdit.action?operator=<s:property value="newClientSite.clientSite.id"/>">
									<s:property value="newClientSite.clientSite.name" />
								</a>
							</s:if>
						</li>
						<li>
							<label>
								<s:text name="global.Notes" />:
							</label>
							<div id="notesDiv">
								<div id="notesHere">
									<pre id="addHere"></pre>
									<s:if test="newClientSite.notes.length() > 0">
										<pre id="notesPreview"><s:property value="newClientSite.notes" /></pre>
									</s:if>
								</div>
							</div>
						</li>
					</s:if>
					<s:if test="newClientSite.id == 0">
						<li>
							<div class="info">
								<s:text name="ReferNewClientSite.message.AutoEmailOnSave" />
							</div>
						</li>
					</s:if>
				</ol>
			</fieldset>
			
			<s:if test="newClientSite.id > 0">
				<fieldset class="form">
					<h2 class="formLegend">
						<s:text name="ClientSiteReferral.label.status" />
					</h2>
					
					<ol>
						<s:if test="!permissions.operatorCorporate">
							<li>
								<label>
									<s:text name="ClientSiteReferral.label.status" />:
								</label>
								<s:select 
									id="status" 
									list="@com.picsauditing.jpa.entities.ClientSiteReferralStatus@values()" 
									listValue="getText(i18nKey)"
									name="status"
									onchange="hideShow()"
								/>
							</li>
							<li id="reasonDeclinedLi">
								<s:textarea name="newClientSite.reasonForDecline" id="reasonForDecline" theme="formhelp" />
								<div class="fieldhelp">
									<h3>
										<s:text name="ReferNewClientSite.label.reasonForDecline" />
									</h3>
									<s:text name="ClientSiteReferral.help.CloseReferral" />
								</div>
							</li>
						</s:if>
						<s:else>
							<li>
								<label>
									<s:text name="ClientSiteReferral.label.status" />:
								</label>
								<s:property value="getText(newContractor.status.I18nKey)" />
							</li>
							
							<s:if test="status.closedUnsuccessful">
								<li>
									<label>
										<s:text name="ReferNewClientSite.label.reasonForDecline" />:
									</label>
									<s:property value="newClientSite.reasonForDecline" />
								</li>
							</s:if>
						</s:else>
					</ol>
				</fieldset>
			</s:if>
			
			<fieldset class="form submit">
				<s:submit value="%{getText('button.Save')}" method="save" cssClass="picsbutton positive" />
				
				<s:if test="newClientSite.id > 0 && newClientSite.phone != null && newClientSite.phone.length() > 0 && !permissions.operatorCorporate">
					<input type="button" class="picsbutton" value="<s:text name="ReferNewClientSite.button.ContactedByPhone" />" id="phoneContact"/>
					<input type="button" class="picsbutton" value="<s:text name="ReferNewClientSite.button.EditEmail" />" id="emailContact"/>
				</s:if>
			</fieldset>
		</s:form>
		
		<div style="display: none" id="load"></div>

		<div class="blockMsg" id="phoneSubmit">
			<s:form>
				<h3><s:text name = "ReferNewClientSite.button.ContactedByPhone" /></h3>
				<br />
				<s:hidden name="newClientSite"/>
				<s:hidden name="contactType" value="%{@vs@PHONE}"/>

				<label><s:text name="ReferNewClientSite.label.AddAdditionalNotes" />:</label>
				<p><s:textarea name="addToNotes" cols="30" rows="3"/></p>
				<p><s:submit value="Submit" method="contact" cssClass="picsbutton positive" /></p>
			</s:form>
		</div>
		<div class="blockMsg" id="emailSubmit">
			<s:form>
				<fieldset>
				<s:hidden name="newClientSite"/>
				<s:hidden name="contactType" value="%{@vs@EMAIL}"/>
				
				<p><h3><s:text name = "ReferNewClientSite.button.ContactedByEmail" /></h3></p>
				<p><label><s:text name="ReferNewClientSite.label.AddAdditionalNotes" />:</label></p>
				<p><s:textarea name="addToNotes" cols="30" rows="3"/></p>
				<p><s:submit value="Submit" method="contact" cssClass="picsbutton positive" /></p>
				</fieldset>
			</s:form>
		</div>
		<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js?v=${version}"></script>
		<script type="text/javascript" src="js/client_site_new_referral.js?v=${version}"></script>
	</body>
</html>