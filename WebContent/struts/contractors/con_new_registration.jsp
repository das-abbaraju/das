<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>
			<s:text name="RequestNewContractor.title" />
		</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css?v=1" />
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/registration.css" />
		
		<s:include value="../jquery.jsp" />	
	</head>
	<body>
		<h1>
			<s:text name="RequestNewContractor.title" />
		</h1>
		
		<pics:permission perm="RequestNewContractor">
			<a href="ReportNewRequestedContractor.action">&lt;&lt;
				<s:text name="RequestNewContractor.link.BackToRequests" />
			</a>
		</pics:permission>
		
		<s:include value="../actionMessages.jsp"></s:include>
		
		<div id="potentialMatches" class="info" style="display: none;"></div>
		
		<s:if test="newContractor.contractor != null || !newContractor.open">
			<div class="info">
				<s:if test="newContractor.contractor != null">
					<s:text name="RequestNewContractor.message.Registered">
						<s:param>
							<s:property value="newContractor.contractor.name" />
						</s:param>
						<s:param>
							<s:date name="newContractor.contractor.creationDate" />
						</s:param>
					</s:text>
				</s:if>
				 
				<s:if test="newContractor.open">
				</s:if>
				<s:else>
					<s:text name="RequestNewContractor.message.RequestClosed" />
				</s:else>
			</div>
		</s:if>
		
		<s:form id="saveContractorForm">
			<s:hidden name="newContractor" />
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.CompanyInformation" />
				</h2>
				
				<ol>
					<li>
						<s:textfield cssClass="checkReq" name="newContractor.name" 	size="35" theme="formhelp" />
						<div id="think_name"></div>
						<div id="match_name"></div>
					</li>
					<li>
						<s:textfield cssClass="checkReq" name="newContractor.contact" theme="formhelp" />
						<div id="think_contact"></div>
						<div id="match_contact"></div>
					</li>
					<li>
						<s:textfield cssClass="checkReq" name="newContractor.phone" size="20" theme="formhelp" />
						
						<div id="think_phone"></div>
						<div id="match_phone"></div>
					</li>
					<li>
						<s:textfield cssClass="checkReq" name="newContractor.email" size="30" id="email" theme="formhelp" />
						
						<div id="think_email"></div>
						<div id="match_email"></div>
					</li>
					<li>
						<s:textfield cssClass="checkReq" name="newContractor.taxID" size="9" maxLength="9" id="taxID" theme="formhelp" />
						<div id="think_tax"></div>
						<div id="match_tax"></div>
					</li>
					
					<s:if test="assignedCSR != null">
						<li>
							<label><s:text name="RequestNewContractor.label.AssignedCSR" />:</label>
							<nobr><s:property value="assignedCSR.name" /> / <s:property	value="assignedCSR.phone" /></nobr>
						</li>
					</s:if>
				</ol>
				
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="global.PrimaryAddress" />
				</h2>
				
				<ol>
					<li>
						<label for="newContractorCountry"><s:text name="Country" />:</label>
						<s:select list="countryList" name="newContractor.country" id="newContractorCountry" listKey="isoCode" 
							listValue="name" value="%{newContractor.country.isoCode}" />
							
						<div class="fieldhelp">
							<h3>
								<s:text name="Country" />
							</h3>
							
							<s:text name="ContractorRegistrationRequest.country.fieldhelp" />
						</div>
					</li>
					<li id="state_li"></li>
					<li>
						<s:textfield name="newContractor.city" size="20" id="city" cssClass="show-address" theme="formhelp" />
					</li>
					<li class="address-zip">
						<s:textfield name="newContractor.address" size="35" id="address" theme="formhelp" />
					</li>
					<li class="address-zip">
						<s:textfield name="newContractor.zip" size="7" id="zip" theme="formhelp" />
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="RequestNewContractor.header.RequestSummary" />
				</h2>
				
				<ol>
					<li>
						<label>
							<s:text name="ContractorRegistrationRequest.requestedBy" />:
						</label>
						<s:select list="operatorsList" id="operatorsList" headerKey="0" 
							headerValue="- %{getText('RequestNewContractor.header.SelectAnOperator')} -" 
							name="newContractor.requestedBy" listKey="id" listValue="name" 
							value="%{newContractor.requestedBy.id}" />
						
						<div class="fieldhelp">
							<h3><s:text name="ContractorRegistrationRequest.requestedBy" /></h3>
							<s:text name="ContractorRegistrationRequest.requestedBy.fieldhelp" />
						</div>
					</li>
					<li id="loadUsersList">
						<s:include value="../users/operator_users.jsp" />
					</li>
					<li>
						<s:textfield id="regDate" name="newContractor.deadline" cssClass="datepicker" size="10" onchange="checkDate(this)" theme="formhelp" />
					</li>
					<li>
						<s:textarea id="reasonForRegistration" name="newContractor.reasonForRegistration" theme="formhelp" />
					</li>
					<li id="loadTagsList">
						<label><s:text name="RequestNewContractor.OperatorTags" /></label>
						<s:optiontransferselect
							label="Operator Tags"
							name="operatorTags"
							list="operatorTags"
							listKey="id"
							listValue="tag"
							doubleName="requestedTags"
							doubleList="requestedTags"
							doubleListKey="id"
							doubleListValue="tag"
							leftTitle="%{getText('RequestNewContractor.AvailableTags')}"
							rightTitle="%{getText('RequestNewContractor.AssignedTags')}"
							addToLeftLabel="%{getText('RequestNewContractor.Remove')}"
							addToRightLabel="%{getText('RequestNewContractor.Assign')}"
							allowAddAllToLeft="false"
							allowAddAllToRight="false"
							allowSelectAll="false"
							allowUpDownOnLeft="false"
							allowUpDownOnRight="false"
							buttonCssClass="arrow"
							theme="pics"
						/>
						<div class="fieldhelp">
							<h3><s:text name="RequestNewContractor.OperatorTags" /></h3>
							<s:text name="RequestNewContractor.OperatorTags.fieldhelp" />
						</div>
					</li>
					
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="RequestNewContractor.header.ContactSummary" /></h2>
				
				<ol>
					<s:if test="newContractor.id > 0">
						<li>
							<label><s:text name="RequestNewContractor.label.LastContactedBy" />:</label>
							<s:property value="newContractor.lastContactedBy.name" /><br />
						</li>
						<li>
							<label><s:text name="RequestNewContractor.label.DateContacted" />:</label>
							<s:date name="newContractor.lastContactDate" /><br />
						</li>
					</s:if>
					
					<s:if test="newContractor.id > 0">
						
						<li>
							<label><s:text name="RequestNewContractor.label.TimesContacted" />:</label>
							<s:property value="newContractor.contactCount" />
						</li>
						<li>
							<label><s:text name="RequestNewContractor.label.MatchesFound" />:</label>
							
							<s:if test="newContractor.matchCount > 0 && newContractor.open">
								<a href="#potentialMatches" id="getMatches"><s:property value="newContractor.matchCount" /></a>
							</s:if>
							<s:else>
								<s:property value="newContractor.matchCount" />
							</s:else>
						</li>
						<li>
							<label><s:text name="RequestNewContractor.label.PICSContractor" />:</label>
							
							<s:if test="!permissions.operatorCorporate">
								<!-- autocomplete broken right now autocomplete name="newContractor.contractor" action="ContractorAutocomplete"/ -->
								<s:textfield name="newContractor.contractor" />
							</s:if>
							
							<s:if test="newContractor.contractor != null">
								<a href="ContractorView.action?id=<s:property value="newContractor.contractor.id"/>">
								<s:property value="newContractor.contractor.name" /></a>
							</s:if>
						</li>
					<li>
						<label><s:text name="global.Notes" />:</label>
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
					<s:if test="newContractor.id == 0">
						<li>
							<div class="info">
								<s:text name="RequestNewContractor.message.AutoEmailOnSave" />
							</div>
						</li>
					</s:if>
				</ol>
			</fieldset>
			
			<s:if test="newContractor.id > 0">
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="ContractorRegistrationRequest.label.status" /></h2>
					
					<ol>
						<s:if test="!permissions.operatorCorporate">
							<li>
								<label><s:text name="ContractorRegistrationRequest.label.status" />:</label>
								<s:select id="status" list="@com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus@values()" listKey="name()" listValue="getText(getI18nKey())" name="newContractor.status" onchange="hideShow()"/>
								  
							</li>
							<li id="holdDateLi">
								<s:textfield id = "holdDate" name="newContractor.holdDate"	cssClass="datepicker" size="10" onchange="checkDate(this)"	theme="formhelp" />
							</li>
							<li id="reasonDeclinedLi">
								<label><s:text name="RequestNewContractor.label.reasonForDecline" />:</label>
								
								<p>
									<s:textarea name="newContractor.reasonForDecline" id="reasonForDecline" />
								</p>
								
								<div class = "fieldhelp">
									<h3><s:text name="RequestNewContractor.label.reasonForDecline" /></h3>
										
									<s:text name="ContractorRegistrationRequest.help.CloseRequest" />
								</div>
							</li>
						</s:if>
						<s:else>
							<li>
								<label><s:text name="ContractorRegistrationRequest.label.status" />:</label>
								<s:property value="getText(newContractor.status.I18nKey)" />
							</li>
							
							<s:if test="newContractor.status==@com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus@Hold">
								<li>
									<label><s:text name="ContractorRegistrationRequest.label.holdDate" />:</label>
									<s:date name="newContractor.holdDate" format="MM/dd/yyyy"/>
								</li>
							</s:if>
							
							<s:if test="newContractor.status==@com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus@ClosedUnsuccessful">
								<li>
									<label><s:text name="RequestNewContractor.label.reasonForDecline" />:</label>
									<s:property value="newContractor.reasonForDecline" />
								</li>
							</s:if>
						</s:else>
					</ol>
				</fieldset>
			</s:if>
			
			<fieldset class="form submit">
				<s:submit value="%{getText('button.Save')}" method="save" cssClass="picsbutton positive" />
				
				<s:if test="newContractor.id > 0 && newContractor.phone != null && newContractor.phone.length() > 0 && !permissions.operatorCorporate">
					<input type="button" class="picsbutton" value="<s:text name="RequestNewContractor.button.ContactedByPhone" />" id="phoneContact"/>
					<input type="button" class="picsbutton" value="<s:text name="RequestNewContractor.button.EditEmail" />" id="emailContact"/>
				</s:if>
			</fieldset>
		</s:form>
		
		<div style="display: none" id="load"></div>
		
		<s:if test="formsViewable && forms.size() > 0">
			<div id="hidden">
				<div id="operatorForms">
					<table class="report">
						<thead>
							<tr>
								<th colspan="2">
									<s:text name="ManageForms.title" />
								</th>
								<th>
									<s:text name="global.Facility" />
								</th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="forms" status="stat">
								<tr>
									<td class="right">
										<s:property value="#stat.index + 1" />
									</td>
									<td>
										<a href="#" onclick="addAttachment('<s:property value="formName" />','<s:property value="file" />'); return false;">
											<s:property value="formName" />
										</a>
									</td>
									<td>
										<s:property value="account.name" />
									</td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
			</div>
		</s:if>
		
		<div class="blockMsg" id="phoneSubmit" style="display: none">
			<s:form>
				<h3><s:text name = "RequestNewContractor.button.ContactedByPhone" /></h3>
				
				<br />
				
				<s:hidden name="newContractor"/>
				<s:hidden name="contactType" value="Phone"/>
				
				<label><s:text name="RequestNewContractor.label.AddAdditionalNotes" />:</label>
				
				<p>
					<s:textarea name="addToNotes" cols="30" rows="3"/>
				</p>
				
				<p>
					<s:submit value="Submit" method="contact" cssClass="picsbutton positive" />
				</p>
			</s:form>
		</div>
		<div class="blockMsg" id="emailSubmit" style="display: none">
			<s:form>
				<fieldset>
				<s:hidden name="newContractor"/>
				
				<p>
					<h3><s:text name = "RequestNewContractor.button.ContactedByEmail" /></h3>
				</p>
				<p>
					<s:select list="#{'Personal Email':'Sent a Personal Email', 'Email':'Draft Email'}" name="contactType" />
				</p>
				<p>
					<label><s:text name="RequestNewContractor.label.AddAdditionalNotes" />:</label>
				</p>
				<p>
					<s:textarea name="addToNotes" cols="30" rows="3"/>
				</p>
				<p>
					<s:submit value="Submit" method="contact" cssClass="picsbutton positive" />
				</p>
				</fieldset>
			</s:form>
		</div>
		<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js"></script>
		<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
		<script type="text/javascript" src="js/con_new_registration.js"></script>
		<script type="text/javascript">
			var show = false;
			var chooseADate = '<s:text name="javascript.ChooseADate" />';
			var name ='<s:property value="permissions.name" />'; 
			
			<s:if test="newContractor.notes.length() > 0">
				show = true;
				$('#notesHere').show();		
			</s:if>
			
			function changeState(country) {
				$('#state_li').load('StateListAjax.action',{countryString: $('#newContractorCountry').val(), stateString: '<s:property value="newContractor.state.isoCode"/>', needsSuffix: false, prefix: 'newContractor.'});
			}
		</script>
	</body>
</html>