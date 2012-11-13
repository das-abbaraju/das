-- update affected audits to be recalculated
Update contractor_audit ca
join pqfdata pd on pd.auditID=ca.id
set ca.lastRecalculation=NULL
where pd.questionID=8810
and pd.answer is not null 
and pd.answer like '%,%';

-- update affected answers
UPDATE pqfdata pd
set pd.answer=replace(pd.answer, ',', '')
where pd.questionID=8810
and pd.answer is not null 
and pd.answer like '%,%';
