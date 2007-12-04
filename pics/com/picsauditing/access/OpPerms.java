package com.picsauditing.access;

public enum OpPerms {
//This list also must be the same as the enum accessType in userAccess table
	ViewFullPQF("View Financial Info"),
	EditForms("Edit Forms"),
	SearchContractors("Search For New Contractors"),
	AddContractors("Add Contractors"),
	RemoveContractors("Remove Contractors"),
	InsuranceCerts("View Insurance Certificates Report"),
	OfficeAuditCalendar("View Office Audit Calendar"),
	EditFlagCriteria("Edit Red Flag Report Criteria"),
	EditForcedFlags("Edit Forced Flags"),
	EditNotes("Edit Contractor Notes"),
	EditUsers("Add/Edit User Accounts"),
	StatusOnly("Can Only View Statuses");

/*	CREATE TABLE `userAccess` (
			  `userID` mediumint(9) NOT NULL,
			  `accessType` mediumint(9) NOT NULL,
			  `grantedByID` mediumint(9) NOT NULL,
			  `lastUpdate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
			  PRIMARY KEY  (`userID`,`accessType`)
			) ENGINE=MyISAM DEFAULT CHARSET=latin1;
*///	'ViewFullPQF','EditForms','SearchContractors','AddContractors','RemoveContractors','InsuranceCerts','OfficeAuditCalendar','EditFlagCriteria','EditForcedFlags','EditNotes'

	private String description;
	public String getDescription(){
		return description;
	}//getDescription
	OpPerms(String description){
		this.description = description;
	}//OperatorPermissions Constructor
}//OperatorPermissions
