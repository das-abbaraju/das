-- PICS-7028 (subtask of PICS-6555) 
-- Adding support for contractors in the countries of the European Union 
-- having to supply a VAT number.
  
ALTER TABLE `contractor_info` add column `europeanUnionVATnumber` varchar(15);
