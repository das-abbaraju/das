#For PICS-7699

# Scratch this -- update invoice_fee set qbFullName = "VAT" where id = 201;
update invoice_fee set qbFullName = "Tax on sales" where id = 201;
