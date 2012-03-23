-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-4542
update accounts a 
join operators o on a.id = o.id
set a.autoApproveRelationships = true where o.approvesRelationships = 'No';

update accounts a 
join operators o on a.id = o.id
set a.autoApproveRelationships = false where o.approvesRelationships = 'Yes';

update operators o
join generalcontractors gc on gc.genID = o.id
set gc.workStatus = 'Y'
where o.approvesRelationships = 'No' and gc.workStatus in ('P','N') and o.id >= 16;
--

-- PICS-2219
update generalcontractors gc
set gc.type = 'ContractorOperator'
where gc.type is null;

-- PICS-4791
update operators o set o.discountPercent = .5, o.discountExpiration = '2012-03-15' where o.id = 19344;
update operators o set o.discountPercent = .5, o.discountExpiration = '2012-06-01' where o.id = 26873;
update operators o set o.discountPercent = .5, o.discountExpiration = '2012-06-30' where o.id = 27406;
update operators o set o.discountPercent = .25, o.discountExpiration = '2012-12-31' where o.id = 22100;

-- PICS-4878
insert into app_properties(property, value)
values ('PICS.liveChat', 1);

-- PICS-5221 Data Conversion script to fix current flag data overrides to audit
-- lastyear, 2011
update
flag_data_override fdo
join flag_criteria fc on fdo.criteriaID=fc.id
join contractor_audit ca on fdo.conID=ca.conID
join contractor_audit_operator cao on cao.auditID=ca.id
join contractor_audit_operator_workflow caow on caow.caoID = cao.id
set fdo.year = ca.auditFor
where 1
and cao.visible=1
and cao.status='Complete'
AND caow.status='Complete'
AND ca.auditTypeID=11
and ca.auditFor='2011'
and fdo.year is NULL
AND fc.multiYearScope='LastYearOnly';

-- last year, 2010
update
flag_data_override fdo
join flag_criteria fc on fdo.criteriaID=fc.id
join contractor_audit ca on fdo.conID=ca.conID
join contractor_audit_operator cao on cao.auditID=ca.id
join contractor_audit_operator_workflow caow on caow.caoID = cao.id
join contractor_audit ca2 on fdo.conID=ca2.conID
join contractor_audit_operator cao2 on cao2.auditID=ca2.id and ca2.auditTypeID = 11 and ca2.auditFor = '2011' and cao2.status != 'Complete'
set fdo.year = ca.auditFor
where 1
and cao.visible=1
and cao.status='Complete'
AND caow.status='Complete'
AND ca.auditTypeID=11
and ca.auditFor='2010'
and fdo.year is NULL
AND fc.multiYearScope='LastYearOnly';

-- 2 years 2010
update
flag_data_override fdo
join flag_criteria fc on fdo.criteriaID=fc.id
join contractor_audit ca on fdo.conID=ca.conID
join contractor_audit_operator cao on cao.auditID=ca.id
join contractor_audit_operator_workflow caow on caow.caoID = cao.id
join contractor_audit ca2 on fdo.conID=ca2.conID
join contractor_audit_operator cao2 on cao2.auditID=ca2.id and ca2.auditTypeID = 11 and ca2.auditFor = '2011' and cao2.status = 'Complete'
set fdo.year = ca.auditFor
where 1
and cao.visible=1
and cao.status='Complete'
AND caow.status='Complete'
AND ca.auditTypeID=11
and ca.auditFor='2010'
and fdo.year is NULL
AND fc.multiYearScope='TwoYearsAgo';

-- 2 years 2009
update
flag_data_override fdo
join flag_criteria fc on fdo.criteriaID=fc.id
join contractor_audit ca on fdo.conID=ca.conID
join contractor_audit_operator cao on cao.auditID=ca.id
join contractor_audit_operator_workflow caow on caow.caoID = cao.id
join contractor_audit ca2 on fdo.conID=ca2.conID
join contractor_audit_operator cao2 on cao2.auditID=ca2.id and ca2.auditTypeID = 11 and ca2.auditFor = '2010' and cao2.status = 'Complete'
join contractor_audit ca3 on fdo.conID=ca3.conID
join contractor_audit_operator cao3 on cao3.auditID=ca3.id and ca2.auditTypeID = 11 and ca3.auditFor = '2011' and cao3.status != 'Complete'
set fdo.year = ca.auditFor
where 1
and cao.visible=1
and cao.status='Complete'
AND caow.status='Complete'
AND ca.auditTypeID=11
and ca.auditFor='2009'
and fdo.year is NULL
AND fc.multiYearScope='TwoYearsAgo';

