-- PICS-9449_AddFKtoReportTables.sql

-- report_column FK back to report table
CREATE INDEX FK_report_column ON report_column (reportID);

ALTER TABLE report_column ADD CONSTRAINT `FK_report_column` FOREIGN KEY (`reportID`) REFERENCES `report` (`id`) ON DELETE CASCADE;

-- report_filter FK back to report table
CREATE INDEX FK_report_filter ON report_filter (reportID);

ALTER TABLE report_filter ADD CONSTRAINT `FK_report_filter` FOREIGN KEY (`reportID`) REFERENCES `report` (`id`) ON DELETE CASCADE;

-- report_sort FK back to report table
CREATE INDEX FK_report_sort ON report_sort (reportID);

ALTER TABLE report_sort ADD CONSTRAINT `FK_report_sort` FOREIGN KEY (`reportID`) REFERENCES `report` (`id`) ON DELETE CASCADE;