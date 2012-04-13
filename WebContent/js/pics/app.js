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
        'report.ReportController'
    ],
    stores: [
        'report.AvailableFields',             
        'report.ReportData'
    ],
    
    launch: function () {
        PICS.app = this; //save reference to application    
        Ext.create('PICS.view.report.Viewport');
    }
});