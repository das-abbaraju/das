Ext.define('PICS.store.report.Filters', {
    extend: 'PICS.store.report.base.Store',
    model: 'PICS.model.report.Filter2',

    groupField: 'category',
    proxy: {
        reader: {
            root: 'filters',
            type: 'json'
        },
        type: 'memory'
    },
    sorters: [{
        property: 'category',
        direction: 'ASC'
    }]
});