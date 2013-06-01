--     PICS-10791 "Please remove the Bulk FM 2012-Find Alternative Tag (# 829)"

DELETE from contractor_tag where tagId = 829;
DELETE from operator_tag where id = 829 and tag = 'Bulk FM 2012-Find Alternative'; 
