Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],
    
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
                text: 'Safety Pro Invoices'
            }, {
                text: 'Create Safety Pro Invoices'
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
    }, {
        text: 'See JSON Data',
        url: 'ReportDynamic!data.action?report=' + reportID
    }]
});