-- 3 years, 2009
update
flag_data_override fdo
join flag_criteria fc on fdo.criteriaID=fc.id
join contractor_audit ca on fdo.conID=ca.conID
join contractor_audit_operator cao on cao.auditID=ca.id
join contractor_audit_operator_workflow caow on caow.caoID = cao.id
join contractor_audit ca2 on fdo.conID=ca2.conID
join contractor_audit_operator cao2 on cao2.auditID=ca2.id and ca2.auditTypeID = 11 and ca2.auditFor = '2010' and cao2.status = 'Complete'
join contractor_audit ca3 on fdo.conID=ca3.conID
join contractor_audit_operator cao3 on cao3.auditID=ca3.id and ca2.auditTypeID = 11 and ca3.auditFor = '2011' and cao3.status = 'Complete'
set fdo.year = ca.auditFor
where 1
and cao.visible=1
and cao.status='Complete'
AND caow.status='Complete'
AND ca.auditTypeID=11
and ca.auditFor='2009'
and fdo.year is NULL
AND fc.multiYearScope='ThreeYearsAgo';

-- 3 year, 2008
update
flag_data_override fdo
join flag_criteria fc on fdo.criteriaID=fc.id
join contractor_audit ca on fdo.conID=ca.conID
join contractor_audit_operator cao on cao.auditID=ca.id
join contractor_audit_operator_workflow caow on caow.caoID = cao.id
join contractor_audit ca2 on fdo.conID=ca2.conID
join contractor_audit_operator cao2 on cao2.auditID=ca2.id and ca2.auditTypeID = 11 and ca2.auditFor = '2009' and cao2.status = 'Complete'
join contractor_audit ca3 on fdo.conID=ca3.conID
join contractor_audit_operator cao3 on cao3.auditID=ca3.id and ca3.auditTypeID = 11 and ca3.auditFor = '2010' and cao3.status = 'Complete'
join contractor_audit ca4 on fdo.conID=ca4.conID
join contractor_audit_operator cao4 on cao4.auditID=ca4.id and ca4.auditTypeID = 11 and ca4.auditFor = '2011' and cao4.status != 'Complete'
set fdo.year = ca.auditFor
where 1
and cao.visible=1
and cao.status='Complete'
AND caow.status='Complete'
AND ca.auditTypeID=11
and ca.auditFor='2008'
and fdo.year is NULL
AND fc.multiYearScope='ThreeYearsAgo';

-- remaining
update
flag_data_override fdo
join flag_criteria fc on fdo.criteriaID=fc.id
join contractor_audit ca on fdo.conID=ca.conID
join contractor_audit_operator cao on cao.auditID=ca.id
join contractor_audit_operator_workflow caow on caow.caoID = cao.id
set fdo.year = ca.auditFor
where fdo.year is null
and cao.visible=1
and fdo.creationDate > caow.creationDate
AND ca.auditTypeID=11
AND fc.multiYearScope in ('ThreeYearsAgo','TwoYearsAgo','LastYearOnly');

-- PICS-5031
-- For Data conversion 
update flag_criteria 
set multiYearScope = 'ThreeYearSum' 
where multiYearScope = 'ThreeYearAggregate'; 

-- PICS-4928
update contractor_info c JOIN accounts a ON a.id = c.id 
set c.safetyRisk = 0
 WHERE (c.safetyRisk <> 0 AND a.onsiteServices = 0 AND a.offsiteServices = 0); 

update contractor_info c JOIN accounts a ON a.id = c.id 
set c.productRisk = 0
 WHERE (c.productRisk <> 0 AND a.materialSupplier = 0); 

update contractor_info c JOIN accounts a ON a.id = c.id 
set c.transportationRisk = 0
 WHERE (c.transportationRisk <> 0 AND a.transportationServices = 0);
 
 
 -- PICS-3156 Osha Data Conversion
-- OSHA
-- Total Man Hours
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8810,
  oa.manHours,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- Fatalities
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8812,
  oa.fatalities,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- Lost Workday Cases
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8813,
  oa.lostWorkCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- Lost Workdays
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8814,
  oa.lostWorkDays,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- Restricted
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8815,
  oa.restrictedWorkCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- Job Transfers
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8816,
  oa.modifiedWorkDay,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- injuries and illnesses
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select 
  oa.auditID,
  8817,
  oa.injuryIllnessCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- Were there any injuries?
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select 
  oa.auditID,
  8838,
  "Yes",
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "OSHA";

