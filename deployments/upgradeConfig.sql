update widget set chartType = 'Pie2D' where widgetID = 23;

-- PICS-2801
update flag_criteria set category="Insurance AMB Class" where label like '%Class%';
update flag_criteria set category="Insurance AMB Rating" where label like '%Rating%';
update flag_criteria set optionCode='ExcessEachOccurrence' where category="Insurance Criteria" and description like '%plus Excess Each Occurrence%';
update flag_criteria set optionCode='ExcessAggregate' where category="Insurance Criteria" and description like '%plus Excess Aggregate%';
--

-- PICS-3287
update pqfData pd
  join contractor_audit ca
    on ca.id = pd.auditID
  join contractor_audit ca2
    on ca2.conID = ca.conID
set pd.auditID = ca2.id, pd.questionID = (case pd.questionID when 7786 then 9186 when 7787 then 9188 when 7788 then 9187 end)
where (pd.questionID = 7786
        or pd.questionID = 7787
        or pd.questionID = 7788)
    and ca2.auditTypeID = 279;
