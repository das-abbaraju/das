-- create google chart widget
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Flag Color Count by Category v2', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=623', 'Column', 'StackedFlags');

-- update to use google chart
UPDATE IGNORE widget_user
SET widgetID = 44
WHERE id = 56;

