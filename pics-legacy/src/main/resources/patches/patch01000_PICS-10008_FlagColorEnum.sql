-- set affected contractors to be rerun in ContractorCron
UPDATE
    contractor_info ci
    JOIN generalcontractors gc ON gc.subID=ci.id
SET ci.lastRecalculation = NULL
WHERE gc.flag='';

-- update affected contractors
UPDATE
  generalcontractors gc
SET gc.flag='Clear'
WHERE gc.flag='';