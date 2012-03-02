Ext.define('PICS.model.report.SimpleColumn', {
	extend: 'Ext.data.Model',

	fields: [
        { name: 'name', type: 'string' },
        { name: 'method', type: 'string' },
        { name: 'option', type: 'string' },
        { name: 'renderer' }
    ],
    
	belongsTo: {
	    model: 'PICS.model.report.Report',
	    getterName: 'getReport',
	    setterName: 'setReport'
	}
});