Ext.application({
	name: 'PICS',
	
	//autoCreateViewport: true,
	launch: function() {
		Ext.create('Ext.container.Viewport', {
			title: 'Main',
			layout: {
				type: 'border',
				padding: 5
			},
			items: [{
				id: 'header',
				xtype: 'box',
				region: 'north',
				height: 35,
				border: false,
				html: '<header><img src="http://localhost:8080/picsWeb2/images/logo_sm.png" /></header>'
			}, {
				id: 'content',
                region: 'center',
                layout: {
                	type: 'vbox',
                	align: 'stretch'
                },
                bodyPadding: 5,
				items: [{
					border: false,
					height: 50,
					html: '<h1>Reports</h1>'
				}, {
					layout: {
	                	type: 'vbox',
	                	align: 'stretch'
	                },
					bodyPadding: 5,
					collapsible: true,
					height: 200,
					title: 'Search',
					items: [{
						xtype: 'fieldcontainer',
						layout: 'hbox',
						defaults: {
							height: 22,
							labelWidth: 'auto',
							margin: '0 10 0 0'
						},
						items: [{
							xtype: 'textfield',
							fieldLabel: 'Company Name',
							name: 'theField'
						}, {
							xtype: 'textfield',
							fieldLabel: 'Status',
							name: 'theField'
						}, {
							xtype: 'textfield',
							fieldLabel: 'Account Level',
							name: 'theField'
						}]
					}, {
						xtype: 'fieldcontainer',
						layout: 'hbox',
						defaults: {
							height: 22,
							labelWidth: 'auto',
							margin: '0 10 0 0'
						},
						items: [{
							xtype: 'textfield',
							fieldLabel: 'Name',
							name: 'theField'
						}, {
							xtype: 'textfield',
							fieldLabel: 'Name',
							name: 'theField'
						}, {
							xtype: 'textfield',
							fieldLabel: 'Name',
							name: 'theField'
						}]
					}]
				}, {
					bodyPadding: 5,
					border: false,
					html: 'list'
				}],
				dockedItems: [{
					xtype: 'toolbar',
					dock: 'top',
					height: 30,
					items: [{
						text: 'Home',
						url: 'http://www.google.com'
					}, {
		        		text: 'Contractors',
		        		menu: {
		        			items: [{
		        				text: 'Contractor List'
		        			}, {
		        				text: 'Registtration Requests'
		        			}, {
		        				text: 'Archived Accounts'
		        			}, {
		        				text: 'Delinquent Accounts'
		        			}, {
		        				text: 'Search By Question'
		        			}, {
		        				text: 'Activity Watch'
		        			}]
		        		}
        			}, {
		        		text: 'AuditGUARD&trade;',
		        		menu: {
		        			items: [{
		        				text: 'Saftey Pro Invoices'
		        			}, {
		        				text: 'Create Saftey Pro Invoices'
		        			}, {
		        				text: 'Audit List Compress'
		        			}, {
		        				text: 'Audit List'
		        			}, {
		        				text: 'Audit List By Status'
		        			}, {
		        				text: 'Schedule & Assign'
		        			}, {
		        				text: 'Obsolete Schedule Audits'
		        			}, {
		        				text: 'Close Assigned Audits'
		        			}, {
		        				text: 'Audit Calendar'
		        			}, {
		        				text: 'Answer Updates'
		        			}, {
		        				text: 'Auditor Assignments'
		        			}]
		        		}
        			}, {
		        		text: 'Customer Service',
		        		menu: {
		        			items: [{
		        				text: 'Assign Contractors'
		        			}, {
		        				text: 'Manage Webcams'
		        			}, {
		        				text: 'Assign Webcams'
		        			}, {
		        				text: 'Pending PQF'
		        			}, {
		        				text: 'PQF Verification'
		        			}, {
		        				text: 'CSR Assignment'
		        			}]
		        		}
        			}, {
		        		text: 'Accounting',
		        		menu: {
		        			items: [{
		        				text: 'Billing Report'
		        			}, {
		        				text: 'Unpaid Invoices Report'
		        			}, {
		        				text: 'Invoice Search Report'
		        			}, {
		        				text: 'Expired CC Report'
		        			}, {
		        				text: 'Lifetime Member Report'
		        			}, {
		        				text: 'QuickBooks Sync'
		        			}, {
		        				text: 'QuickBooks Sync Canada'
		        			}]
		        		}
        			}, {
		        		text: 'InsureGUARD&trade;',
		        		menu: {
		        			items: [{
		        				text: 'Contractor Policies'
		        			}, {
		        				text: 'Policy Verification'
		        			}]
		        		}
        			}, {
		        		text: 'Management',
		        		menu: {
		        			items: [{
		        				text: 'Manage Accounts'
		        			}, {
		        				text: 'Manage User Accounts'
		        			}, {
		        				text: 'Permissions Matrix'
		        			}, {
		        				text: 'Manage Employees'
		        			}, {
		        				text: 'Email Subscriptions'
		        			}, {
		        				text: 'Email Wizard'
		        			}, {
		        				text: 'Email Webinar'
		        			}, {
		        				text: 'Email Queue'
		        			}, {
		        				text: 'Email Error Report'
		        			}, {
		        				text: 'Resources'
		        			}, {
		        				text: 'Sales Report'
		        			}, {
		        				text: 'Edit Profile'
		        			}, {
		        				text: 'My Schedule'
		        			}, {
		        				text: 'Debug OFF'
		        			}]
		        		}
        			}, {
        				text: 'Configuration',
        				menu: {
        					items: [{
        						text: 'Audit Category Matrix'
        					}, {
        						text: 'Audit Definition'
        					}, {
        						text: 'Audit Type Rules'
        					}, {
        						text: 'Category Rules'
        					}, {
        						text: 'Contractor Simulator'
        					}, {
        						text: 'Email Exclusions List'
        					}, {
        						text: 'Email Template Editor'
        					}, {
        						text: 'Flag Criteria'
        					}, {
        						text: 'Import/Export Translations'
        					}, {
        						text: 'Manage Audit Options'
        					}, {
        						text: 'Manage Translations'
        					}, {
        						text: 'Manage Workflow'
        					}, {
        						text: 'Trade Taxonomy'
        					}]
        				}
        			}, {
        				text: 'Developer Tools',
        				menu: {
        					items: [{
        						text: 'System Logging'
        					}, {
        						text: 'Page Logging'
        					}, {
        						text: 'Clear Cache'
        					}, {
        						text: 'Cache Statistics'
        					}, {
        						text: 'Cron'
        					}, {
        						text: 'Contractor Cron'
        					}, {
        						text: 'Contractor/Operator Flag Differences'
        					}, {
        						text: 'Mail Cron'
        					}, {
        						text: 'Subscription Cron'
        					}, {
        						text: 'Server Information'
        					}, {
        						text: 'Audit Schedule Builder'
        					}, {
        						text: 'Huntsman Sync'
        					}, {
        						text: 'CSS Style Guide'
        					}, {
        						text: 'Manage App Properties'
        					}, {
        						text: 'Exception Log'
        					}, {
        						text: 'Batch Insert Translations'
        					}]
        				}
        			}, {
        				text: 'Reports',
        				menu: {
        					items: [{
        						text: 'Audit Analysis'
        					}, {
        						text: 'CSR Tracking'
        					}, {
        						text: 'CSR Contractor Count'
        					}, {
        						text: 'CSR Policies Status Count'
        					}, {
        						text: 'Audit Rule History'
        					}, {
        						text: 'Contractor Licenses'
        					}, {
        						text: 'Contractor Score'
        					}, {
        						text: 'EMR Rates Graph'
        					}, {
        						text: 'EMR Rates Report'
        					}, {
        						text: 'Incidence Rates Graph'
        					}, {
        						text: 'Incidence Rates Report'
        					}, {
        						text: 'Contractor Trade Conflicts'
        					}, {
        						text: 'User Multi-Login'
        					}, {
        						text: 'User Search'
        					}, {
        						text: 'Washington Audit'
        					}, {
        						text: 'Employee List'
        					}, {
        						text: 'Flag Changes'
        					}, {
        						text: 'Assessment Tests'
        					}, {
        						text: 'Report WCB Accounts'
        					}]
        				}
        			}]
				}]
			}, {
				id: 'footer',
				xtype: 'box',
				region: 'south',
				height: 100,
				border: false,
				html: '<footer><ul><li>Footer</li></ul></footer>'
			}]
		});
	}
});