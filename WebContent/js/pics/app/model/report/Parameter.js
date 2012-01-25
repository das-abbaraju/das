Ext.define('PICS.model.report.Parameter', {
    extend: 'Ext.data.Model',
    
    fields: ['id', 'report_id']
    
    hasMany: [{
        model: 'SimpleField', 
        name: 'columns'
    }, {
        model: 'SimpleFilter', 
        name: 'filters'
    }, {
        model: 'SimpleSort', 
        name: 'sorts'
    }],
    
    belongsTo: 'Report'
});