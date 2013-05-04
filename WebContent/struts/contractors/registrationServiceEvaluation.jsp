<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.actions.contractors.RegistrationServiceEvaluation" %>

<title><s:text name="ContractorRegistration.title" /></title>

<s:set var="requires_ssip_evaluation" value="shouldShowSsip()" />

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

<s:if test="!isStringEmpty(servicesHelpText)">
	<div class="alert-error alert-message warning">
		<span class="icon warn"></span>

		<p>
			<s:property value="servicesHelpText" escape="false" />
		</p>
	</div>
</s:if>

<div class="service-evaluation">
    <s:if test="!isLiveEnvironment()" >
        <a class="btn" id="autofill">Autofill</a>
    </s:if>

	<s:form cssClass="service-evaluation-form" theme="pics">

        <s:if test="#requires_ssip_evaluation == true">
            <section>
                <h1><s:text name="RegistrationServiceEvaluation.ssip.evaluationHeading" /></h1>
                <ul>
                    <li>
                        <label><s:text name="RegistrationServiceEvaluation.ssip.registeredWithMemberScheme" /></label>
                        <ul class="radio registered-with-ssip-member-scheme-input inline">
                            <li>
                                <input type="radio" id="registeredWithSsipMemberScheme" name="answerMap[<%=RegistrationServiceEvaluation.QUESTION_ID_REGISTERED_WITH_SSIP%>].answer" value="Yes" />
                                <label>
                                    <s:text name="YesNo.Yes" />
                                </label>
                            </li>
                            <li>
                                <input type="radio" name="answerMap[<%=RegistrationServiceEvaluation.QUESTION_ID_REGISTERED_WITH_SSIP%>].answer" value="No" />
                                <label>
                                    <s:text name="YesNo.No" />
                                </label>
                            </li>
                        </ul>
                    </li>
                    <li class="request-to-provide-ssip-details-container">
                        <label><s:text name="RegistrationServiceEvaluation.ssip.requestToProvideSsipDetails" /></label>
                        <ul class="radio request-to-provide-ssip-details-input inline">
                            <li>
                                <input type="radio" name="readyToProvideSsipDetails" value="Yes" />
                                <label>
                                    <s:text name="YesNo.Yes" />
                                </label>
                            </li>
                            <li>
                                <input type="radio" name="readyToProvideSsipDetails" value="No" />
                                <label>
                                    <s:text name="YesNo.No" />
                                </label>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <div class="provide-ssip-details-later-message right-column">
                            <s:text name="RegistrationServiceEvaluation.ssip.provideSsipDetailsLaterMessage" />
                        </div>
                    </li>
                    <li class="ssip-details-container" style="display: none;">
                        <ul>
                            <li>
                                <label><s:text name="RegistrationServiceEvaluation.ssip.dateOfLastAudit" /></label>
                                <ul class="text-input-list">
                                    <li>
                                        <input type="text" name="yearOfLastSsipMemberAudit" placeholder="YYYY" maxlength="4" class="year">
                                    </li>
                                    <li>
                                        <input type="text" name="monthOfLastSsipMemberAudit" placeholder="MM" maxlength="2" class="month">
                                    </li>
                                    <li>
                                        <input type="text" name="dayOfLastSsipMemberAudit" placeholder="DD" maxlength="2" class="day">
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <label><s:text name="RegistrationServiceEvaluation.ssip.dateOfMembershipExpiration" /></label>
                                <ul class="text-input-list">
                                    <li>
                                        <input type="text" name="yearOfSsipMembershipExpiration" placeholder="YYYY" maxlength="4" class="year">
                                    </li>
                                    <li>
                                        <input type="text" name="monthOfSsipMembershipExpiration" placeholder="MM" maxlength="2" class="month">
                                    </li>
                                    <li>
                                        <input type="text" name="dayOfSsipMembershipExpiration" placeholder="DD" maxlength="2" class="day">
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <label><s:text name="RegistrationServiceEvaluation.ssip.whichMemberScheme" /></label>
                                <div class="right-column">
                                    <select name="ssipAnswerMap[<%=RegistrationServiceEvaluation.QUESTION_ID_SSIP_SCHEME%>].answer">
                                        <option>
                                            - <s:text name="RegistrationServiceEvaluation.ssip.selectYourScheme" /> -
                                        </option>
                                        <s:iterator var="member_scheme" value="ssipMemberSchemes">
                                            <option value="${member_scheme.id}">
                                                ${member_scheme.name}
                                            </option>
                                        </s:iterator>
                                    </select>
                                </div>
                            </li>
                        </ul>
                    </li>
                </ul>
            </section>
        </s:if>

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
						name="soleProprietor"
						value="contractor.soleProprietor"
						list="#{'true':getTextNullSafe('YesNo.Yes'), 'false':getTextNullSafe('YesNo.No')}"
						cssClass="inline"
					/>
				</li>

				<s:if test="showBidOnly">
					<li>
						<label><s:text name="RegistrationServiceEvaluation.BidOnly" /></label>
						<s:radio
							name="bidOnly"
							value="contractor.getAccountLevel().isBidOnly()"
							list="#{'true':getTextNullSafe('YesNo.Yes'), 'false':getTextNullSafe('YesNo.No')}"
							cssClass="inline"
						/>
					</li>
				</s:if>
			</ul>
		</section>

		<div class="service-safety-evaluation">

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

		<%-- business interruption evaluation display toggle --%>
		<s:if test="contractor.materialSupplier">
			<s:set var="business_interruption_evaluation_display" value="" />
		</s:if>
		<s:else>
			<s:set var="business_interruption_evaluation_display" value="'display: none;'" />
		</s:else>

		<div class="business_interruption_evaluation" style="${business_interruption_evaluation_display}">

			<div class="separator"></div>

			<section>
				<h1><s:text name="RegistrationServiceEvaluation.BusinessInterruption" /></h1>

				<ul>
					<s:iterator value="infoQuestions" var="question">
						<s:if test="#question.category.id == 1682 && #question.questionType=='MultipleChoice'">
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

		<%-- transportation evaluation display toggle --%>
        <s:if test="hasTransportationQuestions">
            <s:if test="contractor.transportationServices">
                <s:set var="transportation_evaluation_display" value="" />
            </s:if>
            <s:else>
                <s:set var="transportation_evaluation_display" value="'display: none;'" />
            </s:else>

            <div class="transportation_evaluation" style="${transportation_evaluation_display}">

                <div class="separator"></div>

                <section>
                    <s:set var="transportation_category_id" value="%{@com.picsauditing.jpa.entities.AuditCategory@TRANSPORTATION_SAFETY_EVAL}" />
                    <h1><s:text name="%{'AuditCategory.' + #transportation_category_id + '.name'}" /></h1>

                    <ul>
                        <s:iterator value="infoQuestions" var="question">
                            <s:if test="#question.category.id == #transportation_category_id && #question.questionType=='MultipleChoice'">
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
        </s:if>

		<ul>
			<li class="actions">
				<s:submit
					method="previousStep"
					type="button"
					key="button.Previous"
					cssClass="btn"
				/>

				<s:submit
					method="nextStep"
					key="button.SaveNext"
					cssClass="btn success"
				/>
			</li>
		</ul>
	</s:form>
</div>