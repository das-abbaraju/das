Ext.define('PICS.model.report.SimpleFilter', {
    extend: 'Ext.data.Model',

    belongsTo: {
	    model: 'PICS.model.report.AvailableField',
	    foreignKey: 'field',
	    getterName: 'getAvailableField',
	    setterName: 'setAvailableField'
    },
    fields: [
        { name: 'column', type: 'string' },
        { name: 'not', type: 'boolean', defaultValue: false },
        { name: 'operator', type: 'string' },
        { name: 'column2', type: 'string' },
        { name: 'value', type: 'string' }
    ]
});