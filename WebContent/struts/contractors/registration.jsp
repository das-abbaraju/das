<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- toggle display flags for form display --%>
<s:if test="contractor.country.isoCode == 'AE'">
	<s:set var="zip_display" value="'display: none;'" />
</s:if>
<s:else>
	<s:set var="zip_display" value="''" />
</s:else>

<s:if test="contractor.country.isoCode != ''">
	<s:set var="country_value" value="contractor.country.isoCode" />
</s:if>
<s:else>
	<s:set var="country_value" value="'US'" />
</s:else>

<s:if test="getStateList(#country_value).size == 0">
	<s:set var="state_display" value="'display: none;'" />
</s:if>
<s:else>
	<s:set var="state_display" value="''" />
</s:else>

<s:if test="contractor.country.isoCode == 'CA'">
	<s:set var="state_label_display" value="%{getText('global.Province')}" />
</s:if>
<s:elseif test="contractor.country.isoCode == 'GB'">
	<s:set var="state_label_display" value="%{getText('global.Country')}" />
</s:elseif>
<s:else>
	<s:set var="state_label_display" value="%{getText('global.State')}" />
</s:else>

<div class="registration-header">
	<section>
		<header>
			<h1><s:text name="Registration.BecomeAMember" /></h1>
		</header>
	</section>
</div>
					
<div class="registration">
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
		<div class="company-information">
			<section>
				<h1>
					<span>1</span>
					<s:text name="Registration.CompanyInformation" />
				</h1>
				
				<ul>
					<li>
						<s:textfield name="contractor.name" />
					</li>
					<li class="country">
						<s:select 
							list="countryList"
							cssClass="contractor-country"
							name="contractor.country.isoCode"
							listKey="isoCode"
							listValue="name"
						/>
					</li>
					<li class="address">
						<s:textfield name="contractor.address" />
					</li>
					<li class="city">
						<s:textfield name="contractor.city" />
					</li>
					<li class="state" style="${state_display}">
						<s:select 
							label="%{#state_label_display}"
							list="getStateList(#country_value)"
							cssClass="contractor-state"
							name="state" 
							listKey="isoCode" 
							listValue="simpleName" 
						/>
					</li>
					<li class="zip" style="${zip_display}">
						<s:textfield name="contractor.zip" />
					</li>
				</ul>
			</section>
		</div>
		
		<div class="contact-information">
			<section>
				<h1>
					<span>2</span>
					<s:text name="Registration.ContactInformation" />
				</h1>
				
				<ul>
					<li>
						<s:textfield name="user.name" />
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
					<span>3</span>
					<s:text name="Registration.AccountInformation" />
				</h1>
				
				<ul>
					<li>
						<s:textfield name="user.username" autocomplete="off" />
				 	</li>
					<li>
						<s:password name="user.password" label="global.Password" autocomplete="off" />
					</li>
					<li>
						<s:password name="confirmPassword" label="global.ConfirmPassword" autocomplete="off" />
					</li>
				</ul>
			</section>
		</div>
		
		<div class="actions">
			<div class="info-agreement">
				<p>
					<s:text name="Registration.AgreeTC" />
				</p>
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