Ext.define('PICS.model.report.AvailableField', {
	extend: 'Ext.data.Model',

	fields: [{
		name: 'category',
		type: 'string'
	}, {
		name: 'name',
		type: 'string'
	}, {
		name: 'text',
		type: 'string'
	}, {
		name: 'description',
		type: 'string'
	}],

	proxy: {
		type: 'ajax',
		url: 'ReportDynamic!availableFields.action?report=7',
		reader: {
			type: 'json',
			root: 'fields'
		}
	}
});