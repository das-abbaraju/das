-- change emtpy tagit "[]" to empty answer ""
UPDATE pqfdata pd
SET pd.answer=""
where pd.answer="[]";