Ext.define('PICS.store.report.Filters', {
    extend: 'PICS.store.report.base.Store',
    model: 'PICS.model.report.Filter2',

    groupField: 'category',
    proxy: {
        reader: {
            root: 'filters',
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