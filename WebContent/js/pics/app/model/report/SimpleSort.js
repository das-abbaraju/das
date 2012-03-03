Ext.define('PICS.model.report.SimpleSort', {
    extend: 'Ext.data.Model',

    belongsTo: {
        model: 'PICS.model.report.Report',
        
        getterName: 'getReport',
        setterName: 'setReport'
    },
    fields: [
        { name: 'column', type: 'string' },
        { name: 'ascending', type: 'boolean', defaultValue: true }
    ]
});