-- PICS-8894
-- PICS Admin
Insert INTo useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedById)
Values (10, 'AuditDocumentReview', 1, 1, NULL, 1, NOW(), 37745);
-- PICS Auditor
Insert INTo useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedById)
Values (11, 'AuditDocumentReview', 1, 1, NULL, 1, NOW(), 37745);
-- PICS Manager
Insert INTo useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedById)
Values (981, 'AuditDocumentReview', 1, 1, NULL, 1, NOW(), 37745);
-- PICS Safety Professional
Insert INTo useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedById)
Values (65744, 'AuditDocumentReview', 1, 1, NULL, 1, NOW(), 37745);