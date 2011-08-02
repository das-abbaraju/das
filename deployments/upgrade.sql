-- PICS-2926
update contractor_info c
join contractor_audit ca on ca.conID = c.id and ca.auditTypeID = 1
join pqfdata d on d.auditID = ca.id and d.questionID = 2444 and d.dateVerified is not null
set c.safetyRiskVerified = d.dateVerified;

update contractor_info c
join (select ca.conID, min(d.dateVerified) dateVerified from contractor_audit ca
join pqfdata d on d.auditID = ca.id and d.questionID in (7678, 7679) and d.dateVerified is not null
where ca.auditTypeID = 1
group by ca.conID) d2 on d2.conID = c.id
set c.productRiskVerified = d2.dateVerified;
-- END