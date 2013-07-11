ALTER TABLE widget
ADD COLUMN googleChartType varchar(50) COLLATE utf8_general_ci NULL after chartType,
ADD COLUMN googleStyleType varchar(50) COLLATE utf8_general_ci NULL after googleChartType;