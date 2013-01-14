Ext.define('PICS.store.report.Columns', {
    extend: 'PICS.store.report.base.Store',
    model: 'PICS.model.report.Column2',

    groupField: 'category',
    proxy: {
        reader: {
            root: 'columns',
            type: 'json'
        },
        type: 'memory'
    },
    sorters: [{
        property: 'category',
        direction: 'ASC'
    }]
});