Ext.define('PICS.model.report.SimpleSort', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'column', type: 'string' },
        { name: 'direction', type: 'string' }
    ],
    
    belongsTo: {
        model: 'PICS.model.report.Report',
        getterName: 'getReport',
        setterName: 'setReport'
    }
});