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
            ['NotContains', 'does not contain'],
            ['BeginsWith', 'beginswith'],
            ['NotBeginsWith', 'does not begin with'],
            ['EndsWith', 'endswith'],
            ['NotEndsWith', 'does not end with'],
            ['Equals', 'equals'],
            ['NotEquals', 'does not equal'],
            ['Empty', 'blank']
        ]
    },

    controllers: [
        'report.ColumnSelectorController',
        'report.ReportController',
        'report.FilterController',
        'report.SortController'        
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
