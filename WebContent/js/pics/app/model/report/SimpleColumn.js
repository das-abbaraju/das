Ext.define('PICS.model.report.SimpleColumn', {
	extend: 'Ext.data.Model',

	belongsTo: {
	    model: 'PICS.model.report.Report',
	    
	    getterName: 'getReport',
	    setterName: 'setReport'
	},
	fields: [
        { name: 'name', type: 'string' },
        { name: 'text', type: 'string', persist: false }, 
        { name: 'filterType', type: 'string', persist: false },
        { name: 'method', type: 'string' },
        { name: 'option', type: 'string' },
        { name: 'renderer' }
    ]
});