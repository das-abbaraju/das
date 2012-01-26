Ext.define('PICS.model.report.SimpleField', {
	extend: 'Ext.data.Model',

	fields: [
        { name: 'name', type: 'string' },
        { name: 'method', type: 'string' },
        { name: 'option', type: 'string' }
    ],
    
	belongsTo: {
	    model: 'PICS.model.report.Report',
	    getterName: 'getReport',
	    setterName: 'setReport'
	}
});