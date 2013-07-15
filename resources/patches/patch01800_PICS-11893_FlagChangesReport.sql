-- update flags
UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Audits,', ':"Audits",')
WHERE gc.flagDetail LIKE '%:Audits,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Insurance,', ':"Insurance",')
WHERE gc.flagDetail LIKE '%:Insurance,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Insurance AMB Class,', ':"InsuranceAMBClass",')
WHERE gc.flagDetail LIKE '%:Insurance AMB Class,%'
;
UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':InsuranceAMBClass,', ':"InsuranceAMBClass",')
WHERE gc.flagDetail LIKE '%:InsuranceAMBClass,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Insurance AMB Rating,', ':"InsuranceAMBRating",')
WHERE gc.flagDetail LIKE '%:Insurance AMB Rating,%'
;
UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':InsuranceAMBRating,', ':"InsuranceAMBRating",')
WHERE gc.flagDetail LIKE '%:InsuranceAMBRating,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Insurance Criteria,', ':"InsuranceCriteria",')
WHERE gc.flagDetail LIKE '%:Insurance Criteria,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':InsuranceCriteria,', ':"InsuranceCriteria",')
WHERE gc.flagDetail LIKE '%:InsuranceCriteria,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Paperwork,', ':"Paperwork",')
WHERE gc.flagDetail LIKE '%:Paperwork,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Safety,', ':"Safety",')
WHERE gc.flagDetail LIKE '%:Safety,%'
;

UPDATE
  generalcontractors gc
SET gc.flagDetail=REPLACE(gc.flagDetail, ':Statistics,', ':"Statistics",')
WHERE gc.flagDetail LIKE '%:Statistics,%'
;

-- update base flags

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Audits,', ':"Audits",')
WHERE gc.baselineFlagDetail LIKE '%:Audits,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Insurance,', ':"Insurance",')
WHERE gc.baselineFlagDetail LIKE '%:Insurance,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Insurance AMB Class,', ':"InsuranceAMBClass",')
WHERE gc.baselineFlagDetail LIKE '%:Insurance AMB Class,%'
;
UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':InsuranceAMBClass,', ':"InsuranceAMBClass",')
WHERE gc.baselineFlagDetail LIKE '%:InsuranceAMBClass,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Insurance AMB Rating,', ':"InsuranceAMBRating",')
WHERE gc.baselineFlagDetail LIKE '%:Insurance AMB Rating,%'
;
UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':InsuranceAMBRating,', ':"InsuranceAMBRating",')
WHERE gc.baselineFlagDetail LIKE '%:InsuranceAMBRating,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Insurance Criteria,', ':"InsuranceCriteria",')
WHERE gc.baselineFlagDetail LIKE '%:Insurance Criteria,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':InsuranceCriteria,', ':"InsuranceCriteria",')
WHERE gc.baselineFlagDetail LIKE '%:InsuranceCriteria,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Paperwork,', ':"Paperwork",')
WHERE gc.baselineFlagDetail LIKE '%:Paperwork,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Safety,', ':"Safety",')
WHERE gc.baselineFlagDetail LIKE '%:Safety,%'
;

UPDATE
  generalcontractors gc
SET gc.baselineFlagDetail=REPLACE(gc.baselineFlagDetail, ':Statistics,', ':"Statistics",')
WHERE gc.baselineFlagDetail LIKE '%:Statistics,%'
;
