Ext.define('PICS.model.report.Field', {
	extend : 'Ext.data.Model',

	fields : [ {
		name : 'category',
		type : 'string'
	}, {
		name : 'dataIndex',
		type : 'string'
	}, {
		name : 'text',
		type : 'string'
	}, {
		name : 'description',
		type : 'string'
	} ],

	proxy : {
		type : 'ajax',
		url : reportURL,
		reader : {
			type : 'json',
			root : 'fields'
		}
	}
});