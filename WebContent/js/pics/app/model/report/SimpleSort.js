Ext.define('PICS.model.report.SimpleSort', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'column',
        type: 'string'
    }, {
        name: 'ascending',
        type: 'boolean'
    }],
    
    belongsTo: 'Report'
});