-- MSHA
-- Total Man Hours
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  10009,
  oa.manHours,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "MSHA";

-- Fatalities
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  10010,
  oa.fatalities,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "MSHA";

-- Lost Workday Cases
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  1011,
  oa.lostWorkCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "MSHA";

-- Lost Workdays
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  10012,
  oa.lostWorkDays,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "MSHA";

-- Restricted
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  10013,
  oa.restrictedWorkCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "MSHA";

-- Job Transfers
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  10014,
  oa.modifiedWorkDay,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "MSHA";

-- injuries and illnesses
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select 
  oa.auditID,
  11112,
  oa.injuryIllnessCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "MSHA";

-- COHS
-- Total Man Hours
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8839,
  oa.manHours,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "COHS";

-- Fatalities
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8841,
  oa.fatalities,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "COHS";

-- Lost Workday Cases
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8842,
  oa.lostWorkCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "COHS";

-- Lost Workdays
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8843,
  oa.lostWorkDays,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "COHS";

-- Restricted
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  8844,
  oa.restrictedWorkCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "COHS";

-- Job Transfers
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select
  oa.auditID,
  11119,
  oa.modifiedWorkDay,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "COHS";

-- injuries and illnesses
replace into pqfdata
            (auditID,
             questionID,
             answer,
             dateVerified,
             auditorID,
             creationDate,
             updateDate)
select 
  oa.auditID,
  8845,
  oa.injuryIllnessCases,
  oa.verifiedDate,
  oa.auditorID,
  oa.creationDate,
  oa.updateDate
from osha_audit oa
where oa.SHAType = "COHS";

-- PICS-5192
-- MSHA: Audit Cat Data update
update audit_cat_data acd1
join audit_cat_data acd2 on acd1.auditID = acd2.auditID
set acd1.applies = 0, acd2.applies = 1
where acd1.categoryID = 157
and acd2.categoryID = 2256
and acd1.applies = 1;

-- COHS: Audit Cat Data update
update audit_cat_data acd1
join audit_cat_data acd2 on acd1.auditID = acd2.auditID
set acd1.applies = 0, acd2.applies = 1
where acd1.categoryID = 158
and acd2.categoryID = 2086
and acd1.applies = 1;

-- OSHA: Audit Cat Data update
update audit_cat_data acd1
join audit_cat_data acd2 on acd1.auditID = acd2.auditID
set acd1.applies = 0, acd2.applies = 1
where acd1.categoryID = 151
and acd2.categoryID = 2033
and acd1.applies = 1;

-- Resetting the annual updates so calculations run when contractor is synchronized
update contractor_audit ca
set ca.lastRecalculation = null
where ca.auditTypeID = 11
and ca.auditFor in ('2008','2009','2010','2011');

