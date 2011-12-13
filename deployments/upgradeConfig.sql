-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-3414 removing spaces from the msgKey in these translations
update app_translation set msgKey = "ContractorEdit.error.BrochureFormat" where msgKey = "ContractorEdit.error.Brochure Format";

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
UPDATE app_translation SET msgValue = '<p>
	This is the web site of <b>PICS</b>.
</p>
<p>
	Our postal address is <br />
	<b>P.O. Box 51387<br />Irvine, CA 92619-1387</b><br />
	USA
</p>
<p>
	We can be reached via e-mail at <a href="mailto:info@picsauditing.com">info@picsauditing.com</a><br />
	or you can reach us by telephone at {0}
</p>

<h2>DISCLAIMER AND INDEMNITY</h2>

<p>
	As the duly authorized representative of your company that has been delegated the task of filling out the requested
	forms, you agree that your company will defend, indemnify and hold PICS harmless from any claim, loss, liability or
	expense that PICS may incur relating to your company''s participation in the PICS program, including any claim, loss,
	liability or expense made by a third party accessing the information provided by your company. The sole exception to the
	foregoing disclaimer and indemnity shall be any claim, loss, liability or expense that is attributable to the gross
	negligence or willful misconduct of PICS. Without limiting the foregoing, your company agrees that PICS shall, in no event
	or circumstance, be liable for any loss or damage, direct, indirect or consequential, that your company may incur arising
	from or related in any way to the PICS audit and/or the use of the PICS website.
</p>
<p>
	While PICS will endeavor to accurately record the information received from you into your company''s account appearing
	on the PICS website, PICS shall have no responsibility for any claim, loss, liability or expense, direct, indirect or
	consequential, arising out of or related to any errors or omissions in the information contained in the online PICS audit
	for your company. Upon logging in or by request, your company will be provided a completed copy of any audit that PICS
	has performed on your company as it will appear on PICS''s online website. It shall be your responsibility to review the
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
	You will be issued a username and password to access your company''s inputed data. It is your responsibility to keep
	this username and password confidential and private. PICS will not share your username and password data with any
	third party, and regardless of circumstances you cannot share your username and/or password with a third party with
	the sole exception that the third party is contractually acting as your agent to assist you through the PICS process. Any
	violation of this user agreement will result in your account being either temporarily or permanently deactivated.
</p>

<h2>PRIVACY POLICY</h2>

