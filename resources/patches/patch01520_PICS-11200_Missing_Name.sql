UPDATE
  users u
SET u.name = CONCAT(u.firstName, ' ', u.lastName)
WHERE u.isGroup='No' AND u.isActive='Yes'
      AND NAME IS NULL;