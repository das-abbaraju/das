-- Create new report: id: 406, "Contractor Audit Files", owned by mdo 37951

insert into `report`
(`id`, `ownerID`, `deleted`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `modelType`, `name`, `description`, `parameters`, `filterExpression`, `private`)
  values
  ('406','37951','0','37951','37951',curdate(),curdate(),'ContractorAuditFiles','Contractor Audit Files','Demonstrates the use of the new ContractorAuditFile model.',NULL,'','0');

insert into `report_column`
(`id`, `reportID`, `name`, `sqlFunction`, `width`, `sortIndex`)
  values
  (NULL,'406','AccountName',NULL,'250','1'),
  (NULL,'406','AuditTypeName',NULL,'200','2'),
  (NULL,'406','ContractorAuditFileDescription',NULL,'410','3'),
  (NULL,'406','ContractorAuditFileFileType',NULL,'165','4'),
  (NULL,'406','ContractorAuditFileReviewed',NULL,'246','5'),
  (NULL,'406','ContractorAuditFileUploadDate',NULL,'204','6');

insert into `report_sort`
(`id`, `reportID`, `name`, `ascending`, `sqlFunction`)
  values
  (NULL,'406','AuditExpiresDate','1',NULL);