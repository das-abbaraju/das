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

    configuration: null,

    controllers: [
        'report.ReportController',
        'report.DataSetController',
        'report.FilterController',
        'report.SortController',
        'report.ColumnSelectorController',
        'report.ReportHeaderController'
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

        this.getConfiguration();
    },

    getConfiguration: function () {
        var url = Ext.Object.fromQueryString(document.location.search);

        Ext.Ajax.request({
           url: 'ReportDynamic!getUserStatus.action?report=' + url.report,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               PICS.app.configuration = (function config() {
                   return {
                       isDeveloper: function () {
                           return result.is_developer;
                       },
                       isOwner: function () {
                           return result.is_owner
                       }
                   }
               })();

               Ext.create('PICS.view.report.Viewport');
           }
        });
    }
});