-- create google chart widget
-- 23 to 42 (Report #603) for corporate
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Contractor Flag Summary v2', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=603', 'Pie', 'Flags');
        
-- create a second operator user with google charts instead of fushion charts
INSERT IGNORE INTO widget_user (widgetID, userID, expanded, `COLUMN`, sortOrder)
SELECT widgetID, 1728, expanded, `COLUMN`, sortOrder FROM widget_user WHERE userID = 646;

UPDATE IGNORE widget_user
SET widgetID = 39
WHERE widgetID = 5 AND userID = 1728;

UPDATE IGNORE widget_user
SET widgetID = 42
WHERE widgetID = 23 AND userID = 1728;

