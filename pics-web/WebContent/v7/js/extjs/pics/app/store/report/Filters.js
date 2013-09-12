Ext.define('PICS.store.report.Filters', {
    extend: 'PICS.store.report.base.Store',
    model: 'PICS.model.report.Filter',

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
    }, {
        property: 'name',
        direction: 'ASC'
    }]
});