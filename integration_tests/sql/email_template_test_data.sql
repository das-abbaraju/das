insert into email_template values(
'2', '1100', 'Welcome Email', 'Welcome to PICS, Here is Your Username and Password ', '<SubscriptionHeader>Thank you for creating an online account with PICS. All of the information that you have entered has been saved in our system. Please keep this e-mail in the event you forget your password.<br /><br />Below is your login Information.<br/><br/>Username: ${userName}<br /><br />If you forget your password, please <a href = "http://www.picsorganizer.com/AccountRecovery.action">Click Here </a>.<br /><br />Please <a href=" ${confirmLink}">Click Here</a> to confirm that you have received this email. A separate email will be sent to you to confirm registration once you have completed the process.<br /><br />Thank you,<br />Have a great day!<br />Regards,<br /><br />PICS<br />(949) 387-1940<br />www.picsauditing.com<br /><br />- Problems logging in? Contact Registrations@PICSauditing.com<br />- Questions about Registration with PICS? Registrations@PICSauditing.com<br />- Trying to reach a PICS Customer Service Representative? Contact Info@PICSauditing.com<SubscriptionFooter>', '941', '2008-09-29 00:00:00', '46726', '2012-04-25 15:44:48', 'Audit', '1', '1', 'undefined', '1', '["en","fr","es","de","nl","fi","sv","zh","pt"]'
);

insert into email_template values(
'5', '1100', 'New User Welcome', 'New PICS User Account Created', 'Hello <DisplayName>,
<MyName> has issued you a login for the ${user.account.name} account on PICS.
Please log in using the following link to set your password.
${confirmLink}
Have a great week,
<PICSSignature>', '941', '2008-09-29 00:00:00', '20952', '2012-03-09 12:15:46', 'Audit', '1', '0', 'undefined', '1', '["en","fr","es","de","nl","fi","sv","zh","pt"]'
);

insert into email_template values(
'6', '1100', 'Open Requirements Reminder', 'Open Requirements Reminder for <AuditTypeName> - Your Action Pending', 'Dear <ContactName>,

While reviewing your account--<CompanyName>--we have determined that you have some outstanding requirements that need to be addressed in regards to your <AuditTypeName>.  We would greatly appreciate it if you could please take the time to review the outstanding requirements on each audit and upload the documents that will satisfy those requirements.

#if( $audit.auditType.id == 2)
Remember, in order to satisfy the requirements of a <AuditTypeName>, we will need your revised, completed and most up to date copies of your safety programs that include the items required within each section of the <AuditTypeName>.  You can view these requirements by logging on to your account and there will be an "OPEN TASKS" window, which will read "You have open requirements from your recent <AuditTypeName>."  This is a link that you can click on to see any categories that may have outstanding requirements to fulfill.

MANUAL AUDIT (Safety Manual Program Audit) HOW TO CLOSE YOUR AUDIT

ITEMS THAT ARE NOT APPLICABLE
If there is a requirement that, based on your scope of work as a company, is not applicable please provide via email or upload a file explaining why that requirement (program) does not apply. For example if you use forklifts at your own facility, but currently not at the operator''s facility we will still need to review your forklift program. Keep in mind that there are some items within the audit that are required depending on the facilities such as Awareness Programs (Asbestos, Benzene, H2S and Lead) which will be applicable if you enter into a refinery site. Once we can verify the not applicable items, those requirements will be removed off your <AuditTypeName>.

SAMPLE PROGRAMS TO ASSIST YOU IN WRITING YOUR PROGRAMS
Once we have a list of all items that are applicable, we can send you sample programs to assist in updating your safety manual.  Please keep in mind that these programs are only a tool to help your company write their own policy/procedure.  Once you feel that you have updated your program to cover all requirements please email us a copy to review and close out the audit.  You can upload your updated programs online.

IF YOU HAVE UPLOADED A REVISED MANUAL OR ANY OTHER DOCUMENTS SINCE THE AUDIT
If you have uploaded a revised manual or any other documents that you feel will close out the outstanding requirements, please let us know by replying to this email and we will check your account to verify receipt and then close out the requirements based on what we have received.
#end
#if( $audit.auditType.id == 3)
Remember, in order to satisfy the requirements of your <AuditTypeName>, we will need training documentation, dated within the past year and signed by your employees in order to close out the <AuditTypeName> Requirements.  You can view these requirements by logging on to your account and there will be an "OPEN TASKS" window, which will read "You have open requirements from your recent <AuditTypeName>."  This is a link that you can click on to see any categories that may have outstanding requirements to fulfill.

IMPLEMENTATION AUDIT (TRAINING DOCUMENTATION AUDIT)
In order to close out your implementation audit requirements, please send the applicable training documentation (for example tests/quizzes, safety meetings/tailgate meetings, safety council training, site specific training or computer based training) completed within the last year.  If you have any questions on whether or not the training documentation will suffice, please feel free to contact us and we will be more than happy to assist you.
#end

We thank you for your time and cooperation in order to assist us in completing our audit process, please login to your account and click on the open task to address the outstanding requirements.  Please upload your documents within the
upload requirements section on your audit.  You can log in to your account during the process to view any requirements that are still outstanding or to check on your status. We look forward to hearing from you.

Thanks,
PICS

Fax number 949-269-9165
17701 Cowan Ste. 140
Irvine, CA 92614', '941', '2008-09-29 00:00:00', '46726', '2012-03-15 09:20:06', 'Audit', '1', '0', 'undefined', '1', '["en","fr","es","de","nl","fi","sv","zh","pt"]'

);

