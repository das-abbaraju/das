-- create google chart widget
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Contractor Flags By Site v2', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=624', 'Column', 'StackedFlags');

-- update to use google chart
UPDATE IGNORE widget_user
SET widgetID = 45
WHERE id = 114;
