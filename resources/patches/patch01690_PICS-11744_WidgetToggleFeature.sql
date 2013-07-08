-- add toggle
INSERT IGNORE INTO app_properties (property, VALUE)
VALUES ('Toggle.ShowGoogleCharts', 'permissions.picsEmployee');

-- create google chart widgets
-- 1 to 595 for operators
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Contractor Count by Flag Color v2', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=595', 'Pie', 'Flags');

-- 5 to 596 for operators
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Contractors by Trade v2', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=596', 'Bar', 'Basic');

-- 37 to 597 for operators
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Operator Flag Year History v2', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=598', 'Column', 'StackedFlags');

-- 1 to 598 for CSRs
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Contractor Count by Flag Color v3', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=599', 'Pie', 'Flags');
        
-- create a second operator user with google charts instead of fushion charts
INSERT IGNORE INTO widget_user (widgetID, userID, expanded, `COLUMN`, sortOrder)
SELECT widgetID, 618, expanded, `COLUMN`, sortOrder FROM widget_user WHERE userID = 616;

-- change over all that different user charts to the google charts we have
UPDATE widget_user
SET widgetID = 38
WHERE widgetID = 1 AND userID = 618;

UPDATE widget_user
SET widgetID = 39
WHERE widgetID = 5 AND userID = 618;

UPDATE widget_user
SET widgetID = 40
WHERE widgetID = 37 AND userID = 618;

-- update pics CSRs to use google chart
UPDATE widget_user
SET widgetID = 41
WHERE id = 48;
