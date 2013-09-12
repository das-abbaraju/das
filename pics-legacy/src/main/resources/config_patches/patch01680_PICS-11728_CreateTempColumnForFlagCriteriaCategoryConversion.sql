alter table flag_criteria add column new_category varchar(50);

update flag_criteria set new_category = REPLACE(`category` , ' ' , '' );

create index new_category on flag_criteria (new_category);

insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable)
  values
  ('FlagCriteria.Category.InsuranceAMBClass', "en", "Insurance AMB Class", 64036,64036,now(),now(),2,1),
  ('FlagCriteria.Category.InsuranceAMBRating', "en", "Insurance AMB Rating", 64036,64036,now(),now(),2,1),
  ('FlagCriteria.Category.InsuranceCriteria', "en", "Insurance Criteria", 64036,64036,now(),now(),2,1);