<p>
	PICS'' privacy policy can be accessed here as well as from the payment option page <a href="http://www.picsorganizer.com/PrivacyPolicy.action">(http://www.picsorganizer.com/PrivacyPolicy.action)</a>.
</p>

<h2>CONFIDENTIALITY</h2>
	
<p>
	In addition to the linked privacy policy, PICS recognizes the expected confidentiality and sensitivity of the data that is
	entered through the PICS website.
</p>
<p>
	Sensitive data is defined as the following: your company''s Prequalification Form (PQF), specific Insurance information,
	any and all data gathered during an audit (including but not limited to a Desktop Audit, Office Audit, Field Audit, Integrity
	Management Audit, etc), EMR and OSHA data. Non-sensitive data is defined as your company''s name, description of
	services, which states you operate in and have offices in, contact information of your company''s listed main point of
	contact, your company''s logo.
</p>
<p>
	PICS is a password-protected website. Your company''s Sensitive and Non-sensitive data will be held behind this
	password protection.
</p>
<p>
	Sensitive Data will be shared only with authorized users from operators appearing on your "facility list" which are linked
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
	which case the Operator will be notified that your company requested to be removed from the Operator''s approved
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
</p>' 
WHERE msgKey = 'ContractorAgreement.content'
and locale = 'en';

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
-- 
INSERT into app_translation (msgKey, locale, msgValue) values ("ContractorTrades.NeedsUpdating", "en", "Please select all applicable trades that apply to your company. Completing this portion of the process is very important as it will help ensure the proper risk ranking and will also allow existing/new clients to find you via your selected trades");
--
UPDATE app_translation set msgValue='Does your company perform services that require the operation of equipment and/or machinery at your client(s) facility? ' where locale='en' and msgKey='AuditQuestion.2442.name';
UPDATE audit_question set optionID = 170 where id = 2444;
UPDATE audit_question set optionID = 169 where id = 7679;
-- --------------------------------------------------------
-- Updating timezones and countries
update app_translation set msgValue = 'Heure du Pacifique (US / Canada)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.US.Pacific' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été Magadan (Asie)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Magadan' AND locale = 'fr';
update app_translation set msgValue = 'GMT-12: 00 (GMT +12)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Etc.GMT+12' AND locale = 'fr';
update app_translation set msgValue = 'Samoa Heure Normale (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Samoa' AND locale = 'fr';
update app_translation set msgValue = 'Hawaï Standard Time (Honolulu)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Honolulu' AND locale = 'fr';
update app_translation set msgValue = 'Temps Tahiti (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Tahiti' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée de Hawaii Aléoutiennes (Etats-Unis)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.US.Aleutian' AND locale = 'fr';
update app_translation set msgValue = 'Alaska Heure Normale (YST9)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.SystemV.YST9' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée de l''Alaska (États-Unis)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.US.Alaska' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale des Rocheuses (Arizona)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.US.Arizona' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée des Rocheuses (USA / Canada)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.US.Mountain' AND locale = 'fr';
update app_translation set msgValue = 'Heure Normale du Centre (Saskatchewan)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Canada.Saskatchewan' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée du Centre (Bahia_Banderas)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.Bahia_Banderas' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée du Centre (US / Canada)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.US.Central' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de l''est (Port-au-Prince)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.Port-au-Prince' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée de l''est(US / Canada)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.US.Eastern' AND locale = 'fr';
update app_translation set msgValue = 'Heure du Venezuela  (Caracas)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.Caracas' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Bolivie  (La Paz)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.La_Paz' AND locale = 'fr';
update app_translation set msgValue = 'Heure du Chili Temps (Santiago)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.Santiago' AND locale = 'fr';
update app_translation set msgValue = 'Heure d'' Argentine (Buenos Aires)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.Argentina.Buenos_Aires' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Brasilia  (Sao Paulo)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.Sao_Paulo' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée de Terre-Neuve (Canada)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Canada.Newfoundland' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été de l''ouest Groenland (Godthab)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.America.Godthab' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Fernando de Noronha (DeNoronha)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Brazil.DeNoronha' AND locale = 'fr';
update app_translation set msgValue = 'Heure médiane de Greenwich', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Greenwich' AND locale = 'fr';
update app_translation set msgValue = 'Temps universel coordonné (UTC)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Etc.UTC' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été britannique (Londres)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Europe.London' AND locale = 'fr';
update app_translation set msgValue = 'Heure de l''Afrique de l''Ouest (Lagos)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Africa.Lagos' AND locale = 'fr';
update app_translation set msgValue = 'Heure de l''Afrique de l''Ouest (Windhoek)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Africa.Windhoek' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été de l''Europe centrale (Paris)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Europe.Paris' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''Europe (Tripoli)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Africa.Tripoli' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée d''Israël (Jérusalem)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Jerusalem' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale Saoudite (Riyad)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Riyadh' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée de Moscou (Europe)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Europe.Moscow' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée d''Iran (Téhéran)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Tehran' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale du Golfe (Dubaï)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Dubai' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Géorgie (Tbilissi)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Tbilisi' AND locale = 'fr';
update app_translation set msgValue = 'Heure de l''Afghanistan (Kaboul)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Kabul' AND locale = 'fr';
update app_translation set msgValue = 'Heure du Pakistan (Karachi)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Karachi' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de l''Inde (Kolkata)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Kolkata' AND locale = 'fr';
update app_translation set msgValue = 'Heure du Népal (Katmandou)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Kathmandu' AND locale = 'fr';
update app_translation set msgValue = 'Heure du Kirghizstan (Bichkek)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Bishkek' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Alma-Ata (Almaty)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Almaty' AND locale = 'fr';
update app_translation set msgValue = 'Heure du Myanmar (Rangoon)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Rangoon' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''Indochine (Bangkok)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Bangkok' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''Hovd (Asie)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Hovd' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de Chine (Shanghai)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Shanghai' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de l''ouest central (Australie) (Eucla)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Australia.Eucla' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été d''Irkoutsk (Asie)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Irkutsk' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale du Japon (Tokyo)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Tokyo' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale du Centre (Northern Territory) (Darwin)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Australia.Darwin' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale du Centre (South Australia) (Adelaide)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Australia.Adelaide' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de l''est (Queensland) (Brisbane)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Australia.Brisbane' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de l''est (Nouvelle-Galles du Sud) (Sydney)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Australia.Sydney' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de Lord Howe (Lord_Howe)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Australia.Lord_Howe' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Fidji (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Fiji' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de Nouvelle-Zélande (Auckland)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Auckland' AND locale = 'fr';
update app_translation set msgValue = 'Heure des Marquises (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Marquesas' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de Pitcairn', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Pitcairn' AND locale = 'fr';
update app_translation set msgValue = 'Heure des Galapagos (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Galapagos' AND locale = 'fr';
update app_translation set msgValue = 'Heure de l''Île de Pâques (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Easter' AND locale = 'fr';
update app_translation set msgValue = 'Heure avancée de l''Atlantique (Bermudes)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Atlantic.Bermuda' AND locale = 'fr';
update app_translation set msgValue = 'Heure du Cap-Vert (Cape Verde)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Atlantic.Cape_Verde' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été des Açores (Atlantique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Atlantic.Azores' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale d''Inde (Colombo)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Colombo' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été de Yakoutsk (Asie)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Yakutsk' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été de Vladivostok (Asie)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Vladivostok' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Pohnpei (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Pohnpei' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Norfolk (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Norfolk' AND locale = 'fr';
update app_translation set msgValue = 'Heure d''été d''Anadyr (Asie)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Asia.Anadyr' AND locale = 'fr';
update app_translation set msgValue = 'Heure normale de Chatham (Pacifique)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Chatham' AND locale = 'fr';
update app_translation set msgValue = 'Heure de Tonga (Tongatapu)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Tongatapu' AND locale = 'fr';
update app_translation set msgValue = 'Heure de la Ligne Est (Kiritimati)', updatedBy = 24143, updateDate = NOW() WHERE msgKey = 'TimeZone.Pacific.Kiritimati' AND locale = 'fr';
update app_translation set msgValue = 'Pays', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorAccount.country.isoCode' and locale = 'fr';
update app_translation set msgValue = '<p> Le siège social de votre compagnie. Cela confirmera la devise à utiliser pour votre compte PICS. </p>', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorAccount.country.isoCode.fieldhelp' and locale = 'fr';
update app_translation set msgValue = 'Pays de l''entreprise.<br /><b>Ce champ est obligatoire.</b>', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistrationRequest.country.fieldhelp' and locale = 'fr';
update app_translation set msgValue = 'Andorre', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AD' and locale = 'fr';
update app_translation set msgValue = 'Émirats Arabes Unis', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AE' and locale = 'fr';
update app_translation set msgValue = 'Afghanistan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AF' and locale = 'fr';
update app_translation set msgValue = 'Antigua-et-Barbuda', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AG' and locale = 'fr';
update app_translation set msgValue = 'Anguilla', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AI' and locale = 'fr';
update app_translation set msgValue = 'Albanie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AL' and locale = 'fr';
update app_translation set msgValue = 'Arménie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AM' and locale = 'fr';
update app_translation set msgValue = 'Antilles Néerlandaises', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AN' and locale = 'fr';
update app_translation set msgValue = 'Angola', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AO' and locale = 'fr';
update app_translation set msgValue = 'Antarctique', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AQ' and locale = 'fr';
update app_translation set msgValue = 'Argentine', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AR' and locale = 'fr';
update app_translation set msgValue = 'Samoa Américaines', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AS' and locale = 'fr';
update app_translation set msgValue = 'Autriche', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AT' and locale = 'fr';
update app_translation set msgValue = 'Australie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AU' and locale = 'fr';
update app_translation set msgValue = 'Aruba', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AW' and locale = 'fr';
update app_translation set msgValue = 'Îles Åland', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AX' and locale = 'fr';
update app_translation set msgValue = 'Azerbaïdjan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.AZ' and locale = 'fr';
update app_translation set msgValue = 'Bosnie-Hérzégovine', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BA' and locale = 'fr';
update app_translation set msgValue = 'Barbade', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BB' and locale = 'fr';
update app_translation set msgValue = 'Bangladesh', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BD' and locale = 'fr';
update app_translation set msgValue = 'Bélgique', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BE' and locale = 'fr';
update app_translation set msgValue = 'Burkina Faso', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BF' and locale = 'fr';
update app_translation set msgValue = 'Bulgarie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BG' and locale = 'fr';
update app_translation set msgValue = 'Bahreïn', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BH' and locale = 'fr';
update app_translation set msgValue = 'Burundi', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BI' and locale = 'fr';
update app_translation set msgValue = 'Bénin', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BJ' and locale = 'fr';
update app_translation set msgValue = 'Saint-Barthélemy', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BL' and locale = 'fr';
update app_translation set msgValue = 'Bermudes', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BM' and locale = 'fr';
update app_translation set msgValue = 'Brunei Darussalam', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BN' and locale = 'fr';
update app_translation set msgValue = 'Bolivie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BO' and locale = 'fr';
update app_translation set msgValue = 'Brésil', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BR' and locale = 'fr';
update app_translation set msgValue = 'Bahreïn', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BS' and locale = 'fr';
update app_translation set msgValue = 'Bhoutan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BT' and locale = 'fr';
update app_translation set msgValue = 'Ile Bouvet', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BV' and locale = 'fr';
update app_translation set msgValue = 'Botswana', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BW' and locale = 'fr';
update app_translation set msgValue = 'Biélorussie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BY' and locale = 'fr';
update app_translation set msgValue = 'Bélize', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.BZ' and locale = 'fr';
update app_translation set msgValue = 'Canada', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CA' and locale = 'fr';
update app_translation set msgValue = 'Iles Cocos', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CC' and locale = 'fr';
update app_translation set msgValue = 'République Démocratique du Congo', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CD' and locale = 'fr';
update app_translation set msgValue = 'République Centrafricaine', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CF' and locale = 'fr';
update app_translation set msgValue = 'République du Congo', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CG' and locale = 'fr';
update app_translation set msgValue = 'Suisse', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CH' and locale = 'fr';
update app_translation set msgValue = 'Côte d''Ivoire', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CI' and locale = 'fr';
update app_translation set msgValue = 'Iles Cook', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CK' and locale = 'fr';
update app_translation set msgValue = 'Chili', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CL' and locale = 'fr';
update app_translation set msgValue = 'Cameroun', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CM' and locale = 'fr';
update app_translation set msgValue = 'Chine', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CN' and locale = 'fr';
update app_translation set msgValue = 'Colombie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CO' and locale = 'fr';
update app_translation set msgValue = 'Costa Rica', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CR' and locale = 'fr';
update app_translation set msgValue = 'Cuba', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CU' and locale = 'fr';
update app_translation set msgValue = 'Cap-Vert', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CV' and locale = 'fr';
update app_translation set msgValue = 'Ile Christmas', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CX' and locale = 'fr';
update app_translation set msgValue = 'Chypre', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CY' and locale = 'fr';
update app_translation set msgValue = 'République Tchèque', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.CZ' and locale = 'fr';
update app_translation set msgValue = 'Allemagne', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.DE' and locale = 'fr';
update app_translation set msgValue = 'Djibouti', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.DJ' and locale = 'fr';
update app_translation set msgValue = 'Danemark', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.DK' and locale = 'fr';
update app_translation set msgValue = 'Dominique', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.DM' and locale = 'fr';
update app_translation set msgValue = 'République Dominicaine', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.DO' and locale = 'fr';
update app_translation set msgValue = 'Algérie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.DZ' and locale = 'fr';
update app_translation set msgValue = 'Équateur', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.EC' and locale = 'fr';
update app_translation set msgValue = 'Estonie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.EE' and locale = 'fr';
update app_translation set msgValue = 'Égypte', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.EG' and locale = 'fr';
update app_translation set msgValue = 'Sahara Occidental', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.EH' and locale = 'fr';
update app_translation set msgValue = 'Érythrée', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ER' and locale = 'fr';
update app_translation set msgValue = 'Espagne', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ES' and locale = 'fr';
update app_translation set msgValue = 'Éthiopie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ET' and locale = 'fr';
update app_translation set msgValue = 'Finlande', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.FI' and locale = 'fr';
update app_translation set msgValue = 'Fidji', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.FJ' and locale = 'fr';
update app_translation set msgValue = 'Iles Falklands', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.FK' and locale = 'fr';
update app_translation set msgValue = 'Micronésie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.FM' and locale = 'fr';
update app_translation set msgValue = 'Iles Féroé', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.FO' and locale = 'fr';
update app_translation set msgValue = 'France', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.FR' and locale = 'fr';
update app_translation set msgValue = 'Gabon', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GA' and locale = 'fr';
update app_translation set msgValue = 'Royaume-Uni', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GB' and locale = 'fr';
update app_translation set msgValue = 'Grenade', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GD' and locale = 'fr';
update app_translation set msgValue = 'Géorgie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GE' and locale = 'fr';
update app_translation set msgValue = 'Guyane Française', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GF' and locale = 'fr';
update app_translation set msgValue = 'Guernesey', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GG' and locale = 'fr';
update app_translation set msgValue = 'Ghana', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GH' and locale = 'fr';
update app_translation set msgValue = 'Gibraltar', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GI' and locale = 'fr';
update app_translation set msgValue = 'Groenland', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GL' and locale = 'fr';
update app_translation set msgValue = 'Gambie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GM' and locale = 'fr';
update app_translation set msgValue = 'Guinée', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GN' and locale = 'fr';
update app_translation set msgValue = 'Guadeloupe', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GP' and locale = 'fr';
update app_translation set msgValue = 'Guinée Équatoriale', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GQ' and locale = 'fr';
update app_translation set msgValue = 'Grèce', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GR' and locale = 'fr';
update app_translation set msgValue = 'Géorgie du Sud-et-les Îles Sandwich', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GS' and locale = 'fr';
update app_translation set msgValue = 'Guatemala', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GT' and locale = 'fr';
update app_translation set msgValue = 'Guam', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GU' and locale = 'fr';
update app_translation set msgValue = 'Guinée-Bissau', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GW' and locale = 'fr';
update app_translation set msgValue = 'Guyane', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.GY' and locale = 'fr';
update app_translation set msgValue = 'Hong Kong', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.HK' and locale = 'fr';
update app_translation set msgValue = 'Îles Heard et McDonald', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.HM' and locale = 'fr';
update app_translation set msgValue = 'Honduras', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.HN' and locale = 'fr';
update app_translation set msgValue = 'Croatie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.HR' and locale = 'fr';
update app_translation set msgValue = 'Haïti', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.HT' and locale = 'fr';
update app_translation set msgValue = 'Hongrie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.HU' and locale = 'fr';
update app_translation set msgValue = 'Indonésie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ID' and locale = 'fr';
update app_translation set msgValue = 'Irlande', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IE' and locale = 'fr';
update app_translation set msgValue = 'Israël', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IL' and locale = 'fr';
update app_translation set msgValue = 'Île de Man', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IM' and locale = 'fr';
update app_translation set msgValue = 'Inde', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IN' and locale = 'fr';
update app_translation set msgValue = 'Territoire britannique de l''océan Indien', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IO' and locale = 'fr';
update app_translation set msgValue = 'Irak', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IQ' and locale = 'fr';
update app_translation set msgValue = 'Iran', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IR' and locale = 'fr';
update app_translation set msgValue = 'Islande', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IS' and locale = 'fr';
update app_translation set msgValue = 'Pays', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.isoCode' and locale = 'fr';
update app_translation set msgValue = '<p> Le siège social de votre compagnie. Cela confirmera la devise à utiliser pour votre compte PICS. </p>', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.isoCode.fieldhelp' and locale = 'fr';
update app_translation set msgValue = 'Italie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.IT' and locale = 'fr';
update app_translation set msgValue = 'Jersey', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.JE' and locale = 'fr';
update app_translation set msgValue = 'Jamaïque', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.JM' and locale = 'fr';
update app_translation set msgValue = 'Jordanie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.JO' and locale = 'fr';
update app_translation set msgValue = 'Japon', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.JP' and locale = 'fr';
update app_translation set msgValue = 'Kenya', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KE' and locale = 'fr';
update app_translation set msgValue = 'Kirghizstan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KG' and locale = 'fr';
update app_translation set msgValue = 'Cambodge', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KH' and locale = 'fr';
update app_translation set msgValue = 'Kiribati', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KI' and locale = 'fr';
update app_translation set msgValue = 'Comores', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KM' and locale = 'fr';
update app_translation set msgValue = 'Saint-Kitts-et-Nevis', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KN' and locale = 'fr';
update app_translation set msgValue = 'Corée du Nord', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KP' and locale = 'fr';
update app_translation set msgValue = 'Corée Sud', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KR' and locale = 'fr';
update app_translation set msgValue = 'Koweït', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KW' and locale = 'fr';
update app_translation set msgValue = 'Îles Caïmans', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KY' and locale = 'fr';
update app_translation set msgValue = 'Kazakhstan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.KZ' and locale = 'fr';
update app_translation set msgValue = 'Laos', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LA' and locale = 'fr';
update app_translation set msgValue = 'Liban', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LB' and locale = 'fr';
update app_translation set msgValue = 'Sainte-Lucie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LC' and locale = 'fr';
update app_translation set msgValue = 'Liechtenstein', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LI' and locale = 'fr';
update app_translation set msgValue = 'Sri Lanka', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LK' and locale = 'fr';
update app_translation set msgValue = 'Liberia', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LR' and locale = 'fr';
update app_translation set msgValue = 'Lesotho', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LS' and locale = 'fr';
update app_translation set msgValue = 'Lituanie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LT' and locale = 'fr';
update app_translation set msgValue = 'Luxembourg', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LU' and locale = 'fr';
update app_translation set msgValue = 'Lettonie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LV' and locale = 'fr';
update app_translation set msgValue = 'Libye', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.LY' and locale = 'fr';
update app_translation set msgValue = 'Maroc', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MA' and locale = 'fr';
update app_translation set msgValue = 'Monaco', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MC' and locale = 'fr';
update app_translation set msgValue = 'Moldavie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MD' and locale = 'fr';
update app_translation set msgValue = 'Monténégro', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ME' and locale = 'fr';
update app_translation set msgValue = 'Saint-Martin', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MF' and locale = 'fr';
update app_translation set msgValue = 'Madagascar', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MG' and locale = 'fr';
update app_translation set msgValue = 'Iles Marshall', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MH' and locale = 'fr';
update app_translation set msgValue = 'Macédoine', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MK' and locale = 'fr';
update app_translation set msgValue = 'Mali', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ML' and locale = 'fr';
update app_translation set msgValue = 'Myanmar', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MM' and locale = 'fr';
update app_translation set msgValue = 'Mongolie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MN' and locale = 'fr';
update app_translation set msgValue = 'Macao', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MO' and locale = 'fr';
update app_translation set msgValue = 'Iles Mariannes du Nord', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MP' and locale = 'fr';
update app_translation set msgValue = 'Martinique', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MQ' and locale = 'fr';
update app_translation set msgValue = 'Mauritanie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MR' and locale = 'fr';
update app_translation set msgValue = 'Monserrat', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MS' and locale = 'fr';
update app_translation set msgValue = 'Malte', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MT' and locale = 'fr';
update app_translation set msgValue = 'Ile Maurice', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MU' and locale = 'fr';
update app_translation set msgValue = 'Maldives', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MV' and locale = 'fr';
update app_translation set msgValue = 'Malawi', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MW' and locale = 'fr';
update app_translation set msgValue = 'Mexique', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MX' and locale = 'fr';
update app_translation set msgValue = 'Malaisie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MY' and locale = 'fr';
update app_translation set msgValue = 'Mozambique', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.MZ' and locale = 'fr';
update app_translation set msgValue = 'Namibie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NA' and locale = 'fr';
update app_translation set msgValue = 'Nouvelle-Calédonie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NC' and locale = 'fr';
update app_translation set msgValue = 'Niger', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NE' and locale = 'fr';
update app_translation set msgValue = 'Île de Norfolk', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NF' and locale = 'fr';
update app_translation set msgValue = 'Nigeria', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NG' and locale = 'fr';
update app_translation set msgValue = 'Nicaragua', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NI' and locale = 'fr';
update app_translation set msgValue = 'Pays-Bas', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NL' and locale = 'fr';
update app_translation set msgValue = 'Norvège', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NO' and locale = 'fr';
update app_translation set msgValue = 'Népal', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NP' and locale = 'fr';
update app_translation set msgValue = 'Nauru', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NR' and locale = 'fr';
update app_translation set msgValue = 'Nioué', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NU' and locale = 'fr';
update app_translation set msgValue = 'Nouvelle-Zélande', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.NZ' and locale = 'fr';
update app_translation set msgValue = 'Oman', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.OM' and locale = 'fr';
update app_translation set msgValue = 'Panama', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PA' and locale = 'fr';
update app_translation set msgValue = 'Pérou', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PE' and locale = 'fr';
update app_translation set msgValue = 'Polynésie Française', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PF' and locale = 'fr';
update app_translation set msgValue = 'Papouasie-Nouvelle-Guinée', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PG' and locale = 'fr';
update app_translation set msgValue = 'Philippines', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PH' and locale = 'fr';
update app_translation set msgValue = 'Pakistan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PK' and locale = 'fr';
update app_translation set msgValue = 'Pologne', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PL' and locale = 'fr';
update app_translation set msgValue = 'Saint-Pierre-et-Miquelon', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PM' and locale = 'fr';
update app_translation set msgValue = 'Pitcairn', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PN' and locale = 'fr';
update app_translation set msgValue = 'Puerto Rico', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PR' and locale = 'fr';
update app_translation set msgValue = 'Territoires palestiniens', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PS' and locale = 'fr';
update app_translation set msgValue = 'Portugal', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PT' and locale = 'fr';
update app_translation set msgValue = 'Palau', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PW' and locale = 'fr';
update app_translation set msgValue = 'Paraguay', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.PY' and locale = 'fr';
update app_translation set msgValue = 'Qatar', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.QA' and locale = 'fr';
update app_translation set msgValue = 'Ile de la Réunion', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.RE' and locale = 'fr';
update app_translation set msgValue = 'Roumanie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.RO' and locale = 'fr';
update app_translation set msgValue = 'Sérbie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.RS' and locale = 'fr';
update app_translation set msgValue = 'Russie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.RU' and locale = 'fr';
update app_translation set msgValue = 'Rwanda', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.RW' and locale = 'fr';
update app_translation set msgValue = 'Arabie Saoudite', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SA' and locale = 'fr';
update app_translation set msgValue = 'Iles Salomon', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SB' and locale = 'fr';
update app_translation set msgValue = 'Séychèlles', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SC' and locale = 'fr';
update app_translation set msgValue = 'Soudan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SD' and locale = 'fr';
update app_translation set msgValue = 'Suède', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SE' and locale = 'fr';
update app_translation set msgValue = 'Singapour', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SG' and locale = 'fr';
update app_translation set msgValue = 'Sainte-Hélène', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SH' and locale = 'fr';
update app_translation set msgValue = 'Slovénie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SI' and locale = 'fr';
update app_translation set msgValue = 'Svalbard et Ile Jan Mayen', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SJ' and locale = 'fr';
update app_translation set msgValue = 'Slovaquie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SK' and locale = 'fr';
update app_translation set msgValue = 'Sierra Leone', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SL' and locale = 'fr';
update app_translation set msgValue = 'San Marino', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SM' and locale = 'fr';
update app_translation set msgValue = 'Sénègal', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SN' and locale = 'fr';
update app_translation set msgValue = 'Somalie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SO' and locale = 'fr';
update app_translation set msgValue = 'Surinam', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SR' and locale = 'fr';
update app_translation set msgValue = 'Sao Tomé-et-Principe', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ST' and locale = 'fr';
update app_translation set msgValue = 'Salvador', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SV' and locale = 'fr';
update app_translation set msgValue = 'Syrie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SY' and locale = 'fr';
update app_translation set msgValue = 'Swaziland', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.SZ' and locale = 'fr';
update app_translation set msgValue = 'Iles Turques-et-Caïques', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TC' and locale = 'fr';
update app_translation set msgValue = 'Tchad', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TD' and locale = 'fr';
update app_translation set msgValue = 'Térritoires du Sud Français', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TF' and locale = 'fr';
update app_translation set msgValue = 'Togo', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TG' and locale = 'fr';
update app_translation set msgValue = 'Thaïlande', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TH' and locale = 'fr';
update app_translation set msgValue = 'Tadjikistan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TJ' and locale = 'fr';
update app_translation set msgValue = 'Tokelau', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TK' and locale = 'fr';
update app_translation set msgValue = 'Timor-Leste', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TL' and locale = 'fr';
update app_translation set msgValue = 'Turkménistan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TM' and locale = 'fr';
update app_translation set msgValue = 'Tunisie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TN' and locale = 'fr';
update app_translation set msgValue = 'Tonga', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TO' and locale = 'fr';
update app_translation set msgValue = 'Turquie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TR' and locale = 'fr';
update app_translation set msgValue = 'Trinidad-et-Tobago', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TT' and locale = 'fr';
update app_translation set msgValue = 'Tuvalu', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TV' and locale = 'fr';
update app_translation set msgValue = 'Taïwan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TW' and locale = 'fr';
update app_translation set msgValue = 'Tanzanie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.TZ' and locale = 'fr';
update app_translation set msgValue = 'Ukraine', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.UA' and locale = 'fr';
update app_translation set msgValue = 'Ouganda', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.UG' and locale = 'fr';
update app_translation set msgValue = 'Îles mineures des États-Unis', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.UM' and locale = 'fr';
update app_translation set msgValue = 'États-Unis', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.US' and locale = 'fr';
update app_translation set msgValue = 'Uruguay', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.UY' and locale = 'fr';
update app_translation set msgValue = 'Ouzbékistan', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.UZ' and locale = 'fr';
update app_translation set msgValue = 'Saint-Siège', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.VA' and locale = 'fr';
update app_translation set msgValue = 'Saint-Vincent-et-les-Grenadines', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.VC' and locale = 'fr';
update app_translation set msgValue = 'Vénézuéla', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.VE' and locale = 'fr';
update app_translation set msgValue = 'Îles Vierges britanniques', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.VG' and locale = 'fr';
update app_translation set msgValue = 'Îles majeures des États-Unis', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.VI' and locale = 'fr';
update app_translation set msgValue = 'Vietnam', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.VN' and locale = 'fr';
update app_translation set msgValue = 'Vanuatu', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.VU' and locale = 'fr';
update app_translation set msgValue = 'Wallis-et-Futuna', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.WF' and locale = 'fr';
update app_translation set msgValue = 'Samoa', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.WS' and locale = 'fr';
update app_translation set msgValue = 'Yémen', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.YE' and locale = 'fr';
update app_translation set msgValue = 'Mayotte', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.YT' and locale = 'fr';
update app_translation set msgValue = 'Afrique du Sud', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ZA' and locale = 'fr';
update app_translation set msgValue = 'Zambie', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ZM' and locale = 'fr';
update app_translation set msgValue = 'Zimbabwe', updatedBy = 24143, updateDate = NOW() where msgKey = 'Country.ZW' and locale = 'fr';
update app_translation set msgValue = '<p>Veuillez sélectionner le pays dans lequel l''opérateur est situé. Cela détermine la configuration par pays héritée de PICS.</p>', updatedBy = 24143, updateDate = NOW() where msgKey = 'OperatorAccount.country.fieldhelp' and locale = 'fr';
update app_translation set msgValue = 'Créer un compte', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.button.CreateAccount' and locale = 'fr';
update app_translation set msgValue = 'Informations sur la compagnie', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.CompanyDetails.heading' and locale = 'fr';
update app_translation set msgValue = 'Identification de l''entreprise', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.CompanyIdentification.heading' and locale = 'fr';
update app_translation set msgValue = 'Le nom <b>{0}</b> existe déjà. S&#39;il vous plaît contacter un représentant PICS.', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.error.DuplicateContractorName' and locale = 'fr';
update app_translation set msgValue = 'Vous devez vous déconnecter avant de tenter d&#39;enregistrer un nouveau compte.', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.error.LogoutBeforRegistering' and locale = 'fr';
update app_translation set msgValue = 'S&#39;il vous plaît remplir le champ Mot de passe.', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.error.NoPassword' and locale = 'fr';
update app_translation set msgValue = 'Indique un champ requis', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.IndicatesRequiredInfo' and locale = 'fr';
update app_translation set msgValue = 'Produit ou service', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.IndustryDetails.heading' and locale = 'fr';
update app_translation set msgValue = 'Métier ou service principal', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.IndustryDetails.MainTrade' and locale = 'fr';
update app_translation set msgValue = 'Pays', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.PrimaryAddress.Country' and locale = 'fr';
update app_translation set msgValue = 'Adresse principale', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.PrimaryAddress.heading' and locale = 'fr';
update app_translation set msgValue = 'Personne-ressource', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.PrimaryContact.heading' and locale = 'fr';
update app_translation set msgValue = 'Propriétaire unique', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.SoleProprietor.heading' and locale = 'fr';
update app_translation set msgValue = 'En cliquant sur <span style="color: #529214; font-family: ''Lucida Grande'', Tahoma, Arial">[Créer un compte]</span> ci-dessous, je certifie que je suis d''accord avec les termes et conditions de <a href="#" onclick="window.open(''ContractorAgreement.action?request_locale=fr'',''name'',''toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=700,height=700''); return false;" class="ext"> Accords de l''entrepreneur PICS</a>.', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.TermsAndConditions' and locale = 'fr';
update app_translation set msgValue = 'Enregistrement de la compagnie', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistration.title' and locale = 'fr';
update app_translation set msgValue = 'Veuillez inclure tout autre détail utile à l''entrepreneur pour s''enregistrer. Indiquez l''étendue des travaux et un échéancier au besoin. Ceci est essentiel pour avoir du succès avec les inscriptions.', updatedBy = 24143, updateDate = NOW() where msgKey = 'ContractorRegistrationRequest.reasonForRegistration.fieldhelp' and locale = 'fr';
update app_translation set msgValue = 'vérification la disponibilité du nom ...', updatedBy = 24143, updateDate = NOW() where msgKey = 'JS.Registration.CheckingName' and locale = 'fr';
update app_translation set msgValue = 'vérification de la disponibilité du numéro d''identification fiscale ...', updatedBy = 24143, updateDate = NOW() where msgKey = 'JS.Registration.CheckingTaxID' and locale = 'fr';
update app_translation set msgValue = 'vérification de la disponibilité du nom d''utilisateur ...', updatedBy = 24143, updateDate = NOW() where msgKey = 'JS.Registration.CheckingUsername' and locale = 'fr';
update app_translation set msgValue = 'Enregistrement de Entrepreneur', updatedBy = 24143, updateDate = NOW() where msgKey = 'Subscription.ContractorRegistration.description' and locale = 'fr';
update app_translation set msgValue = 'Ce courriel inclut une liste d''entrepreneurs qui se sont récemment enregistrés sur PICS et liés à votre compte. Vous pouvez choisir de le recevoir quotidiennement, hebdomadairement ou mensuellement. Si aucun entrepreneur ne s''est enregistré, vous ne recevrez pas de courriel.', updatedBy = 24143, updateDate = NOW() where msgKey = 'Subscription.ContractorRegistration.longDescription' and locale = 'fr';
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.AgreeTC', 'En cliquant sur Débuter, vous donnez votre accords avec les termes et conditions  <a href="#" class="contractor-agreement modal-link" data-title="Contractor Agreement" data-url="ContractorAgreement.action">PICS Contractor Agreement</a>.', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.BecomeAMember', 'Devenez membre de PICS maintenant !', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.CompanyDetails.heading', 'Traduction manquante', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.CompanyInformation', 'Informations sur la compagnie', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.ContactInformation', 'Contact principal', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.CountryCount', '16 Pays', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.JoinInfo', 'Vous n''êtes qu''à quelques clics de<br />de joindre le réseau d''entrepreneurs <br /> avec la croissance la plus rapide au monde.', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.ModalBody', 'One fine body…', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.ModalHeading', 'Modal Heading', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.NeedHelp', 'Besoin d''aide pour vous enregistrer?', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.OperatorCount', '850 Clients/Opérateurs', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.PICSCompanies', 'Compagnies qui utilisent PICS', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Qualify', 'Se qualifer<br />grâce à PICS', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Questions', 'Pour toutes questions, écrivez-nous à <a href="#">support@picsauditing.com</a>, appeler nous au (800) 506-PICS (7427) ou clavarder avec l''un de nos représentants.', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Reason1', 'Se  préqualifer pour un projet ou un travail', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Reason2', 'Être exposé à plus de clients', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Reason3', 'Prime d''assurance réduite', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Reason4', 'Marketing pour votre compagnie', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Reason5', 'Accès direct à des professionnels de la sécurité', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Reason6', 'Technologie de logiciel simple et puissante', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Reason7', 'Service à la clientèle expert', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.TheContractorsChoice', 'Le Choix des entrepreneurs', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.UserCount', '50,000 utilisateurs courants', 'fr', 24143, NOW());
insert into app_translation (msgKey, msgValue, locale, createdBy, creationDate) values ('Registration.Why', 'Pourquoi les entrepreneurs <br />choisissent PICS?', 'fr', 24143, NOW());
--