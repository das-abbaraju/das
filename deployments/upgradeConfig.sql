-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-3803
update audit_question set uniqueCode='policyExpirationDatePlus120' where id = 2952

-- PICS-3428 Product Assessment Questions
update audit_question set expirationDate=CURDATE() where id in (7662, 7663, 7678);
update audit_question set optionID=169 where id=7679;
update audit_question set optionID=170 where id=2444;
update app_translation set msgValue='What amount of risk do your services place on the health & safety of your employees?' where locale='en' and msgKey='AuditQuestion.2444.name';

-- PICS-3746
DELETE from app_translation where msgKey = 'ContractorAccount.city.fieldhelp';

-- PICS-3747
DELETE from app_translation where msgKey = 'ContractorAccount.zip.fieldhelp';

-- PICS-3749
DELETE from app_translation where msgKey = 'User.email.fieldhelp';

-- PICS-3750
DELETE from app_translation where msgKey = 'User.phone.fieldhelp';

-- PICS-3753
UPDATE app_translation SET msgValue = "<p>
	This is the web site of <b>PICS</b>.
</p>
<p>
	Our postal address is <br />
	<b>P.O. Box 51387<br />Irvine, CA 92619-1387</b><br />
	USA
</p>
<p>
	We can be reached via e-mail at <a href=\"mailto:info@picsauditing.com\">info@picsauditing.com</a><br />
	or you can reach us by telephone at {0}
</p>

<h2>DISCLAIMER AND INDEMNITY</h2>

<p>
	As the duly authorized representative of your company that has been delegated the task of filling out the requested
	forms, you agree that your company will defend, indemnify and hold PICS harmless from any claim, loss, liability or
	expense that PICS may incur relating to your company's participation in the PICS program, including any claim, loss,
	liability or expense made by a third party accessing the information provided by your company. The sole exception to the
	foregoing disclaimer and indemnity shall be any claim, loss, liability or expense that is attributable to the gross
	negligence or willful misconduct of PICS. Without limiting the foregoing, your company agrees that PICS shall, in no event
	or circumstance, be liable for any loss or damage, direct, indirect or consequential, that your company may incur arising
	from or related in any way to the PICS audit and/or the use of the PICS website.
</p>
<p>
	While PICS will endeavor to accurately record the information received from you into your company's account appearing
	on the PICS website, PICS shall have no responsibility for any claim, loss, liability or expense, direct, indirect or
	consequential, arising out of or related to any errors or omissions in the information contained in the online PICS audit
	for your company. Upon logging in or by request, your company will be provided a completed copy of any audit that PICS
	has performed on your company as it will appear on PICS's online website. It shall be your responsibility to review the
	information contained in the completed audits for your company and to verify that the information contained in the
	audit form is and remains complete and accurate. You further agree to promptly notify PICS of any errors or omissions in
	the information contained in the PICS website.
</p>
<p>
	Listing on the PICS website does not guarantee acceptance of work from any client. It is understood these audits are for
the purpose of gathering the required documentation by PICS for review by potential clients. PICS is not responsible for
record retention. Unless specifically requested in writing to have your documentation returned, PICS has the right to
dispose of the records.
</p>

<h2>USER AGREEMENT</h2>

<p>
	You will be issued a username and password to access your company's inputed data. It is your responsibility to keep
	this username and password confidential and private. PICS will not share your username and password data with any
	third party, and regardless of circumstances you cannot share your username and/or password with a third party with
	the sole exception that the third party is contractually acting as your agent to assist you through the PICS process. Any
	violation of this user agreement will result in your account being either temporarily or permanently deactivated.
</p>

<h2>PRIVACY POLICY</h2>

<p>
	PICS' privacy policy can be accessed here as well as from the payment option page <a href=\"http://www.picsorganizer.com/PrivacyPolicy.action\">(http://www.picsorganizer.com/PrivacyPolicy.action)</a>.
</p>

<h2>CONFIDENTIALITY</h2>
	
<p>
	In addition to the linked privacy policy, PICS recognizes the expected confidentiality and sensitivity of the data that is
	entered through the PICS website.
</p>
<p>
	Sensitive data is defined as the following: your company's Prequalification Form (PQF), specific Insurance information,
	any and all data gathered during an audit (including but not limited to a Desktop Audit, Office Audit, Field Audit, Integrity
	Management Audit, etc), EMR and OSHA data. Non-sensitive data is defined as your company's name, description of
	services, which states you operate in and have offices in, contact information of your company's listed main point of
	contact, your company's logo.
</p>
<p>
	PICS is a password-protected website. Your company's Sensitive and Non-sensitive data will be held behind this
	password protection.
