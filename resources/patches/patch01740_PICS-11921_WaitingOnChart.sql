-- create google chart widget
INSERT IGNORE INTO widget (caption, widgetType, synchronous, url, googleChartType, googleStyleType)
VALUES ('Waiting On Totals v2', 'GoogleChart', 0, 'ReportApi!chart.action?reportId=620', 'Pie', 'Basic');

-- update to use google chart
UPDATE IGNORE widget_user
SET widgetID = 43
WHERE id = 42;

