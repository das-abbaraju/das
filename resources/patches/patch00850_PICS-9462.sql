-- update general liability effective date from invalid input
update contractor_audit
set creationDate='2013-01-02 00:00:00', effectiveDate='2013-01-02 00:00:00'
where id=883017;

update pqfdata
set answer='2013-01-02'
where id=15465832;