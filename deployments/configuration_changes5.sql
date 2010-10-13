/**
 * adding operator tagsfor adHoc audits  
 */
insert into operator_tag 
select null,concat(at.auditname, '-', ao.opid),1,ao.opID,1098,1098, 
Now(),Now(),0,1
from pics_yesterday.audit_operator ao 
join audit_type at on at.id = ao.auditTypeID
join accounts a on a.id = ao.opid
join pics_alpha.audit_type_rule atr on atr.auditTypeID = ao.auditTypeID and atr.opid = ao.opid
where ao.cansee =1 and ao.minRiskLevel = 0
group by atr.opid, atr.audittypeid 
order by ao.opid , ao.auditTypeID;

select * from operator_tag where length(tag) = 50;

