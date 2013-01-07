Ext.define('PICS.store.report.Columns', {
    extend: 'PICS.store.report.base.Store',
    model: 'PICS.model.report.Column2',

    groupField: 'category',
    proxy: {
        reader: {
            root: 'columns',
            type: 'json'
        },
        timeout: 10000,
        type: 'ajax',
        url: '/v7/js/extjs/pics/app/data/report.json'
    },
    sorters: [{
        property: 'category',
        direction: 'ASC'
    }]
});