</p>
<p>
	Sensitive Data will be shared only with authorized users from operators appearing on your \"facility list\" which are linked
	to your account. You control which operators are linked to your account and can review any time by logging in to your
	account. At any time you can add or remove any operator from the facility list by either calling in to Customer Service
	({0}) or controlling it using the web interface. Any user from an operator not listed on your facility can not
	access your Sensitive Data. Other Contractors do not have access to your Sensitive Data.
</p>
<p>
	Non-Sensitive Data will be used to assist Operators who are searching for potential bidders. Only Operators listed on the
	PICS Facility list will be able to view this Non-Sensitive Data. In other words, even your Non-Sensitive Data is not public
	data, but is password protected and shared only with PICS consortium operator members.
</p>
<p>
	In an effort to help you gain more work with PICS consortium operator members, authorized users can add your
	contractor account to the approved contractor list of the operator. This generates a notification email to your primary
	user, stating which operator added your company and when. If you do not wish to associate your account with that
	operator, you may remove your account from their approved contractor list by contacting PICS Customer Service, in
	which case the Operator will be notified that your company requested to be removed from the Operator's approved
	contractor list.
</p>

<h2>REFUND POLICY</h2>

<p>
	Your account will be automatically renewed each 12 months unless you call to cancel your account.  In the event of an operator-requested upgrade, your primary user will be notified via email immediately.  The invoice will be created three days after the upgrade event and the upgrade charge will occur 7 days later. PICS refund policy can be accessed here as well from as the payment options page <a href="http://www.picsorganizer.com/RefundPolicy.action">(http://www.picsorganizer.com/RefundPolicy.action)</a>
</p>
<p>
	Our annual pricing can be seen on the facilities page, which you will be able to review before completing registration.
</p>

<h2>AGREEMENTS OUTSIDE OF THIS DOCUMENT</h2>

<p>
	You agree to the terms and conditions of the contractor agreement as communicated in this document.  Any agreements outside of the terms and conditions contained herein shall be agreed to in writing and signed by both contractor and PICS representatives.  PICS copy of said agreement will be held in the PICS UPLOAD FILES category of the PQF under PICS Contractor Agreement.
</p>" WHERE msgKey = 'ContractorAgreement.content';

-- registration/services performed -- rbeaini
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.BecomeAMember", "en", "Become a Member of PICS Today!");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.TheContractorsChoice", "en", "The Contractors' Choice.");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.CompanyInformation", "en", "Company Information");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.ContactInformation", "en", "Contact Information");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.AccountInformation", "en", "Account Information");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.AgreeTC", "en", "By clicking Get Started, you agree to the terms and<br /> conditions of the <a href=\"#\" class=\"contractor-agreement modal-link\" data-title=\"Contractor Agreement\" data-url=\"ContractorAgreement.action\">PICS Contractor Agreement</a>.");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.ModalHeading", "en", "Modal Heading");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.ModalBody", "en", "One fine body…");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.NeedHelp", "en", "Need help signing up?");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Questions", "en", "If you have any questions please email us at <a href=\"#\">support@picsauditing.com</a>,  call us at (800) 506-PICS (7427), or chat with one of our friendly representatives.");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.JoinInfo", "en", "You're a few clicks away from<br />joining the fastest-growing<br />contractor network in the world.");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Qualify", "en", "Qualify for<br />Work in PICS");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.CountryCount", "en", "16 Countries");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.OperatorCount", "en", "850 Client/Operators");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.UserCount", "en", "50,000 Active Users");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Why", "en", "Why Contractors<br />Choose PICS?");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Reason1", "en", "Prequalify for work");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Reason2", "en", "Exposure to more clients");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Reason3", "en", "Lower insurance costs");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Reason4", "en", "Marketing for your business");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Reason5", "en", "Direct access to Safety Professionals");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Reason6", "en", "Simple, powerful software technology");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.Reason7", "en", "Expert Customer Service");
INSERT into app_translation (msgKey, locale, msgValue) values ("Registration.PICSCompanies", "en", "Companies that use PICS");
--
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationAddClientSite.Select", "en", "Select the client sites you want to work with:");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationAddClientSite.Selected", "en", "Selected client sites:");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationAddClientSite.Invitation", "en", "Get your invitation!");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationAddClientSite.Requested", "en", "Add the client site that requested you.");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationAddClientSite.FindSites", "en", "Find others client sites");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationAddClientSite.OtherSites", "en", "Select any other client sites that you are or will be working for.");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationAddClientSite.AddClientSites", "en", "Add Client Sites");
--
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationServiceEvaluation.ServicesPerformed", "en", "Services Performed");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationServiceEvaluation.SelectServices", "en", "Please select <i>all</i> services that your company performs. It is common to select more than one.");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationServiceEvaluation.SoleProprietor", "en", "Are you a sole proprietor with no employees? (A sole proprietor means you do not employee anyone and do not pay any employees (this includes administrative help))");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationServiceEvaluation.BidOnly", "en", "Are you bid only?");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationServiceEvaluation.ServiceSafety", "en", "Service Safety Evaluation");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationServiceEvaluation.ProductSafety", "en", "Product and Safety Evaluation");
--
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.AnnualMembership", "en", "Annual Membership");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.Price", "en", "Price");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.Total", "en", "Total");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.OneTimeFees", "en", "One Time Fees");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.View Pricing", "en", "View Pricing");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.PrivacyPolicy", "en", "Privacy Policy");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.RefundPolicy", "en", "Refund Policy");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.ContractorAgreement", "en", "Contractor Agreement");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.PrintTest", "en", "Print Test");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.ModalHeading", "en", "Modal Heading");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.ModalBody", "en", "One fine body…");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.DocuGUARD", "en", "DocuGUARD");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.MembershipHelp", "en", "A PICS Saftey Professional will personally review your prequalification information, as requested by the facilities you work with.  This information will be communicated back and, based on the results, you will be approved for work at, or with this location.");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.BillingInformation", "en", "Billing Information");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.ExpirationDate", "en", "Expiration Date");
INSERT into app_translation (msgKey, locale, msgValue) values ("RegistrationMakePayment.CreditCardNote", "en", "Note: Your credit card will be kept on file and used for any upgrades or renewals.  We will notify the primary user via email 30 days before any changes occur for renewals and 7 days before any charge occurs for upgrades.  If you chose to deactivate your account, please call us at 800-506-PICS.");
-- credit card response codes
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.1000", "en", "Approved");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.1001", "en", "Approved, check customer ID");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.1002", "en", "Processed - This code will be assigned to all credits and voice authorizations. These types of transactions do not need to be authorized they are immediately submitted for settlement.");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2000", "en", "Do Not Honor");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2001", "en", "Insufficient Funds");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2002", "en", "Limit Exceeded");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2003", "en", "Cardholder's Activity Limit Exceeded");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2004", "en", "Expired Card");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2005", "en", "Invalid Credit Card Number");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2006", "en", "Invalid Expiration Date");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2007", "en", "No Account");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2008", "en", "Card Account Length Error");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2009", "en", "No Such Issuer");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2010", "en", "Card Issuer Declined CVV");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2011", "en", "Voice Authorization Required");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2012", "en", "Voice Authorization Required - Possible Lost Card");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2013", "en", "Voice Authorization Required - Possible Stolen Card");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2014", "en", "Voice Authorization Required - Fraud Suspected");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2015", "en", "Transaction Not Allowed");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2016", "en", "Duplicate Transaction");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2017", "en", "Cardholder Stopped Billing");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2018", "en", "Cardholder Stopped All Billing");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2019", "en", "Invalid Transaction");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2020", "en", "Violation");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2021", "en", "Security Violation");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2022", "en", "Declined - Updated Cardholder Available");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2023", "en", "Processor Does Not Support This Feature");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2024", "en", "Card Type Not Enabled");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2025", "en", "Set Up Error - Merchant");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2026", "en", "Invalid Merchant ID");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2027", "en", "Set Up Error - Amount");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2028", "en", "Set Up Error - Hierarchy");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2029", "en", "Set Up Error - Card");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2030", "en", "Set Up Error - Terminal");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2031", "en", "Encryption Error");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2032", "en", "Surcharge Not Permitted");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2033", "en", "Inconsistent Data");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2034", "en", "No Action Taken");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2035", "en", "Partial Approval For Amount In Group III Version");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2036", "en", "Authorization could not be found to reverse");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2037", "en", "Already Reversed");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2038", "en", "Processor Declined");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2039", "en", "Invalid Authorization Code");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2040", "en", "Invalid Store");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2041", "en", "Declined - Call For Approval");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2043", "en", "Error - Do Not Retry, Call Issuer");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2044", "en", "Declined - Call Issuer");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2045", "en", "Invalid Merchant Number");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2046", "en", "Declined");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2047", "en", "Call Issuer. Pick Up Card.");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2048", "en", "Invalid Amount");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2049", "en", "Invalid SKU Number");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2050", "en", "Invalid Credit Plan");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2051", "en", "Credit Card Number does not match method of payment");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2052", "en", "Invalid Level III Purchase");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2053", "en", "Card reported as lost or stolen");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2054", "en", "Reversal amount does not match authorization amount");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2055", "en", "Invalid Transaction Division Number");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2056", "en", "Transaction amount exceeds the transaction division limit");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2057", "en", "Issuer or Cardholder has put a restriction on the card");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2058", "en", "Merchant not MasterCard SecureCode enabled.");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2059", "en", "Address Verification Failed");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.2060", "en", "Address Verification and Card Security Code Failed");
INSERT into app_translation (msgKey, locale, msgValue) values ("CreditCard.ResponseCode.3000", "en", "Processor network unavailable.Try Again");
-- --------------------------------------------------------