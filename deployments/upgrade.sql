-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-5567 COHS Stattistics table
update pqfdata pd 
join contractor_audit ca on ca.id = pd.auditID 
join pqfdata pd2 on pd2.auditID = pd.auditID 
LEFT join pqfdata pd3 on pd3.auditID = pd.auditID 
set pd3.answer='0.00' 
where pd.questionID=8840 and pd.answer='No' 
and pd2.questionID=2066 and pd2.answer='Yes' 
and (pd3.questionID=11117 OR pd3.questionID=11118);

-- PICS-5758
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AM');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AZ');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BA');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BG');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BY');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'CH');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'CY');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'CZ');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'DK');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'EE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'EL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'ES');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'FI');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'GE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'HR');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'HU');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'IE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'IS');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LI');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LU');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LV');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'MD');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'ME');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'MK');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'MT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'NL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'NO');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'PL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'PT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'RO');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'RS');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'SE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'SI');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'SK');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'TR');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'UA');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'XK');