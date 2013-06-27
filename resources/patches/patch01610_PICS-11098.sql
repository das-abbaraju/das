ALTER TABLE email_queue ADD subjectViewableBy INT UNSIGNED AFTER viewableBy;
ALTER TABLE email_queue ADD bodyViewableBy INT UNSIGNED AFTER subjectViewableBy;
UPDATE email_queue SET subjectViewableBy = viewableBy, bodyViewableBy = viewableBy;