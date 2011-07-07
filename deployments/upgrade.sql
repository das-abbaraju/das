update audit_type set scoreType='Percent' where scoreable;

update invoice_fee set visible = 0 where id in (3,297,299,301,309,317,325);