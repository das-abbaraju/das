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