insert into email_template values(
'7', '1100', 'Audit Submission with Requirements', 'Your PICS <AuditTypeName> has been submitted - Requirements Pending', 'Hello <ContactName>,

PICS has submitted <AuditTypeName> of <CompanyName>''s.
Please log in to your PICS account at http://www.picsauditing.com/.   Once you log in, there will be an "OPEN TASKS" window, which will read "You have open requirements from your recent Audit."  This is a link that you can click on to see any categories that may have outstanding requirements to fulfill. Once in the upload requirements section you can click on each question to upload the requirement for it.

Our auditors have made every effort to perform the audit based on their knowledge of what your company does, but sometimes there are requirements listed that do not apply to the scope of work for your company''s job processes. These items can often be rectified immediately.

#if( $audit.auditType.id == 2)
We encourage you to read the "HOW TO close out your audits" information below to rectify these items. 

HOW TO CLOSE YOUR AUDIT

ITEMS THAT ARE NOT APPLICABLE
     If there is a requirement that, based on your scope of work as a company, is not applicable please provide via email an explanation as to why a requirement (program) does not apply. For example if you use forklifts at you own facility, but currently not at the operator''s facility we will still need to review your forklift program.  Keep in mind that there are some items within the audit that are required depending on the facilities such as Awareness Programs (Asbestos, Benzene, H2S and Lead) which will always be applicable if you enter into a refinery site. Once we can verify the not applicable items, those requirements will be removed off your manual audit.

SAMPLE PROGRAMS TO ASSIST YOU IN WRITING YOUR PROGRAMS
     Once we have a list of all items that are applicable, we can send you sample programs to assist in updating your safety manual.  Please keep in mind that these programs are only a tool to help your company write their own policy/procedure.  Once you feel that you have updated your program to cover all requirements please email us a copy to review and close out the audit.  You can send your updated programs to audits@picsauditing.com.

IF YOU HAVE UPLOADED A REVISED MANUAL OR ANY OTHER DOCUMENTS SINCE THE AUDIT
     If you have uploaded a revised manual or any other documents that you feel will close out the outstanding requirements, please let us know by replying to this email and we will check your account to verify receipt and then close out the requirements based on what we have received.
#end

In order to close out any requirements, please login to your account and click on the open task to address the outstanding requirements.  You can log in to your account during the process to view any requirements that are still outstanding or to check on your status.

Again, if you have any questions or concerns, feel free to contact us.

Thanks, and have a safe year!

PICS Auditing
Please upload your documentation within the
upload requirements section on your audit.
Fax number 949-269-9165
17701 Cowan Ste. 140
Irvine, CA 92614
', '941', '2008-09-29 00:00:00', '46726', '2012-03-05 09:39:41', 'Audit', '1', '0', 'undefined', '0', '["en","fr","es","de","nl","fi","sv","zh","pt"]'
);

insert into email_template values(
'8', '1100', 'DA Submission', 'Your PICS Drug and Alcohol Audit has been completed', 'Hello <ContactName>,

PICS has completed a Drug &Alcohol audit for <CompanyName>.  Please log in to our website, and click on the open task link to see any outstanding requirements to address. Please feel free to contact us and review the D&A audit as there may be many items we can close out just by speaking over the phone. 

Our auditors have made every effort to perform the audit based on their knowledge of what your company does, but sometimes there are requirements listed that do not apply to the work your company performs. These items can often be rectified immediately so we encourage you to contact us to discuss any items you feel may not apply. 

To close out open requirements, login to your account and click on the open task to upload the required documentation. 

Again, if you have any questions or concerns, feel free to contact us.

Thanks, and have a safe year!

<PICSSignature>
', '941', '2008-09-29 00:00:00', '46726', '2012-03-05 09:42:45', 'Audit', '1', '0', 'undefined', '0', '["en"]'
);