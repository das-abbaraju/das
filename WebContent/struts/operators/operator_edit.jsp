<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.PICS.DateBean" %>

<head>
	<title>
		<s:property value="operator.name" default="Create New Account" />
	</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css?v=${version}" />
	
	<s:include value="../jquery.jsp" />
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<s:if test="operator.id == 0">
		    <h1>Create New 
		    <s:if test="operator.operator">
		    	Client Site
		    </s:if>
  				<s:else>
  					<s:property value="operator.type" />
			</s:else>
               Account</h1>
		</s:if>
		<s:else>
			<s:include value="opHeader.jsp"></s:include>
		</s:else>
		
		<s:if test="hasFieldErrors()">
			<div class="error">
				<s:iterator value="fieldErrors.keySet()" var="field">
					<s:iterator value="fieldErrors.get(#field)" var="error">
						${error}<br />
					</s:iterator>
				</s:iterator>
			</div>
		</s:if>
		
		<s:if test="permissions.admin">
			<s:if test="operator.visibleAudits.size == 0">
				<div class="alert">
					<s:text name="FacilitiesEdit.NoAccessToAudits">
						<s:param>
							<s:property value="operator.id" />
						</s:param>
					</s:text>
				</div>
			</s:if>
			
			<s:if test="operator.flagCriteriaInherited.size == 0">
				<div class="alert">
					<s:text name="FacilitiesEdit.NoFlagCriteriaDefined">
						<s:param>
							<s:property value="operator.id" />
						</s:param>
					</s:text>
				</div>
			</s:if>
		</s:if>
		
		<s:form id="save" method="POST" enctype="multipart/form-data">
			<div>
				<s:submit cssClass="picsbutton positive" value="%{getText('button.Save')}" method="save" />
			</div>
			<br clear="all" />
			
			<s:hidden name="operator" />
			<s:hidden name="createType" />
			<s:hidden name="operatorCountrySubdivision" value="%{operator.countrySubdivision.isoCode}" id="operatorCountrySubdivision" />
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="FacilitiesEdit.AccountSummary" /></h2>

				<ol>
					<s:if test="operator.id > 0">
						<li>
							<s:text name="OperatorAccount.createdBy" />:
							<s:set var="o" value="operator" />
							<s:include value="../who.jsp" />
						</li>
					</s:if>
					
					<li>
						<s:textfield name="operator.name" maxlength="50" theme="formhelp" />
					</li>
					<li>
						<s:textfield name="operator.dbaName" theme="formhelp" />
					</li>
					
					<s:if test="permissions.admin">
						<li>
							<s:select list="statusList" name="operator.status" theme="form" listValue="%{getText(i18nKey)}" />
							<div class="fieldhelp">
								<s:text name="OperatorAccount.status.fieldhelp" />
							</div>
						</li>
					</s:if>

					<s:if test="operator.id > 0">
						<s:if test="operator.status.deactivated || operator.status.deleted">
							<li>
								<s:textarea name="operator.reason" rows="3" theme="form" />
							</li>
						</s:if>
						
						<s:if test="operator.operator">
							<li>
								<label><s:text name="FacilitiesEdit.AccountManager" />:</label>
								<s:iterator value="accountManagers" id="au">
									<s:property value="#au.user.name"/>
								</s:iterator>
							</li>
							
							<s:if test="salesReps.size() > 0">
								<li>
									<label><s:text name="FacilitiesEdit.SalesRepresentative" />:</label>
									<s:iterator value="salesReps" id="au">
										<s:property value="#au.user.name"/>
									</s:iterator>
								</li>
							</s:if>
						</s:if>
					</s:if>
				</ol>
			</fieldset>
			
			<s:if test="operator.id > 0 && permissions.picsEmployee">
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="FacilitiesEdit.LinkedAccounts" /></h2>
					<ol>
						<s:if test="operator.corporate">
							<li>
								<s:checkbox name="operator.primaryCorporate" theme="formhelp" />
							</li>
							<li>
								<label>
									<s:text name="FacilitiesEdit.ChildOperators" />:
								</label>
								
								<s:optiontransferselect
									label="Child Operators"
									name="operatorListLeft"
									list="notChildOperatorList"
									listKey="id"
									listValue="name"
									doubleName="facilities"
									doubleList="childOperatorList"
									doubleListKey="id"
									doubleListValue="name"
									leftTitle="%{getText('FacilitiesEdit.OperatorsList')}"
									rightTitle="%{getText('FacilitiesEdit.ChildOperators')}"
									addToLeftLabel="%{getText('FacilitiesEdit.Remove')}"
									addToRightLabel="%{getText('FacilitiesEdit.Assign')}"
									allowAddAllToLeft="false"
									allowAddAllToRight="false"
									allowSelectAll="false"
									allowUpDownOnLeft="false"
									allowUpDownOnRight="false"
									buttonCssClass="arrow"
									theme="pics"
		 						/>
							</li>
						</s:if>
						
						<s:if test="operator.operator">
							<s:if test="operator.corporateFacilities.size() > 0">
								<li>
									<label>
										<s:text name="FacilitiesEdit.ParentCorporationDivisionHub" />:
									</label>
									<s:select
										name="operator.parent"
										list="operator.corporateFacilities"
										listKey="corporate.id"
										listValue="corporate.name"
										headerKey="0"
										value="operator.parent.id"
										headerValue="- %{getText('FacilitiesEdit.SelectParentFacility')} -"
									/>
									
									<s:if test="operator.parent.id > 0">
										<a href="?operator=<s:property value="operator.parent.id"/>">
											<s:text name="FacilitiesEdit.Go" />
										</a>
									</s:if>
									
									<div class="fieldhelp">
										<h3>
											<s:text name="FacilitiesEdit.ParentCorporationDivisionHub" />
										</h3>
										<s:text name="OperatorAccount.parent.fieldhelp" />
									</div>
								</li>
							</s:if>
							
							<li>
								<label>
									<s:text name="FlagCriteria" />:
								</label>
								<s:select
									name="operator.inheritFlagCriteria"
									value="operator.inheritFlagCriteria.id"
									list="relatedFacilities"
									listKey="id"
									listValue="name"
								/>
									
								<s:if test="operator.inheritFlagCriteria.id > 0">
									<a href="?operator=<s:property value="operator.inheritFlagCriteria.id"/>">
										<s:text name="FacilitiesEdit.Go" />
									</a>
								</s:if>
							</li>
							<li>
								<label>
									<s:text name="FlagCriteria.insurance" />:
								</label>
								<s:select
									name="operator.inheritInsuranceCriteria"
									value="operator.inheritInsuranceCriteria.id"
									list="relatedFacilities"
									listKey="id"
									listValue="name"
								/>
								
								<s:if test="operator.inheritInsuranceCriteria.id > 0">
									<a href="?operator=<s:property value="operator.inheritInsuranceCriteria.id"/>">
										<s:text name="FacilitiesEdit.Go" />
									</a>
								</s:if>
							</li>
						</s:if>
						<s:if test="operator.corporateFacilities.size() > 0">
							<li>
								<label>
									<s:text name="OperatorAccount.corporateFacilities" />:
								</label>
								
								<s:iterator value="operator.corporateFacilities" id="facility">
									|
									<s:if test="#facility.corporate.inPicsConsortium && !permissions.admin">
										<s:property value="#facility.corporate.name"/>
									</s:if>
									<s:else>
										<a href="FacilitiesEdit.action?operator=<s:property value="#facility.corporate.id"/>">
											<s:property value="#facility.corporate.name"/>
										</a>
									</s:else>
								</s:iterator>
								|
							</li>
						</s:if>
					</ol>
				</fieldset>
			</s:if>
			
			<s:if test="permissions.picsEmployee">
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="FacilitiesEdit.GeneralContractor" /></h2>
					<ol>
						<li>
							<label for="general_contractor_checkbox">
								<s:text name="FacilitiesEdit.IsGeneralContractor" />:
							</label>
							<s:checkbox 
                                name="operator.generalContractor"
                                label="FacilitiesEdit.IsGeneralContractor" 
                                id="general_contractor_checkbox" />
						</li>
						
						<s:if test="operator.id > 0">
							<s:set var="display_linked_client" value="%{'display: inline;'}" />
							<s:if test="!operator.generalContractor">
								<s:set var="display_linked_client" value="%{'display: none;'}" />
							</s:if>

							<li id="linked_clients" style="${display_linked_client}">
	                            <label>
	                            	<s:text name="FacilitiesEdit.LinkedClientAccount" />:
	                            </label>
	                            <s:optiontransferselect
									label="Selected Clients"
									name="selectedClientsLeft"
									list="notSelectedClients"
									listKey="id"
									listValue="name"
									doubleName="clients"
									doubleList="selectedClients"
									doubleListKey="id"
									doubleListValue="name"
									leftTitle="%{getText('FacilitiesEdit.OperatorsList')}"
									rightTitle="%{getText('FacilitiesEdit.SelectedClients')}"
									addToLeftLabel="%{getText('FacilitiesEdit.Remove')}"
									addToRightLabel="%{getText('FacilitiesEdit.Assign')}"
									allowAddAllToLeft="false"
									allowAddAllToRight="false"
									allowSelectAll="false"
									allowUpDownOnLeft="false"
									allowUpDownOnRight="false"
									buttonCssClass="arrow"
									theme="pics"
		 						/>
							</li>
						</s:if>
					</ol>
				</fieldset>
			</s:if>

			<fieldset class="form">
				<h2 class="formLegend"><s:text name="global.PrimaryAddress" /></h2>
				
				<ol>
					<s:if test="operator.id > 0">
						<li>
							<label><s:text name="global.ContactPrimary" />:</label>
							<s:select
								list="primaryOperatorContactUsers"
								name="contactID"
								listKey="id"
								listValue="name"
								headerKey=""
								headerValue="- %{getText('FacilitiesEdit.SelectAUser')} -"
								value="%{operator.primaryContact.id}" />

							<s:if test="operator.primaryContact">
								<s:url action="UsersManage" var="view_primary_user">
									<s:param name="account">
										${operator.id}
									</s:param>
									<s:param name="user">
										${operator.primaryContact.id}
									</s:param>
								</s:url>
								<a href="${view_primary_user}">
									<s:text name="button.View" />
								</a>
							</s:if>
							<s:else>
								<s:url action="UsersManage!add" var="add_primary_user">
									<s:param name="account">
										${operator.id}
									</s:param>
									<s:param name="isActive" value="'Yes'" />
									<s:param name="isGroup" />
									<s:param name="userIsGroup" value="'No'" />
								</s:url>
								<a class="add" href="${add_primary_user}">
									<s:text name="FacilitiesEdit.AddUser" />
								</a>
							</s:else>
						</li>
					</s:if>
					
					<li>
						<label><s:text name="global.Address" />:</label>
						<s:textfield name="operator.address" size="35" />
						<br />
						<s:textfield name="operator.address2" size="35" />
					</li>
					<li>
						<s:textfield name="operator.city" size="20" theme="formhelp" />
					</li>
					<li>
						<label><s:text name="Country" />:</label>
						<s:select
							list="countryList"
							id="opCountry"
							name="operator.country"
							listKey="isoCode"
							listValue="name"
							headerKey=""
							headerValue="- %{getText('Country')} -"
							value="%{operator.country.isoCode}"
						/>
						
						<s:if test="permissions.admin || operator.operator">
							<div class="fieldhelp">
								<h3>
									<s:text name="Country" />
								</h3>
								<s:text name="OperatorAccount.country.fieldhelp" />
							</div>
						</s:if>
					</li>
					<li id="countrySubdivision_li"></li>
					
					<s:if test="operator.country.isoCode != 'AE'">
						<li id="zip_li">
							<s:textfield name="operator.zip" size="7" theme="form" />
						</li>
					</s:if>
					
					<li>
						<s:select
							name="operator.timezone"
							value="operator.timezone.iD"
							theme="form"
							list="@com.picsauditing.util.TimeZoneUtil@TIME_ZONES"
						/>
					</li>
					<li>
						<s:textfield name="operator.phone" theme="form" />
					</li>
					<li>
						<s:textfield name="operator.fax" theme="form" />
					</li>
					<li>
						<s:textfield name="operator.webUrl" theme="form" />
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="FacilitiesEdit.CompanyIdentification" />
				</h2>
				
				<ol>
					<li>
						<s:textarea name="operator.description" cols="40" rows="15" theme="formhelp" />
					</li>
					
					<s:if test="operator.id > 0">
						<li>
							<label>
								<s:text name="FacilitiesEdit.AccountSince" />:
							</label>
							<s:date name="operator.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@MonthAndYear}" />
						</li>
					</s:if>
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="FacilitiesEdit.Security" />
				</h2>
				<ol>
					<li>
						<s:checkbox id="rememberMeTimeCheckbox" name="operator.rememberMeTimeEnabled" theme="formhelp" onchange="$('#rememberMeTimeTextbox').attr('disabled', !this.checked)"/>
					</li>
					<li>
						<s:textfield id="rememberMeTimeTextbox" name="operator.rememberMeTimeInDays" theme="formhelp" disabled="!operator.rememberMeTimeEnabled"/>
					</li>
					<li>
						<s:textfield name="operator.sessionTimeout" theme="formhelp" />
					</li>
				</ol>
			</fieldset>
			<s:if test="permissions.admin">
				<fieldset class="form">
					<h2 class="formLegend">Configuration</h2>
					
					<ol>
						<s:if test="id > 0">
							<li>
								<label>Required Tags:</label> <s:textfield name="operator.requiredTags" />
								<pics:fieldhelp title="Required Tags">
									<p>
										Example: 1,2,3|4,5
										<a href="OperatorTags.action?id=<s:property value="id" />" target="_BLANK">
											Tags
										</a>
									</p>
								</pics:fieldhelp>
							</li>
						</s:if>
						
						<li>
							<label>Auto Approves Contractors:</label>
                               <input id="number_pending_not_approved" type="hidden" name="number_pending_not_approved" value="${pendingAndNotApprovedRelationshipCount}" />
                            <s:if test ="!permissions.marketing" >
                            	<s:hidden name="autoApproveRelationships" />
                            </s:if>
                            <s:checkbox 
                                name="autoApproveRelationships" 
                                cssClass="checkbox"
                                disabled="!permissions.marketing" 
                            />
                               
							<pics:fieldhelp title="Approves Contractors">
								If Unchecked, contractors must be approved before operator users will see them. 
								Default and recommended setting is Checked. 
								If Unchecked, at least one user should have the permissions: [Approve Contractors] and [View Unapproved Contractors].<br/>
                                <br/>
                                <strong>Note: If enabling, all Pending and Not Approved Contractors will be changed to Approved</strong>
							</pics:fieldhelp>
						</li>
						<li>
							<label>Health &amp; Safety Organization:</label>
							<s:select
								list="@com.picsauditing.jpa.entities.OshaType@values()"
								name="operator.oshaType"
							/>
							<pics:fieldhelp title="Health &amp; Safety Organization">
								<p>The source of statistics that should be used to evaluate contractors. Operators can collect more than one, but only one can be used to evaluate stats.</p>
							</pics:fieldhelp>
						</li>
						<li>
							<label>Contractors pay:</label>
							<s:radio 
								list="#{'Yes':'Yes','No':'No','Multiple':'Multiple'}" 
								name="operator.doContractorsPay"
								theme="pics"
								cssClass="inline" 
							/>
							<pics:fieldhelp>Are contractors required to pay. This field is only applicable for Active accounts. Default = Yes
								Multiple means that contractors working only for this operator will not be charged an annual membership fee.
							</pics:fieldhelp>
						</li>
						
						<s:if test="!operator.corporate">
							<li>
								<label>Accepts Bid Only Contractor:</label>
								<s:checkbox name="operator.acceptsBids" />
								<pics:fieldhelp>
									Does this operator allow bid only contractors to register at a reduced rate? If Yes, then contractors who select only this operator, will have the option to choose a bid only account.
								</pics:fieldhelp>
							</li>
						</s:if>
						
						<li>
							<label>InsureGUARD&trade;:</label>
							<s:radio 
								list="#{'Yes':'Yes','No':'No'}" 
								name="operator.canSeeInsurance"
								theme="pics"
								cssClass="inline" 
							/>
							<pics:fieldhelp>
								This field is no longer needed. Edit the configuration to add InsureGUARD features.
							</pics:fieldhelp>
						</li>
						<li>
							<label>InsureGUARD&trade; Auto Approve Policies:</label>
							<s:checkbox name="operator.autoApproveInsurance" />
							<pics:fieldhelp title="InsureGUARD Auto Approve">
								Check this box to automatically approve Completed policies when they have a recommended status of Approve.
							</pics:fieldhelp>
						</li>
						<li>
							<label>EmployeeGUARD&trade; Uses Operator Qualification (OQ):</label>
							<s:checkbox name="operator.requiresOQ" />
							<pics:fieldhelp>
								Check this box to enable all of the OQ features for this operator and qualifying contractors.
							</pics:fieldhelp>
						</li>
						<li>
							<label>EmployeeGUARD&trade; Uses HSE Competency Review:</label>
							<s:checkbox name="operator.requiresCompetencyReview" />
							<pics:fieldhelp>
								Check this box to enable all of the HSE Competency features for this operator and qualifying contractors.
							</pics:fieldhelp>
						</li>
						<s:if test="operator.operator">
					    	<li>
					    		<label>Operator Service Types:</label>
				    			<s:checkbox
									name="operator.onsiteServices"
									disabled="!permissions.marketing" />
								<s:text name="ContractorAccount.onsiteServices" />
				    			<s:checkbox
									name="operator.offsiteServices"
									disabled="!permissions.marketing" />
								<s:text name="ContractorAccount.offsiteServices" />
				    			<s:checkbox
									name="operator.materialSupplier"
									disabled="!permissions.marketing" />
								<s:text name="ContractorAccount.materialSupplier" />
				    			<s:checkbox
									name="operator.transportationServices"
									disabled="!permissions.marketing" />
								<s:text name="ContractorAccount.transportationServices" />
							</li>
						</s:if>
						<li id="act_li">
							<label>Activation Fee Discount Percentage:</label>

							<s:if test="!operator.hasDiscount && operator.inheritedDiscountPercentOperator">
                                <s:text name="format.percent">
                                    <s:param value="%{operator.inheritedDiscountPercentOperator.discountPercent}" />
                                </s:text>
								&nbsp;&nbsp;(Discount Percentage inherited from <a href="FacilitiesEdit.action?operator=${operator.inheritedDiscountPercentOperator.id}">${operator.inheritedDiscountPercentOperator.name}</a>).
							</s:if>
							<s:else>
								<pics:permission perm="UserRolePicsOperator">
                                    <s:textfield name="operator.scaledDiscountPercent" /> %
								</pics:permission>
								<pics:permission negativeCheck="true" perm="UserRolePicsOperator">
                                    <s:text name="format.percent">
                                        <s:param value="%{operator.discountPercent}" />
                                    </s:text>
								</pics:permission>
							</s:else>
							
							<pics:fieldhelp title="Activation Fee Discount Percentage">
								<p>
									The percentage based discount to give contractors associated with this Site on their Activation Fee.
									For instance a 25% discount would give a contractor an activation fee of $150 instead of $200.
								</p>
							</pics:fieldhelp>
						</li>
                        <li>
                            <label>Discount Expiration Date:</label>
                            <pics:permission perm="UserRolePicsOperator">
                                <s:textfield name="operator.discountExpiration" cssClass="datepicker"/>
                            </pics:permission>
                            <pics:permission negativeCheck="true" perm="UserRolePicsOperator">
                                <s:if test="operator.discountExpiration">
                                    <s:date name="operator.discountExpiration" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
                                </s:if>
                                <s:else>
                                    <s:text name="JS.Filters.status.None" />
                                </s:else>
                            </pics:permission>
                            <s:property value="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
                        </li>
                        <s:if test ="operator.corporate && permissions.hasGroup(9)" >
						<li>
							<label>Is In PICS Consortium:</label>
                            <s:checkbox 
                                name="operator.inPicsConsortium" 
                                cssClass="checkbox"
                            />
							<pics:fieldhelp title="Is In PICS Consortium">
								Used to create PICS countries such as PICS-UK, PICS-US.
							</pics:fieldhelp>
						</li>
						</s:if>
                    </ol>
				</fieldset>
				
				<s:if test="operator.id > 0">
					<pics:permission perm="UserRolePicsOperator" type="Edit">
						<fieldset class="form">
							<h2 class="formLegend">Manage Representatives</h2>
							
							<ol>
								<li>
									<label>Sales Representatives:</label>
									<table class="report">
										<thead>
											<tr>
												<td>
													User
												</td>
												<td>
													Percent
												</td>
												<td>
													Start
												</td>
												<td>
													End
												</td>
												<td></td>
												
												<s:if test="operator.corporate">
													<td></td>
												</s:if>
											</tr>
										</thead>
										<tbody>
											<s:iterator value="operator.accountUsers" status="role">
												<%-- <s:hidden value="%{role}" name="accountRole" /> --%>
												
												<s:if test="role.description == 'Sales Representative' && current">
													<tr>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:property value="user.name" />
														</td>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:property value="ownerPercent" />%
														</td>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:date name="startDate" />
														</td>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:date name="endDate" />
														</td>
														<td>
															<s:url var="facilities_edit_remove" action="FacilitiesEdit" method="remove">
																<s:param name="operator" value="%{operator.id}" />
																<s:param name="accountUser" value="%{id}" />
															</s:url>
															<a href="${facilities_edit_remove}" class="remove">
																<s:text name="button.Remove" />
															</a>
														</td>
														
														<s:if test="operator.corporate">
															<td>
																<s:url var="facilities_edit_copy" action="FacilitiesEdit" method="copyToChildAccounts">
																	<s:param name="operator" value="%{operator.id}" />
																	<s:param name="accountUser" value="%{id}" />
																</s:url>
																<a href="${facilities_edit_copy}" class="add">
																	<s:text name="FacilitiesEdit.CopyToChildAccounts" />
																</a>
															</td>
														</s:if>										
													</tr>
													<tr id="show_<s:property value="id"/>" style="display: none;">
														<td colspan="4">
															<nobr>
																<s:textfield
																	name="operator.accountUsers[%{#role.index}].ownerPercent"
																	value="%{ownerPercent}" size="3"
																/>%
																&nbsp;&nbsp;
																<s:textfield
																	cssClass="blueMain datepicker" size="10"
																	name="operator.accountUsers[%{#role.index}].startDate"
																	id="startDate[%{id}]"
																	value="%{@com.picsauditing.PICS.DateBean@format(startDate, @com.picsauditing.util.PicsDateFormat@Iso)}"
																/>
																&nbsp;&nbsp;
																<s:textfield
																	cssClass="blueMain datepicker"
																	size="10"
																	name="operator.accountUsers[%{#role.index}].endDate"
																	id="endDate[%{id}]"
																	value="%{@com.picsauditing.PICS.DateBean@format(endDate, @com.picsauditing.util.PicsDateFormat@Iso)}"
																/>
															</nobr>
														</td>
														<td>
															<s:submit cssClass="picsbutton positive" method="saveRole" value="Save Role" />
														</td>
													</tr>
												</s:if>
											</s:iterator>
											<tr>
												<td colspan="4">
													<s:select
														name="salesRep.user.id"
														list="userList"
														listKey="id"
														listValue="name"
														headerKey="0"
														headerValue="- Select a User -"
													/>
												</td>
												<td <s:if test="operator.corporate">colspan="2"</s:if>>
													<s:hidden value="PICSSalesRep" name="salesRep.role" />
													<s:submit cssClass="picsbutton positive" method="addRole" value="Add Role" />
												</td>
											</tr>
										</tbody>
									</table>
								</li>
								<li>
									<label>Account Managers: </label>
									
									<table class="report">
										<thead>
											<tr>
												<td>
													User
												</td>
												<td>
													Percent
												</td>
												<td>
													Start
												</td>
												<td>
													End
												</td>
												<td></td>
												
												<s:if test="operator.corporate">
													<td></td>
												</s:if>
											</tr>
										</thead>
										<tbody>
											<s:iterator value="operator.accountUsers" status="role">
												<s:if test="role.description == 'Account Manager' && current">
													<tr>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:property value="user.name" />
														</td>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:property value="ownerPercent" />%
														</td>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:date name="startDate" />
														</td>
														<td onclick="$('#show_<s:property value="id"/>').show();">
															<s:date name="endDate" />
														</td>
														<td>
															<s:url var="facilities_edit_remove" action="FacilitiesEdit" method="remove">
																<s:param name="operator" value="%{operator.id}" />
																<s:param name="accountUser" value="%{id}" />
															</s:url>
															<a href="${facilities_edit_remove}" class="remove">
																<s:text name="button.Remove" />
															</a>
														</td>
														
														<s:if test="operator.corporate">
															<td>
																<s:url var="facilities_edit_copy" action="FacilitiesEdit" method="copyToChildAccounts">
																	<s:param name="operator" value="%{operator.id}" />
																	<s:param name="accountUser" value="%{id}" />
																</s:url>
																<a href="${facilities_edit_copy}" class="add">
																	<s:text name="FacilitiesEdit.CopyToChildAccounts" />
																</a>
															</td>
														</s:if>										
													</tr>
													<tr id="show_<s:property value="id"/>" style="display: none;">
														<td colspan="4">
															<nobr>
																<s:textfield
																	name="operator.accountUsers[%{#role.index}].ownerPercent"
																	value="%{ownerPercent}" size="3"
																/>%
																&nbsp;&nbsp;
																<s:textfield
																	cssClass="blueMain datepicker" size="10"
																	name="operator.accountUsers[%{#role.index}].startDate"
																	id="startDate[%{id}]"
																	value="%{@com.picsauditing.PICS.DateBean@format(startDate, @com.picsauditing.util.PicsDateFormat@Iso)}"
																/>
																&nbsp;&nbsp;
																<s:textfield cssClass="blueMain datepicker"
																	size="10"
																	name="operator.accountUsers[%{#role.index}].endDate"
																	id="endDate[%{id}]"
																	value="%{@com.picsauditing.PICS.DateBean@format(endDate, @com.picsauditing.util.PicsDateFormat@Iso)}"
																/>
															</nobr>
														</td>
														<td>
															<s:submit cssClass="picsbutton positive" method="saveRole" value="Save Role" />
														</td>
													</tr>
												</s:if>
											</s:iterator>
											
											<tr>
												<td colspan="4">
													<s:select name="accountRep.user.id" list="userList" listKey="id" listValue="name" headerKey="0" headerValue="- Select a User -" />
												</td>
												<td <s:if test="operator.corporate">colspan="2"</s:if>>
													<s:hidden value="PICSAccountRep" name="accountRep.role" />
													<s:submit cssClass="picsbutton positive" method="addRole" value="Add Role" />
												</td>
											</tr>
										</tbody>
									</table>
								</li>
								
								<s:if test="previousManagers.keySet().size() > 0">
									<li>
										<label>Previous Representatives: </label>
										
										<table class="report">
											<thead>
												<tr>
													<td>
														User
													</td>
													<td>
														Role
													</td>
													<td>
														Percent
													</td>
													<td>
														Start
													</td>
													<td>
														End
													</td>
												</tr>
											</thead>
											<tbody>
												<s:iterator value="previousManagers.keySet()" id="key">
													<s:iterator value="previousManagers.get(#key)">
														<tr>
															<td>
																<s:property value="user.name" />
															</td>
															<td>
																<s:property value="#key.description" />
															</td>
															<td>
																<s:property value="ownerPercent" />%
															</td>
															<td>
																<s:date name="startDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
															</td>
															<td>
																<s:date name="endDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
															</td>
														</tr>
													</s:iterator>
												</s:iterator>
											</tbody>
										</table>
									</li>
								</s:if>
							</ol>
						</fieldset>
					</pics:permission>
				</s:if>
			</s:if>
			
			<fieldset class="form submit">
				<s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
				<s:if test="operator.id != 0">
					<s:if test="operator.corporate && canDeleteCorp">
						<s:submit cssClass="picsbutton negative" method="delete"
							value="%{getText('button.Delete')}" />
					</s:if>
					<s:if test="operator.operator && canDeleteOp">
						<s:submit cssClass="picsbutton negative" method="delete"
							value="%{getText('button.Delete')}" />
					</s:if>
				</s:if>
		</fieldset>
		</s:form>
	</div>
</body>