UPDATE accounts a
JOIN contractor_info c ON a.id = c.id
SET a.status = 'Declined'
WHERE a.status = 'Deactivated' AND c.membershipDate IS NULL;