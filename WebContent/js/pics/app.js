Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'Ext.ux': './js/pics/app/ux'
    }
});

Ext.application({
    name: 'PICS',
    appFolder: 'js/pics/app',

    constants: {
        TEXTSTORE: [
                    ['Contains', 'contains'],
                    ['BeginsWith', 'beginswith'],
                    ['EndsWith', 'endswith'],
                    ['Equals', 'equals'],
                    ['Empty', 'blank']
                ],
        NUMBERSTORE: [
                    ['Equals', '='],
                    ['GreaterThan', '>'],
                    ['LessThan', '<'],
                    ['GreaterThanOrEquals', '>='],
                    ['LessThanOrEquals', '<='],
                    ['Empty', 'blank']
                ],
        COUNTRIES: [
                    ['United States', 'US'],
                    ['United Kingdom', 'UK'],
                    ['Empty', 'blank']
                ]
    },

    controllers: [
        'report.ColumnSelectorController',
        'report.FilterOptionsController',
        'report.ReportController',
        'report.ReportOptionsController'
    ],

    models: [
        'report.AvailableField',
        'report.Report',
        'report.SimpleField',
        'report.SimpleFilter',
        'report.SimpleSort'
    ],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.ReportData',
        'report.Reports',
        'report.ReportsColumn',
        'report.ReportsFilter',
        'report.ReportsSort'
    ],

    launch: function () {
        PICS.app = this; //save reference to application
        Ext.create('PICS.view.report.Viewport');
    }
});