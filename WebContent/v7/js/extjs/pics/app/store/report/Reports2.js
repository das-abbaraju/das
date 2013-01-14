Ext.define('PICS.store.report.Reports2', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.Report2',

    proxy: {
        reader: {
            root: 'report',
            type: 'json'
        },
        writer: {
            root: 'report',
            type: 'json'
        },
        type: 'memory'
    }
});