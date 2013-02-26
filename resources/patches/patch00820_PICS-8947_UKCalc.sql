-- force recalculation of AFR (IFR) by setting lastRecalculation to null
update
contractor_audit ca
join pqfdata pd on pd.auditID=ca.id
join audit_question_function af on af.questionID=pd.questionID
set ca.lastRecalculation=NULL
where af.function='IFR'
and pd.answer !='Audit.missingParameter';