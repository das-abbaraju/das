Ext.define('PICS.store.report.Reports', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.Report',

    proxy: {
        reader: {
            root: 'report',
            type: 'json'
        },
        type: 'memory'
    }/*,

    proxyA: function () {},
    proxyB: function () {}*/
});