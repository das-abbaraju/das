Ext.define('PICS.model.report.AvailableField', {
	extend: 'Ext.data.Model',

	fields: [
        { name: 'category', type: 'string' }, 
        { name: 'name', type: 'string' }, 
        { name: 'text', type: 'string' }, 
        { name: 'help', type: 'string' },
        { name: 'filterType', type: 'string' }
    ]
});