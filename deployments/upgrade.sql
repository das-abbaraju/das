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
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'AL');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'AM');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'AT');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'AZ');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'BA');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'BE');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'BG');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'BY');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'CH');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'CY');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'CZ');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'DK');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'EE');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'EL');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'ES');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'FI');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'GE');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'HR');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'HU');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'IE');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'IS');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'LI');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'LT');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'LU');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'LV');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'MD');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'ME');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'MK');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'MT');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'NL');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'NO');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'PL');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'PT');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'RO');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'RS');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'SE');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'SI');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'SK');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'TR');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'UA');
insert into user_assignment (userID, assignmentType, country) values (26330, 'CSR', 'XK');