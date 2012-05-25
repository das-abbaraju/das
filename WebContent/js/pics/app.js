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
        ],
        userStatus: null
    },

    controllers: [
        'report.ReportController',
        'report.DataSetController',
        'report.FilterController',
        'report.SortController',
        'report.ColumnSelectorController',
        'report.ReportSaveController'
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

        var url = Ext.Object.fromQueryString(document.location.search);

        Ext.Ajax.request({
           url: 'ReportDynamic!getUserStatus.action?report=' + url.report,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               PICS.app.constants.userStatus = (function config() {
                   return {
                       get_is_developer: function () {
                           return result.is_developer;
                       },
                       get_has_permssion: function () {
                           return result.has_permission;
                       },
                       get_is_owner: function () {
                           return result.is_owner
                       }
                   }
               })();
               Ext.create('PICS.view.report.Viewport');
           }
        });
    }
});
