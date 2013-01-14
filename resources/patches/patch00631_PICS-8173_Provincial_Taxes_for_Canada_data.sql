-- inserts into app_translation have already been run on config

-- Add an invoice_fee_country row for each Canadian province pointing to the applicable invoice_fee.
-- Note that British Columbia and Prince Edward Island have an additional row for changes effective Apr 1, 2013.
insert into `invoice_fee_country`
(`feeID`, `country`, `subdivision`, `amount`, `ratePercent`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `effectiveDate`)
values
('210','CA','CA-AB','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-BC','0','7','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('211','CA','CA-BC','0','7','20952','20952',now(),NOW(),'2013-04-01 00:00:00'),
('211','CA','CA-MB','0','7','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-NB','0','8','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-NL','0','8','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('210','CA','CA-NT','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-NS','0','10','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('210','CA','CA-NU','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-ON','0','8','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('211','CA','CA-PE','0','10.5','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-PE','0','9','20952','20952',now(),NOW(),'2013-04-01 00:00:00'),
('212','CA','CA-QC','0','9.975','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('211','CA','CA-SK','0','5','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('210','CA','CA-YT','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00');

