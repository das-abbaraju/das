-- The purpose of this script is to slowly apply the AuditFor field to the different 
-- WCB Audits using "hueristic-like" checks to determine what the likely rate year
-- is for the WCB Audit.
-- 
-- Once the update is complete, there will be some auditFor years that will be
-- manually set based on special cases that the hueristics would not work.
--
-- Once the auditFor is set, the expiration dates are correctly set for each of
-- the audits based on (1) AuditFor, (2) All the CAOs are "Approved" or "N/A"
-- in order for the Expiration Date to be set. If one CAO does not have a status
-- of "Approved" or "N/A", then the expiration date will be set to NULL.


-- Start out by setting the auditFor on WCBs where we already know are for 2012
update contractor_audit
	set contractor_audit.auditFor = '2012'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.expiresDate >= '2013-03-01';

-- Any audits created on or after March 1, 2012 are also definitely for 2012
update contractor_audit
	set contractor_audit.auditFor = '2012'
	where contractor_audit.creationDate >= '2012-03-01'
	and contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and (contractor_audit.auditFor is null or trim(contractor_audit.auditFor) = '');

-- This is applicable
update contractor_audit
	join contractor_audit_operator on contractor_audit_operator.auditID = contractor_audit.id
	set contractor_audit.auditFor = '2012'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.creationDate >= '2012-01-01'
	and (contractor_audit.effectiveDate >= '2012-01-01' or contractor_audit.effectiveDate is null)
	and contractor_audit.auditFor is null;

-- Any audit where the last recalculation is before January 1, 2012 and has a Null AuditFor
-- is a 2011 WCB
update contractor_audit
	set contractor_audit.auditFor = '2011'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.lastRecalculation < '2012-01-01'
	and contractor_audit.auditFor is null;


-- Delete all the WCBs that do not have any CAOs and with NULL auditFor field.
delete from contractor_audit
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.id not in (select auditID from contractor_audit_operator)
	and contractor_audit.auditFor is null;
	

-- Set the remaining WCBs that do not have any auditFor field to match the answer entered for
-- the question regarding the Rate Year for the Uploaded WCB File
update contractor_audit
	left join pqfdata on pqfdata.auditID = contractor_audit.id
	set contractor_audit.auditFor = pqfdata.answer
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and pqfdata.questionID = 10225
	and contractor_audit.auditFor is null;


-- Set the auditFor to 2011 when the auditFor is still null and the
-- effective date is in 2011 (these will be listed on the site as 2011 WCBs)
Update contractor_audit
	set contractor_audit.auditFor = '2011'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.auditFor is null
	and contractor_audit.effectiveDate < '2012-01-01';


-- These expired in 2011, so they are definitely 2011 WCBs
update contractor_audit
	set contractor_audit.auditFor = '2011'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.expiresDate < '2012-01-01'
	and contractor_audit.auditFor is null;
	
	
-- Due to some special cases, I needed to go through the data and manually set the AuditFor field
-- based on the uploaded WCB.
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 532232;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 548280;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559543;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 569549;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634015;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634545;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634638;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636415;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559533;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559539;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559544;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 637937;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 631921;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634638;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 539921;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 600396;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 570090;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 590221;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 638121;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 633847;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 635882;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636131;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 639444;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636288;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 644771;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 637294;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 584815;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636067;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634677;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 635388;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 656916;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 635212;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 658543;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634015;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634749;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 635820;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 637433;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634545;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634921;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 635422;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636781;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636714;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636415;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 636922;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 665270;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 667393;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 635853;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 637919;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 666235;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 638259;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 687053;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 688436;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 688463;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 640309;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 685494;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 689141;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 694440;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 694716;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 694003;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 695024;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 695022;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 690301;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 664720;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 700432;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 694137;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 702744;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 703669;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 705195;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 708562;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 634035;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 709234;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 711765;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 704280;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 665588;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 716963;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 718255;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 715572;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 720643;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529368;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529370;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529366;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 559774;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 579219;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 590490;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 601911;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 606737;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529367;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559773;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 579218;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 584812;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529369;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 559772;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 526936;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 527071;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559771;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 571782;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 589237;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529365;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 556824;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 572961;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 581105;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 589238;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529363;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 589254;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559770;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559769;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 529364;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 589255;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 589240;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559171;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559172;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 577742;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 587177;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 589241;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 587179;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 589242;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559170;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 587178;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 527104;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 559173;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 528345;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 585719;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 551731;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 635212;
UPDATE contractor_audit SET contractor_audit.auditFor = '2012' WHERE contractor_audit.id = 644771;
update contractor_audit set contractor_audit.auditFor = '2011' where contractor_audit.id = 584343;

UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 614891;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 610892;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 602265;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 612666;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 598318;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 595715;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 601327;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 613608;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 590387;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 586199;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 607139;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 610206;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 597923;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 597920;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 597919;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 597455;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 636615;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 578026;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 590495;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 578048;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 561381;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 637090;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 568068;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 611156;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 557366;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 572075;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 572621;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 580399;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 636291;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 545961;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 527877;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 544352;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 528330;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 528663;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 528557;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 528110;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 528195;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 528009;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 530315;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 526695;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 526841;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 633987;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 633988;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 597833;
UPDATE contractor_audit SET contractor_audit.auditFor = '2011' WHERE contractor_audit.id = 597864;




-- Set the expiration date for the WCBs that are not Approved nor N/A to null so
-- they are still being flagged
update contractor_audit
	join contractor_audit_operator on contractor_audit_operator.auditID = contractor_audit.id
	set contractor_audit.expiresDate = null
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit_operator.status not in ('Approved', 'NotApplicable')
	and contractor_audit.auditFor in ('2011', '2012');

-- Set the expiration date to October 15, 2011 for 2011 WCBs	
update contractor_audit
	join contractor_audit_operator on contractor_audit_operator.auditID = contractor_audit.id
	set contractor_audit.expiresDate = '2011-12-31 23:59:59'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit_operator.status in ('Approved', 'NotApplicable')
	and contractor_audit.auditFor in ('2011');

-- Set the expiration date to October 15, 2012 for 2012 WCBs
update contractor_audit
	join contractor_audit_operator on contractor_audit_operator.auditID = contractor_audit.id
	set contractor_audit.expiresDate = '2012-12-31 23:59:59'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit_operator.status in ('Approved', 'NotApplicable')
	and contractor_audit.auditFor in ('2012');



-- THIS IS THE END OF THE UPDATED SCRIPT
