Ext.application({
	name: 'PICS',
	
	launch: function() {
		var baseStore = Ext.create('Ext.data.Store', {
			proxy : {
				type : 'ajax',
				url : reportURL,
				reader : {
					type : 'json',
					root : 'data'
				}
			},
			autoLoad : true,
			fields : storeFields
		});
		
		// Configuration

		Ext.define('PICS.view.ui.ReportGrid', {
			extend: 'Ext.grid.Panel',
			alias: ['widget.reportgrid'],
			store : baseStore,
			columns : gridColumns
		});

		Ext.define('PICS.view.ui.ReportOptions', {
			extend: 'Ext.panel.Panel',
			alias: ['widget.reportoptions'],
		    layout: {
		    	type: 'accordion'
		    },
		    collapsed: true,
		    collapsible: true,
		    resizable: {
		    	handles: 'e'
		    },
		    title: 'Report Options',
		    items: [{
		    	title: 'Columns'
		    }, {
		    	title: 'Filters'
		    }, {
		    	title: 'Sort'
		    }, {
		    	title: 'Share'
		    }, {
		    	title: 'Save',
		    	html: '<a href="ReportDynamic!data.action?report=' + reportID + '" target="reportData">See JSON Data</a>'
		    }]
		});

		Ext.create('Ext.container.Viewport', {
			title: 'Main',
			layout: {
				type: 'border'
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
                	type: 'border',
                },
				items: [{
					id: 'aside',
					region: 'west',
					width: 300,
					xtype: 'reportoptions'
				}, {
					region: 'center',
					xtype: 'tabpanel',
					title: 'Recently Added Contractors',
					items: [{
						title: 'Grid',
						xtype: 'reportgrid'
					}, {
						title: 'Chart'
					}]
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
        					items: reportMenu
        				}
        			}]
				}]
			}, {
				id: 'footer',
				xtype: 'toolbar',
				region: 'south',
				height: 30,
				border: false,
				html: '&copy; 2012'
			}]
		});
	}
});