-- PICS-4344 populating corruption perception indices
UPDATE ref_country SET perceivedCorruption = 9.5 WHERE isoCode = 'NZ';
UPDATE ref_country SET perceivedCorruption = 9.4 WHERE isoCode = 'DK';
UPDATE ref_country SET perceivedCorruption = 9.4 WHERE isoCode = 'FI';
UPDATE ref_country SET perceivedCorruption = 9.3 WHERE isoCode = 'SE';
UPDATE ref_country SET perceivedCorruption = 9.2 WHERE isoCode = 'SG';
UPDATE ref_country SET perceivedCorruption = 9 WHERE isoCode = 'NO';
UPDATE ref_country SET perceivedCorruption = 8.9 WHERE isoCode = 'NL';
UPDATE ref_country SET perceivedCorruption = 8.8 WHERE isoCode = 'AU';
UPDATE ref_country SET perceivedCorruption = 8.8 WHERE isoCode = 'CH';
UPDATE ref_country SET perceivedCorruption = 8.7 WHERE isoCode = 'CA';
UPDATE ref_country SET perceivedCorruption = 8.5 WHERE isoCode = 'LU';
UPDATE ref_country SET perceivedCorruption = 8.4 WHERE isoCode = 'HK';
UPDATE ref_country SET perceivedCorruption = 8.3 WHERE isoCode = 'IS';
UPDATE ref_country SET perceivedCorruption = 8 WHERE isoCode = 'DE';
UPDATE ref_country SET perceivedCorruption = 8 WHERE isoCode = 'JP';
UPDATE ref_country SET perceivedCorruption = 7.8 WHERE isoCode = 'AT';
UPDATE ref_country SET perceivedCorruption = 7.8 WHERE isoCode = 'BB';
UPDATE ref_country SET perceivedCorruption = 7.8 WHERE isoCode = 'GB';
UPDATE ref_country SET perceivedCorruption = 7.5 WHERE isoCode = 'BE';
UPDATE ref_country SET perceivedCorruption = 7.5 WHERE isoCode = 'IE';
UPDATE ref_country SET perceivedCorruption = 7.3 WHERE isoCode = 'BS';
UPDATE ref_country SET perceivedCorruption = 7.2 WHERE isoCode = 'CL';
UPDATE ref_country SET perceivedCorruption = 7.2 WHERE isoCode = 'QA';
UPDATE ref_country SET perceivedCorruption = 7.1 WHERE isoCode = 'US';
UPDATE ref_country SET perceivedCorruption = 7 WHERE isoCode = 'FR';
UPDATE ref_country SET perceivedCorruption = 7 WHERE isoCode = 'LC';
UPDATE ref_country SET perceivedCorruption = 7 WHERE isoCode = 'UY';
UPDATE ref_country SET perceivedCorruption = 6.8 WHERE isoCode = 'AE';
UPDATE ref_country SET perceivedCorruption = 6.4 WHERE isoCode = 'EE';
UPDATE ref_country SET perceivedCorruption = 6.3 WHERE isoCode = 'CY';
UPDATE ref_country SET perceivedCorruption = 6.2 WHERE isoCode = 'ES';
UPDATE ref_country SET perceivedCorruption = 6.1 WHERE isoCode = 'BW';
UPDATE ref_country SET perceivedCorruption = 6.1 WHERE isoCode = 'PT';
UPDATE ref_country SET perceivedCorruption = 6.1 WHERE isoCode = 'TW';
UPDATE ref_country SET perceivedCorruption = 5.9 WHERE isoCode = 'SI';
UPDATE ref_country SET perceivedCorruption = 5.8 WHERE isoCode = 'IL';
UPDATE ref_country SET perceivedCorruption = 5.8 WHERE isoCode = 'VC';
UPDATE ref_country SET perceivedCorruption = 5.7 WHERE isoCode = 'BT';
UPDATE ref_country SET perceivedCorruption = 5.6 WHERE isoCode = 'MT';
UPDATE ref_country SET perceivedCorruption = 5.6 WHERE isoCode = 'PR';
UPDATE ref_country SET perceivedCorruption = 5.5 WHERE isoCode = 'CV';
UPDATE ref_country SET perceivedCorruption = 5.5 WHERE isoCode = 'PL';
UPDATE ref_country SET perceivedCorruption = 5.4 WHERE isoCode = 'KR';
UPDATE ref_country SET perceivedCorruption = 5.2 WHERE isoCode = 'BN';
UPDATE ref_country SET perceivedCorruption = 5.2 WHERE isoCode = 'DM';
UPDATE ref_country SET perceivedCorruption = 5.1 WHERE isoCode = 'BH';
UPDATE ref_country SET perceivedCorruption = 5.1 WHERE isoCode = 'MO';
UPDATE ref_country SET perceivedCorruption = 5.1 WHERE isoCode = 'MU';
UPDATE ref_country SET perceivedCorruption = 5 WHERE isoCode = 'RW';
UPDATE ref_country SET perceivedCorruption = 4.8 WHERE isoCode = 'CR';
UPDATE ref_country SET perceivedCorruption = 4.8 WHERE isoCode = 'LT';
UPDATE ref_country SET perceivedCorruption = 4.8 WHERE isoCode = 'OM';
UPDATE ref_country SET perceivedCorruption = 4.8 WHERE isoCode = 'SC';
UPDATE ref_country SET perceivedCorruption = 4.6 WHERE isoCode = 'HU';
UPDATE ref_country SET perceivedCorruption = 4.6 WHERE isoCode = 'KW';
UPDATE ref_country SET perceivedCorruption = 4.5 WHERE isoCode = 'JO';
UPDATE ref_country SET perceivedCorruption = 4.4 WHERE isoCode = 'CZ';
UPDATE ref_country SET perceivedCorruption = 4.4 WHERE isoCode = 'NA';
UPDATE ref_country SET perceivedCorruption = 4.4 WHERE isoCode = 'SA';
UPDATE ref_country SET perceivedCorruption = 4.3 WHERE isoCode = 'MY';
UPDATE ref_country SET perceivedCorruption = 4.2 WHERE isoCode = 'CU';
UPDATE ref_country SET perceivedCorruption = 4.2 WHERE isoCode = 'LV';
UPDATE ref_country SET perceivedCorruption = 4.2 WHERE isoCode = 'TR';
UPDATE ref_country SET perceivedCorruption = 4.1 WHERE isoCode = 'GE';
UPDATE ref_country SET perceivedCorruption = 4.1 WHERE isoCode = 'ZA';
UPDATE ref_country SET perceivedCorruption = 4 WHERE isoCode = 'HR';
UPDATE ref_country SET perceivedCorruption = 4 WHERE isoCode = 'ME';
UPDATE ref_country SET perceivedCorruption = 4 WHERE isoCode = 'SK';
UPDATE ref_country SET perceivedCorruption = 3.9 WHERE isoCode = 'GH';
UPDATE ref_country SET perceivedCorruption = 3.9 WHERE isoCode = 'IT';
UPDATE ref_country SET perceivedCorruption = 3.9 WHERE isoCode = 'MK';
UPDATE ref_country SET perceivedCorruption = 3.9 WHERE isoCode = 'WS';
UPDATE ref_country SET perceivedCorruption = 3.8 WHERE isoCode = 'BR';
UPDATE ref_country SET perceivedCorruption = 3.8 WHERE isoCode = 'TN';
UPDATE ref_country SET perceivedCorruption = 3.6 WHERE isoCode = 'CN';
UPDATE ref_country SET perceivedCorruption = 3.6 WHERE isoCode = 'RO';
UPDATE ref_country SET perceivedCorruption = 3.5 WHERE isoCode = 'GM';
UPDATE ref_country SET perceivedCorruption = 3.5 WHERE isoCode = 'LS';
UPDATE ref_country SET perceivedCorruption = 3.5 WHERE isoCode = 'VU';
UPDATE ref_country SET perceivedCorruption = 3.4 WHERE isoCode = 'CO';
UPDATE ref_country SET perceivedCorruption = 3.4 WHERE isoCode = 'SV';
UPDATE ref_country SET perceivedCorruption = 3.4 WHERE isoCode = 'GR';
UPDATE ref_country SET perceivedCorruption = 3.4 WHERE isoCode = 'MA';
UPDATE ref_country SET perceivedCorruption = 3.4 WHERE isoCode = 'PE';
UPDATE ref_country SET perceivedCorruption = 3.4 WHERE isoCode = 'TH';
UPDATE ref_country SET perceivedCorruption = 3.3 WHERE isoCode = 'BG';
UPDATE ref_country SET perceivedCorruption = 3.3 WHERE isoCode = 'JM';
UPDATE ref_country SET perceivedCorruption = 3.3 WHERE isoCode = 'PA';
UPDATE ref_country SET perceivedCorruption = 3.3 WHERE isoCode = 'RS';
UPDATE ref_country SET perceivedCorruption = 3.3 WHERE isoCode = 'LK';
UPDATE ref_country SET perceivedCorruption = 3.2 WHERE isoCode = 'BA';
UPDATE ref_country SET perceivedCorruption = 3.2 WHERE isoCode = 'LR';
UPDATE ref_country SET perceivedCorruption = 3.2 WHERE isoCode = 'TT';
UPDATE ref_country SET perceivedCorruption = 3.2 WHERE isoCode = 'ZM';
UPDATE ref_country SET perceivedCorruption = 3.1 WHERE isoCode = 'AL';
UPDATE ref_country SET perceivedCorruption = 3.1 WHERE isoCode = 'IN';
UPDATE ref_country SET perceivedCorruption = 3.1 WHERE isoCode = 'KI';
UPDATE ref_country SET perceivedCorruption = 3.1 WHERE isoCode = 'SZ';
UPDATE ref_country SET perceivedCorruption = 3.1 WHERE isoCode = 'TO';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'AR';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'BJ';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'BF';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'DJ';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'GA';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'ID';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'MG';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'MW';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'MX';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'ST';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'SR';
UPDATE ref_country SET perceivedCorruption = 3 WHERE isoCode = 'TZ';
UPDATE ref_country SET perceivedCorruption = 2.9 WHERE isoCode = 'DZ';
UPDATE ref_country SET perceivedCorruption = 2.9 WHERE isoCode = 'EG';
UPDATE ref_country SET perceivedCorruption = 2.9 WHERE isoCode = 'MD';
UPDATE ref_country SET perceivedCorruption = 2.9 WHERE isoCode = 'SN';
UPDATE ref_country SET perceivedCorruption = 2.9 WHERE isoCode = 'VN';
UPDATE ref_country SET perceivedCorruption = 2.8 WHERE isoCode = 'BO';
UPDATE ref_country SET perceivedCorruption = 2.8 WHERE isoCode = 'ML';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'BD';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'EC';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'ET';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'GT';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'IR';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'KZ';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'MN';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'MZ';
UPDATE ref_country SET perceivedCorruption = 2.7 WHERE isoCode = 'SB';
UPDATE ref_country SET perceivedCorruption = 2.6 WHERE isoCode = 'AM';
UPDATE ref_country SET perceivedCorruption = 2.6 WHERE isoCode = 'DO';
UPDATE ref_country SET perceivedCorruption = 2.6 WHERE isoCode = 'HN';
UPDATE ref_country SET perceivedCorruption = 2.6 WHERE isoCode = 'PH';
UPDATE ref_country SET perceivedCorruption = 2.6 WHERE isoCode = 'SY';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'CM';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'ER';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'GY';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'LB';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'MV';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'NI';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'NE';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'PK';
UPDATE ref_country SET perceivedCorruption = 2.5 WHERE isoCode = 'SL';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'AZ';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'BY';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'KM';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'MR';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'NG';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'RU';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'TL';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'TG';
UPDATE ref_country SET perceivedCorruption = 2.4 WHERE isoCode = 'UG';
UPDATE ref_country SET perceivedCorruption = 2.3 WHERE isoCode = 'TJ';
UPDATE ref_country SET perceivedCorruption = 2.3 WHERE isoCode = 'UA';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'CF';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'CG';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'GW';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'KE';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'LA';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'NP';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'PG';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'PY';
UPDATE ref_country SET perceivedCorruption = 2.2 WHERE isoCode = 'ZW';
UPDATE ref_country SET perceivedCorruption = 2.1 WHERE isoCode = 'KH';
UPDATE ref_country SET perceivedCorruption = 2.1 WHERE isoCode = 'GN';
UPDATE ref_country SET perceivedCorruption = 2.1 WHERE isoCode = 'KG';
UPDATE ref_country SET perceivedCorruption = 2.1 WHERE isoCode = 'YE';
UPDATE ref_country SET perceivedCorruption = 2 WHERE isoCode = 'AO';
UPDATE ref_country SET perceivedCorruption = 2 WHERE isoCode = 'TD';
UPDATE ref_country SET perceivedCorruption = 2 WHERE isoCode = 'CD';
UPDATE ref_country SET perceivedCorruption = 2 WHERE isoCode = 'LY';
UPDATE ref_country SET perceivedCorruption = 1.9 WHERE isoCode = 'BI';
UPDATE ref_country SET perceivedCorruption = 1.9 WHERE isoCode = 'GQ';
UPDATE ref_country SET perceivedCorruption = 1.9 WHERE isoCode = 'VE';
UPDATE ref_country SET perceivedCorruption = 1.8 WHERE isoCode = 'HT';
UPDATE ref_country SET perceivedCorruption = 1.8 WHERE isoCode = 'IQ';
UPDATE ref_country SET perceivedCorruption = 1.6 WHERE isoCode = 'SD';
UPDATE ref_country SET perceivedCorruption = 1.6 WHERE isoCode = 'TM';
UPDATE ref_country SET perceivedCorruption = 1.6 WHERE isoCode = 'UZ';
UPDATE ref_country SET perceivedCorruption = 1.5 WHERE isoCode = 'AF';
UPDATE ref_country SET perceivedCorruption = 1.5 WHERE isoCode = 'MM';
UPDATE ref_country SET perceivedCorruption = 1 WHERE isoCode = 'KP';
UPDATE ref_country SET perceivedCorruption = 1 WHERE isoCode = 'SO';
