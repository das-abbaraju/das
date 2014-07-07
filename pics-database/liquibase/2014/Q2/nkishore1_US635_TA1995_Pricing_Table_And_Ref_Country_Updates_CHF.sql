--liquibase formatted sql

--changeset nkishore:1
-- For CHF activation and reactivation fee
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 300, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass in ('Reactivation','Activation');

-- For CHF DocuGUARD
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 150, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='DocuGUARD';


-- For CHF InsureGUARD for 1 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 75, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='InsureGUARD' and ifee.fee='InsureGUARD for 1 Operator';

-- For CHF InsureGUARD for 2-4 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 105, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='InsureGUARD' and ifee.fee='InsureGUARD for 2-4 Operators';

-- For CHF InsureGUARD for 5 to 50+ Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 150, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='InsureGUARD' and ifee.fee not in ('InsureGUARD for 1 Operator','InsureGUARD for 2-4 Operators');

-- For CHF AuditGUARD for 1 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 450, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='AuditGUARD' and ifee.fee='AuditGUARD for 1 Operator';

-- For CHF AuditGUARD for 2-4 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 900, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='AuditGUARD' and ifee.fee='AuditGUARD for 2-4 Operators';

-- For CHF AuditGUARD for 5 to 50+ Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 1350, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='AuditGUARD' and ifee.fee not in ('AuditGUARD for 1 Operator','AuditGUARD for 2-4 Operators');


-- For CHF EmployeeGUARD for 1 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 150, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='EmployeeGUARD' and ifee.fee='EmployeeGUARD for 1 Operator';

-- For CHF EmployeeGUARD for 2-4 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 300, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='EmployeeGUARD' and ifee.fee='EmployeeGUARD for 2-4 Operators';


-- For CHF EmployeeGUARD for 5 to 50+ Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 450, ifc.updateDate = NOW() where ifc.country ='CH' and ifee.feeClass='EmployeeGUARD' and ifee.fee not in ('EmployeeGUARD for 1 Operator','EmployeeGUARD for 2-4 Operators');

update ref_country
set currency='CHF', updateDate = NOW()
where isoCode='CH';
