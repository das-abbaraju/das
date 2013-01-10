Ext.define('PICS.store.report.Reports2', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.Report2',

    proxy: {
        reader: {
            root: 'report',
            type: 'json'
        },
        timeout: 10000,
        type: 'ajax',
        url: '/v7/js/extjs/pics/app/data/report.json'
    }
});