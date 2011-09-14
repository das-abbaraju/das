update widget set chartType = 'Pie2D' where widgetID = 23;

-- PICS-2801
update flag_criteria set category="Insurance AMB Class" where label like '%Class%';
update flag_criteria set category="Insurance AMB Rating" where label like '%Rating%';
update flag_criteria set optionCode='ExcessEachOccurrence' where category="Insurance Criteria" and description like '%plus Excess Each Occurrence%';
update flag_criteria set optionCode='ExcessAggregate' where category="Insurance Criteria" and description like '%plus Excess Aggregate%';
--