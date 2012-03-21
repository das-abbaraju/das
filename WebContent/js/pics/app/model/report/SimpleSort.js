Ext.define('PICS.model.report.SimpleSort', {
    extend: 'Ext.data.Model',

    belongsTo: {
        model: 'PICS.model.report.AvailableField',
        foreignKey: 'field',        
        getterName: 'getAvailableField',
        setterName: 'setAvailableField'
    },
    fields: [
        { name: 'column', type: 'string' },
        { name: 'direction', type: 'string', defaultValue: 'ASC' }
    ]
});