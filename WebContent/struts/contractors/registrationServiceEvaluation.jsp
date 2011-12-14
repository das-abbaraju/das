<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="registration-header">
	<section>
		<s:include value="/struts/contractors/registrationStep.jsp">
			<s:param name="step_current" value="2" />
			<s:param name="step_last" value="getLastStepCompleted()" />
		</s:include>
	</section>
</div>

<s:if test="hasActionErrors()">
	<s:actionerror cssClass="action-error alert-message error" />
</s:if>
	
<s:if test="getServicesHelpText() != ''">
	<div class="alert-error alert-message warning">
		<span class="icon warn"></span>
		
		<p>
			<s:property value="getServicesHelpText()" escape="false" />
		</p>
	</div>
</s:if>

<div class="service-evaluation">
	<s:form cssClass="service-evaluation-form" theme="pics">
	
		<section>
			<h1><s:text name="RegistrationServiceEvaluation.ServicesPerformed" /></h1>
			<ul>
				<li>
					<label><s:text name="RegistrationServiceEvaluation.SelectServices" /></label>
					<ul class="checkbox-list services-list">
						<li>
							<s:set name="onsite" value="" />
							<s:checkbox 
								name="conTypes" 
								id="onSite"
								fieldValue="Onsite"
								label="ContractorAccount.onsiteServices" 
								value="requireOnsite ? true : contractor.onsiteServices" 
								cssClass="checkbox"
								disabled="requireOnsite" 
							/>
							
							<div class="services-performed-description">
								<p>
									<s:text name="ContractorAccount.onsiteServices.withDescription" />
								</p>
							</div>
						</li>
						<li>
							<s:checkbox 
								name="conTypes" 
								id="offSite"
								fieldValue="Offsite"
								label="ContractorAccount.offsiteServices" 
								value="requireOffsite ? true : contractor.offsiteServices"
								cssClass="checkbox"
								disabled="requireOffsite" 
							/>
							
							<div class="services-performed-description">
								<p>
									<s:text name="ContractorAccount.offsiteServices.withDescription" />
								</p>
							</div>
						</li>
						<li>
							<s:checkbox 
								name="conTypes" 
								id="materialSupplier"
								fieldValue="Supplier" 
								label="ContractorAccount.materialSupplier"
								value="requireMaterialSupplier ? true : contractor.materialSupplier" 
								cssClass="checkbox"
								disabled="requireMaterialSupplier" 
							/>
							
							<div class="services-performed-description">
								<p>
									<s:text name="ContractorAccount.materialSupplier.withDescription" />
								</p>
							</div>
						</li>
						<li>
							<s:checkbox 
								name="conTypes" 
								id="transportation"
								fieldValue="Transportation"
								label="ContractorAccount.transportationServices" 
								value="requireTransportation ? true : contractor.transportationServices"
								cssClass="checkbox"
								disabled="requireTransportation" 
							/>
							
							<div class="services-performed-description">
								<p>
									<s:text name="ContractorAccount.transportationServices.withDescription" />
								</p>
							</div>
						</li>
					</ul>
				</li>
				<li>
					<label><s:text name="RegistrationServiceEvaluation.SoleProprietor" /></label>
					<s:radio 
						name="contractor.soleProprietor" 
						value="false" 
						list="#{true:'Yes', false:'No'}"
						cssClass="inline"
					/>
				</li>
				
				<s:if test="showBidOnly">
					<li>
						<label><s:text name="RegistrationServiceEvaluation.BidOnly" /></label>
						<s:radio 
							name="contractor.accountLevel" 
							value="contractor.accountLevel" 
							list="#{@com.picsauditing.jpa.entities.AccountLevel@BidOnly:'Yes', @com.picsauditing.jpa.entities.AccountLevel@Full:'No'}"
							cssClass="inline" 
						/>
					</li>
				</s:if>
			</ul>
		</section>
		
		<%-- service safety evaluation display toggle --%>
		<s:if test="contractor.onsiteServices || contractor.offsiteServices || contractor.transportationServices || requireOnsite || requireOffsite || requireTransportation">
			<s:set var="service_safety_evaluation_display" value="" /> 
		</s:if>
		<s:else>
			<s:set var="service_safety_evaluation_display" value="'display: none;'" />
		</s:else>
		
		<div class="service-safety-evaluation" style="${service_safety_evaluation_display}">
		
			<div class="separator"></div>
			
			<section>
				<h1><s:text name="RegistrationServiceEvaluation.ServiceSafety" /></h1>
	
				<ul>
					<s:iterator value="infoQuestions" var="question">
						<s:if test="#question.category.id == 1721 && #question.questionType=='MultipleChoice'">
							<li class="audit-question" data-audit-id="${conAudit.id}" data-question-id="${question.id}">
								<label><s:property value="#question.name" /></label>
								
								<s:if test="#question.option.uniqueCode == 'YesNo'">
									<s:set var="radio_class" value="'inline'" />
								</s:if>
								<s:else>
									<s:set var="radio_class" value="" />
								</s:else>
								
								<s:radio 
									list="#question.option.values"
									listKey="uniqueCode" 
									listValue="name" 
									name="answerMap[%{#question.id}].answer"
									value="answerMap[#question.id].answer"
									cssClass="%{#radio_class}" 
								/>
							</li>
						</s:if>
					</s:iterator>
				</ul>
			</section>
		
		</div>
		
		<%-- product safety evaluation display toggle --%>
		<s:if test="contractor.materialSupplier">
			<s:set var="product_safety_evaluation_display" value="" /> 
		</s:if>
		<s:else>
			<s:set var="product_safety_evaluation_display" value="'display: none;'" />
		</s:else>
		
		<div class="product-safety-evaluation" style="${product_safety_evaluation_display}">
		
			<div class="separator"></div>
			
			<section>
				<h1><s:text name="RegistrationServiceEvaluation.ProductSafety" /></h1>
				
				<ul>
					<s:iterator value="infoQuestions" var="question">
						<s:if test="#question.category.id == 1683 && #question.questionType=='MultipleChoice'">
							<li class="audit-question" data-audit-id="${conAudit.id}" data-question-id="${question.id}">
								<label><s:property value="#question.name" /></label>
							
								<s:if test="#question.option.uniqueCode == 'YesNo'">
									<s:set var="radio_class" value="'inline'" />
								</s:if>
								<s:else>
									<s:set var="radio_class" value="" />
								</s:else>
								
								<s:radio 
									list="#question.option.values"
									listKey="uniqueCode" 
									listValue="name" 
									name="answerMap[%{#question.id}].answer"
									value="answerMap[#question.id].answer"
									cssClass="%{#radio_class}" 
								/>
							</li>
						</s:if>
					</s:iterator>
				</ul>
			</section>
			
		</div>
		
		<ul>
			<li class="actions">
				<s:submit 
					method="previousStep"
					type="button" 
					value="Previous" 
					cssClass="btn" 
				/>
				
				<s:submit 
					method="nextStep" 
					value="Save & Next" 
					cssClass="btn success" 
				/>
			</li>
		</ul>
	</s:form>
</div>