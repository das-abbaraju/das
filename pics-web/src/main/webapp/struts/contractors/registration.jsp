<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="mibew_language_code" value="getText('Mibew.LanguageCode')"/>

<s:url value="https://chat.picsorganizer.com/client.php" var="mibew_href">
    <s:param name="locale">${mibew_language_code}</s:param>
    <s:param name="style">PICS</s:param>
    <s:param name="name">${User.name}</s:param>
    <s:param name="email">${User.email}</s:param>
    <s:param name="url">${requestURL}</s:param>
    <s:param name="referrer">${referer}</s:param>
</s:url>


<title><s:text name="ContractorRegistration.title" /></title>

<s:if test="contractor.country.isoCode != ''">
	<s:set var="country_iso_code" value="contractor.country.isoCode" />
</s:if>
<s:else>
	<s:set var="country_iso_code" value="'US'" />
</s:else>

<s:set name="chat_url" value="%{chatUrl}"></s:set>

<s:if test="hasActionErrors()">
	<s:actionerror cssClass="action-error alert-message error" />
</s:if>

<div class="registration-header">
	<section>
		<header>
			<h1><s:text name="Registration.BecomeAMember" /></h1>
		</header>
	</section>
</div>

<div class="registration">
    <s:if test="isLocalhostEnvironment() || isAlphaEnvironment()">
        <a class="btn" id="autofill">Autofill</a>
    </s:if>
	<aside class="registration-side-bar">
		<div class="info-join">
			<section>
				<h1><s:text name="Registration.JoinInfo" /></h1>
			</section>
		</div>

		<div class="info-qualify">
			<section>
				<h1><s:text name="Registration.Qualify" /></h1>
				<ul>
					<li>
						<s:text name="Registration.CountryCount" />
					</li>
					<li>
						<s:text name="Registration.OperatorCount" />
					</li>
					<li>
						<s:text name="Registration.UserCount" />
					</li>
				</ul>
			</section>
		</div>

		<div class="info-choice">
			<section>
				<h1><s:text name="Registration.Why" /></h1>
				<ul>
					<li>
						<s:text name="Registration.Reason1" />
					</li>
					<li>
						<s:text name="Registration.Reason2" />
					</li>
					<li>
						<s:text name="Registration.Reason3" />
					</li>
					<li>
						<s:text name="Registration.Reason4" />
					</li>
					<li>
						<s:text name="Registration.Reason5" />
					</li>
					<li>
						<s:text name="Registration.Reason6" />
					</li>
					<li>
						<s:text name="Registration.Reason7" />
					</li>
				</ul>
			</section>
		</div>
	</aside>

	<s:form cssClass="registration-form" theme="pics" method="POST">
		<s:hidden name="requestID" />
		<s:if test="contractor.status.requested">
			<s:hidden name="contractor" id="requested_contractor" />
			<s:hidden name="user" />
			<s:hidden name="registrationKey" />
		</s:if>

		<div class="company-information">
			<section>
				<h1>
					<span><s:text name="Number.1" /></span>
					<s:text name="Registration.CompanyInformation" />
				</h1>

				<ul>
		            <li>
		                <s:select
		                    label="User.locale"
		                    list="supportedLanguages.visibleLanguagesSansDialect"
		                    listKey="key"
		                    listValue="value"
		                    name="language"
		                    value="language"
		                    id="registration_language"
		                    cssClass="select2Min"
		                />
		            </li>
		            <li id="registration_dialect">
		                <s:include value="/struts/contractors/_registration-dialects.jsp" />
		            </li>
		            <li class="country">
		                <s:select
		                    list="countryList"
		                    cssClass="select2 contractor-country"
		                    name="contractor.country.isoCode"
		                    listKey="isoCode"
		                    listValue="name"
		                />
		            </li>
		            <li class="timezone">
		                <label for="contractor_timezone"><s:text name="global.timezone" /></label>
		                <input class="timezone_input" name="contractor.timezone" data-placeholder="<s:text name='Timezone.list.select.header' />"/>
		                <s:hidden id="registration_requested_timezone" name="contractor.timezone" />
		            </li>
		            <li>
						<s:textfield name="contractor.name" />
					</li>
		            <li class="contractor-name-duplicate">
		            	<div class="alert-message warning">
		            		<p>
		            			<s:text name="Registration.Error.AlreadyStarted" /><br/>
		            			<s:text name="Registration.Error.PickUp" />
		            		</p>
		            		<p>
		            			<span class="icon warn"></span><s:text name="Registration.Error.PersonalizedHelp" />
		            			<ul>
		            				<li>
		            					<s:text name="Registration.Error.CallUs" />
		            					<span class="phone pics_phone_number" title="United States">${salesPhoneNumber}</span>
		            				<li>
		                                <a class="chat-link" href="${mibew_href}" target="_blank"><s:text name="Header.Chat" /></a>
		                                <s:text name="Registration.Error.PicsRep" />
		                            </li>
		            			</ul>
		            		</p>
		            	</div>
		            </li>

					<%-- Hack to override company address for UK and AU (Step 2 of the Rule of Three) --%>
					<div id="company_address_fields">
						<s:if test="#country_iso_code == 'GB'">
                            <s:include value="/struts/contractors/united-kingdom/_registration-company-address.jsp">
                                <s:param name="country_iso_code" value="#country_iso_code" />
                            </s:include>
						</s:if>
                        <s:elseif test="#country_iso_code == 'AU'">
                            <s:include value="/struts/contractors/australia/_registration-company-address.jsp">
                                <s:param name="country_iso_code" value="#country_iso_code" />
                                <s:param name="country_subdivision_iso_code" value="countrySubdivision.isoCode" />
                            </s:include>
                        </s:elseif>
						<s:else>
                            <s:include value="/struts/contractors/_registration-company-address.jsp">
                            	<s:param name="country_subdivision_iso_code" value="countrySubdivision.isoCode" />
                            </s:include>
						</s:else>
					</div>
					<%-- End hack --%>

					<li id="tax_id" class="${tax_id_class}">
						<s:textfield label="taxIdLabel" name="contractor.vatId" />
					</li>
 		        </ul>
			</section>
		</div>
		<div class="contact-information">
			<section>
				<h1>
					<span><s:text name="Number.2" /></span>
					<s:text name="Registration.ContactInformation" />
				</h1>

				<ul>
                    <li>
                        <s:textfield name="user.firstName" />
                    </li>
                    <li>
                        <s:textfield name="user.lastName" />
					</li>
					<li>
						<s:textfield name="user.email" />
					</li>
					<li>
						<s:textfield name="user.phone" />
					</li>
				</ul>
			</section>
		</div>

		<div class="account-information">
			<section>
				<h1>
					<span><s:text name="Number.3" /></span>
					<s:text name="Registration.AccountInformation" />
				</h1>

				<ul>
					<li>
						<s:textfield name="user.username" label="global.Username" autocomplete="off" />
				 	</li>
					<li>
						<s:password name="user.password" label="global.Password" autocomplete="off"  />
					</li>
					<li>
						<s:password name="confirmPassword" label="global.ConfirmPassword" autocomplete="off" />
					</li>
				</ul>
			</section>
		</div>

		<div class="actions">
			<div class="info-agreement">
			</div>

			<s:submit
				method="createAccount"
				key="button.GetStarted"
				cssClass="btn success"
			/>

			<div class="modal hide fade">
				<div class="modal-header">
					<a href="#" class="close">×</a>
					<h3><s:text name="Registration.ModalHeading" /></h3>
				</div>
				<div class="modal-body">
					<p><s:text name="Registration.ModalBody" /></p>
				</div>
				<div class="modal-footer">

				</div>
			</div>

			<br style="clear: both" />
		</div>
	</s:form>

	<br style="clear: both" />
</div>
