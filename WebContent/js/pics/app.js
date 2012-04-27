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
        COUNTRIES: [
            ['United States', 'US'],
            ['United Kingdom', 'UK'],
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
        TEXTSTORE: [
            ['Contains', 'contains'],
            ['BeginsWith', 'beginswith'],
            ['EndsWith', 'endswith'],
            ['Equals', 'equals'],
            ['Empty', 'blank']
        ]
    },

    controllers: [
        'report.ColumnSelectorController',
        'report.ReportController',
        'report.FilterOptionsController'
    ],

    models: [
        'report.AvailableField',
        'report.Report'
    ],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.DataSets',
        'report.Reports'
    ],

    launch: function () {
        PICS.app = this; //save reference to application
        Ext.create('PICS.view.report.Viewport');
    }
});