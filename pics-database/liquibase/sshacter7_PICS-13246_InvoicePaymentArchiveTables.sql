--liquibase formatted sql
--changeset sshacter:7

CREATE TABLE IF NOT EXISTS	log_event
(
  id SERIAL,	-- id bigINT(20) NOT NULL AUTO_INCREMENT,
  logStart DATETIME NOT NULL 	COMMENT "Log transaction date.",
  logFinish DATETIME NOT NULL	COMMENT "Log transaction finish date for period history.",
  validStart DATE NOT NULL 	COMMENT "Start date this fact was valid in reality.",
  validFinish DATE NOT NULL 	COMMENT "Finish date this fact was valid in reality.",
  dmlType ENUM("INSERT", "UPDATE", "DELETE", "VIEW")	COMMENT "Type of data manipulation (INSERT, UPDATE, DELETE, VIEW).",
  ddlName VARCHAR(128) DEFAULT NULL 	COMMENT "Name of data definition (table) being logged.",
  ddlKey INT(11) NOT NULL 	COMMENT "The primary key value of the table being logged (PK from ddlName).",
  logSeq INT(11) DEFAULT NULL 	COMMENT "The log sequence number for contiguous history.",
  userName VARCHAR(128) NOT NULL 	COMMENT "The user logging the changes.",
  logYear INT(11) DEFAULT NULL 	COMMENT "Log transaction date year.",
  logMonth INT(11) DEFAULT NULL 	COMMENT "Log transaction date month.",
  logWeek INT(11) DEFAULT NULL 	COMMENT "Log transaction date week.",
  logDay INT(11) DEFAULT NULL 	COMMENT "Log transaction date day.",
  logQtr INT(11) DEFAULT NULL 	COMMENT "Log transaction date quarter.",
  logEntry TEXT DEFAULT NULL 	COMMENT "The XML description of changes."
) ENGINE=ARCHIVE AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_invoice
(
  logID BIGINT(20) NOT NULL,
  id INT(11) NOT NULL,
  accountID MEDIUMINT(8) UNSIGNED NULL,
  tableType CHAR(1) NULL ,
  invoiceType VARCHAR(20) NULL,
  createdBy INT(11) NULL,
  updatedBy INT(11) NULL,
  creationDate DATETIME NULL,
  updateDate DATETIME NULL,
  dueDate DATE NULL,
  STATUS VARCHAR(10)  NULL,
  totalAmount DECIMAL(9,2) NULL,
  amountApplied DECIMAL(9,2) NULL,
  commissionableAmount DECIMAL(9,2) NULL ,
  paidDate DATETIME NULL,
  paymentMethod VARCHAR(30) NULL,
  checkNumber VARCHAR(50) NULL,
  transactionID VARCHAR(50) NULL,
  poNumber VARCHAR(20) NULL,
  ccNumber VARCHAR(20) NULL,
  qbSync TINYINT(4) NOT NULL ,
  sapLastSync DATETIME ,
  sapSync TINYINT(1) ,
  sapID VARCHAR(25) NULL,
  qbListID VARCHAR(25) NULL,
  qbSyncWithTax TINYINT(4) NULL ,
  notes TEXT CHARACTER SET utf8,
  currency CHAR(3) ,
  lateFeeInvoiceID INT(11) NULL
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_invoice_item
(
  logID BIGINT(20) NOT NULL,
  id INT(11) NOT NULL,
  invoiceID INT(11) NOT NULL,
  feeID INT(11) NOT NULL,
  amount DECIMAL(9,2) NOT NULL ,
  description VARCHAR(100) DEFAULT NULL,
  createdBy INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  paymentExpires DATE DEFAULT NULL,
  qbRefundID VARCHAR(25) CHARACTER SET latin1 DEFAULT NULL,
  refunded TINYINT(4) DEFAULT NULL,
  refundFor INT(11) DEFAULT NULL,
  transactionType CHAR(1) DEFAULT NULL,
  revenueStartDate DATE DEFAULT NULL,
  revenueFinishDate DATE DEFAULT NULL
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_invoice_commission
(
  logID BIGINT(20) NOT NULL,
  id INT(11) DEFAULT NULL,
  invoiceID INT(11) NOT NULL,
  userID INT(11) NOT NULL,
  createdBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  activationPoints DECIMAL(11,7) DEFAULT NULL,
  accountUserID INT(11) DEFAULT NULL
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_invoice_fee
(
  logID BIGINT(20) NOT NULL,
  id INT(11) NOT NULL,
  fee VARCHAR(100) DEFAULT NULL,
  defaultAmount DECIMAL(9,2) NOT NULL DEFAULT '0.00',
  ratePercent DECIMAL(6,3) DEFAULT '0.000',
  visible TINYINT(4) NOT NULL DEFAULT '1',
  feeClass VARCHAR(50) CHARACTER SET latin1 DEFAULT NULL,
  minFacilities INT(11) NOT NULL DEFAULT '0',
  maxFacilities INT(11) NOT NULL DEFAULT '0',
  qbFullName VARCHAR(75) DEFAULT NULL,
  createdBy INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  effectiveDate DATETIME DEFAULT NULL,
  displayOrder TINYINT(3) UNSIGNED NOT NULL DEFAULT '100',
  commissionEligible TINYINT(1) NOT NULL DEFAULT '0'
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_invoice_fee_country
(
  logID BIGINT(20) NOT NULL,
  id INT(11) NOT NULL,
  feeID INT(11) NOT NULL,
  country VARCHAR(10) NOT NULL,
  subdivision VARCHAR(10) DEFAULT NULL,
  amount DECIMAL(9,2) NOT NULL DEFAULT '0.00',
  ratePercent DECIMAL(6,3) DEFAULT '0.000',
  createdBy INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  effectiveDate DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00',
  expirationDate DATETIME NOT NULL DEFAULT '4000-01-01 23:59:59'
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_invoice_payment
(
  logID BIGINT(20) NOT NULL,
  paymentID INT(10) NOT NULL,
  invoiceID INT(10) DEFAULT NULL,
  refundID INT(10) DEFAULT NULL,
  amount DECIMAL(6,2) NOT NULL DEFAULT '0.00',
  createdBy INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  paymentType CHAR(1) NOT NULL DEFAULT 'I'
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_accounts
(
  logID BIGINT(20) NOT NULL,
  `type` ENUM('Contractor','Operator','Admin','Corporate','Assessment') CHARACTER SET latin1 NOT NULL DEFAULT 'Contractor',
  `name` VARCHAR(50) DEFAULT NULL,
  createdBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  deactivationDate DATETIME DEFAULT NULL,
  deactivatedBy INT(11) DEFAULT NULL,
  `status` VARCHAR(15) CHARACTER SET latin1 NOT NULL DEFAULT 'Pending',
  address VARCHAR(50) DEFAULT NULL,
  address2 VARCHAR(50) DEFAULT NULL,
  address3 VARCHAR(50) DEFAULT NULL,
  city VARCHAR(35) DEFAULT NULL,
  countrySubdivision VARCHAR(10) CHARACTER SET latin1 DEFAULT NULL,
  zip VARCHAR(15) CHARACTER SET latin1 DEFAULT NULL,
  country VARCHAR(25) CHARACTER SET latin1 DEFAULT NULL,
  phone VARCHAR(30) CHARACTER SET latin1 DEFAULT NULL,
  phone2 VARCHAR(35) CHARACTER SET latin1 DEFAULT NULL,
  fax VARCHAR(30) CHARACTER SET latin1 DEFAULT NULL,
  contactID MEDIUMINT(9) DEFAULT NULL,
  email VARCHAR(50) DEFAULT NULL,
  web_URL VARCHAR(50) DEFAULT NULL,
  mainTradeID INT(11) DEFAULT NULL,
  industryID INT(11) DEFAULT NULL,
  industry VARCHAR(50) CHARACTER SET latin1 DEFAULT NULL,
  naics VARCHAR(10) CHARACTER SET latin1 NOT NULL DEFAULT '0',
  naicsValid TINYINT(4) NOT NULL DEFAULT '0',
  dbaName VARCHAR(400) DEFAULT NULL,
  nameIndex VARCHAR(50) DEFAULT NULL,
  reason VARCHAR(100) DEFAULT NULL,
  acceptsBids TINYINT(4) NOT NULL DEFAULT '0',
  description TEXT,
  requiresOQ TINYINT(4) UNSIGNED NOT NULL DEFAULT '0',
  requiresCompetencyReview TINYINT(4) UNSIGNED NOT NULL DEFAULT '0',
  needsIndexing TINYINT(4) UNSIGNED NOT NULL DEFAULT '1',
  onsiteServices TINYINT(4) UNSIGNED NOT NULL DEFAULT '0',
  transportationServices TINYINT(4) UNSIGNED NOT NULL DEFAULT '0',
  offsiteServices TINYINT(4) UNSIGNED NOT NULL DEFAULT '0',
  materialSupplier TINYINT(4) UNSIGNED NOT NULL DEFAULT '0',
  generalContractor TINYINT(4) NOT NULL DEFAULT '0',
  autoApproveRelationships TINYINT(4) NOT NULL DEFAULT '1',
  accreditation DATE DEFAULT NULL,
  parentID INT(11) DEFAULT NULL,
  currencyCode CHAR(3) CHARACTER SET latin1 DEFAULT 'USD',
  qbListID VARCHAR(25) CHARACTER SET latin1 DEFAULT NULL,
  qbListCAID VARCHAR(25) CHARACTER SET latin1 DEFAULT NULL,
  qbListUKID VARCHAR(25) CHARACTER SET latin1 DEFAULT NULL,
  qbListEUID VARCHAR(25) CHARACTER SET latin1 DEFAULT NULL,
  qbSync TINYINT(4) NOT NULL DEFAULT '1',
  sapLastSync DATETIME DEFAULT NULL,
  sapSync TINYINT(1) DEFAULT '0',
  locale VARCHAR(5) CHARACTER SET latin1 DEFAULT 'en',
  timezone VARCHAR(50) CHARACTER SET latin1 DEFAULT NULL,
  rememberMeTime TINYINT(4) DEFAULT '-1',
  sessionTimeout TINYINT(3) UNSIGNED DEFAULT '60',
  rememberMeTimeEnabled TINYINT(4) DEFAULT '1',
  passwordSecurityLevelId TINYINT(4) DEFAULT '0'
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_users
(
  logID BIGINT(20) NOT NULL,
  username VARCHAR(100) DEFAULT NULL,
  `password` VARCHAR(100) DEFAULT NULL,
  isGroup ENUM('Yes','No') CHARACTER SET latin1 NOT NULL DEFAULT 'No',
  email VARCHAR(100) DEFAULT NULL,
  firstName VARCHAR(50) DEFAULT NULL,
  lastName VARCHAR(50) DEFAULT NULL,
  `name` VARCHAR(100) DEFAULT NULL,
  isActive ENUM('Yes','No') CHARACTER SET latin1 NOT NULL DEFAULT 'Yes',
  lastLogin DATETIME DEFAULT NULL,
  accountID INT(11) NOT NULL,
  passwordHistory VARCHAR(1000) CHARACTER SET latin1 DEFAULT NULL,
  failedAttempts TINYINT(4) NOT NULL DEFAULT '0',
  lockUntil DATETIME DEFAULT NULL,
  resetHash VARCHAR(100) CHARACTER SET latin1 DEFAULT NULL,
  phone VARCHAR(50) CHARACTER SET latin1 DEFAULT NULL,
  fax VARCHAR(15) CHARACTER SET latin1 DEFAULT NULL,
  phoneIndex VARCHAR(11) CHARACTER SET latin1 DEFAULT NULL,
  passwordChanged DATE DEFAULT NULL,
  createdBy INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  emailConfirmedDate DATE DEFAULT NULL,
  timezone VARCHAR(50) CHARACTER SET latin1 DEFAULT NULL,
  forcePasswordReset TINYINT(4) NOT NULL DEFAULT '0',
  needsIndexing TINYINT(4) NOT NULL DEFAULT '1',
  locale VARCHAR(5) CHARACTER SET latin1 DEFAULT 'en',
  department VARCHAR(100) DEFAULT NULL,
  inheritReportMenuFrom INT(11) DEFAULT NULL,
  usingDynamicReports TINYINT(4) NOT NULL DEFAULT '0',
  usingDynamicReportsDate DATETIME DEFAULT NULL,
  usingVersion7Menus TINYINT(4) NOT NULL DEFAULT '0',
  usingVersion7MenusDate DATETIME DEFAULT NULL,
  reportsManagerTutorialDate DATETIME DEFAULT NULL COMMENT 'Indicates the date that the user was redirected to the tutorial. NULL indicates never.',
  assignmentCapacity SMALLINT(6) DEFAULT NULL,
  shiftStartHour TINYINT(3) UNSIGNED DEFAULT '8',
  shiftEndHour TINYINT(3) UNSIGNED DEFAULT '16',
  workdays CHAR(7) CHARACTER SET latin1 DEFAULT 'xMTWTFx',
  api TINYINT(4) NOT NULL DEFAULT '0',
  apiKey VARCHAR(36) CHARACTER SET latin1 DEFAULT NULL,
  appUserID INT(11) DEFAULT NULL
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;
