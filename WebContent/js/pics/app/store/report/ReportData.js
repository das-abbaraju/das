Ext.define('PICS.store.report.ReportData', {
	extend: 'Ext.data.Store',
	autoLoad: true,
	fields: ['column', 'email', 'value', 'type'],
	data: [
		{column: 'AccountName', email: 'test1@test.com', value: 'Dis', type: 'string'},
		{column: 'Onsite', email: 'test2@test.com', value: 'true', type: 'boolean'},
		{column: 'Country', email: 'test3@test.com', value: 'United States', type: 'country'},
		{column: 'AccountCreation', email: 'test4@test.com', value: '03012012', type: 'date'},
		{column: 'accountCode', email: 'test5@test.com', value: '19123', type: 'number'},
		{column: 'AccountCreation', email: 'test6@test.com', value: '04012012', type: 'date'},
	]
});