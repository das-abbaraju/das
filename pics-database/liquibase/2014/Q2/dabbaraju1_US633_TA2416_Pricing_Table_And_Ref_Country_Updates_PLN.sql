-- liquibase formatted sql

-- changeset dabbaraju:1
-- For PLN activation and reactivation fee
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 1000, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass in ('Reactivation','Activation');

-- For PLN DocuGUARD
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 500, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='DocuGUARD';


-- For PLN InsureGUARD for 1 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 250, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='InsureGUARD' and ifee.fee='InsureGUARD for 1 Operator';

-- For PLN InsureGUARD for 2-4 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 350, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='InsureGUARD' and ifee.fee='InsureGUARD for 2-4 Operators';

-- For PLN InsureGUARD for 5 to 50+ Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 500, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='InsureGUARD' and ifee.fee not in ('InsureGUARD for 1 Operator','InsureGUARD for 2-4 Operators');

-- For PLN AuditGUARD for 1 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 1500, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='AuditGUARD' and ifee.fee='AuditGUARD for 1 Operator';

-- For PLN AuditGUARD for 2-4 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 3000, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='AuditGUARD' and ifee.fee='AuditGUARD for 2-4 Operators';

-- For PLN AuditGUARD for 5 to 50+ Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 4500, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='AuditGUARD' and ifee.fee not in ('AuditGUARD for 1 Operator','AuditGUARD for 2-4 Operators');


-- For PLN EmployeeGUARD for 1 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 500, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='EmployeeGUARD' and ifee.fee='EmployeeGUARD for 1 Operator';

-- For PLN EmployeeGUARD for 2-4 Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 1000, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='EmployeeGUARD' and ifee.fee='EmployeeGUARD for 2-4 Operators';


-- For PLN EmployeeGUARD for 5 to 50+ Operator
update invoice_fee_country ifc
join invoice_fee ifee on ifee.id=ifc.feeID
set amount = 1500, ifc.updateDate = NOW() where ifc.country ='PL' and ifee.feeClass='EmployeeGUARD' and ifee.fee not in ('EmployeeGUARD for 1 Operator','EmployeeGUARD for 2-4 Operators');

update ref_country
set currency='PLN', updateDate = NOW()
where isoCode='PL';
