update flag_criteria set category = REPLACE(`category` , ' ' , '' );

delete from app_translation where msgKey in
('FlagCriteria.Category.Insurance AMB Class',
 'FlagCriteria.Category.Insurance AMB Rating',
 'FlagCriteria.Category.Insurance Criteria');