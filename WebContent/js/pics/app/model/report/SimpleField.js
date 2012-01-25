Ext.define('PICS.model.report.SimpleField', {
	extend: 'Ext.data.Model',

	fields: [{
		name: 'field',
		type: 'string'
	}, {
		name: 'function',
		type: 'string'
	}, {
		name: 'option',
		type: 'string'
	}, {
		name: 'ascending',
		type: 'boolean'
	}],
	
	belongsTo: 'Parameter'
});