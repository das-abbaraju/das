-- PICS-7109 Give Low Risk to contractors with transportation services
update contractor_info ci
join accounts a on ci.id=a.id
set ci.transportationRisk=1
where ci.transportationRisk=0
and a.transportationServices=1;