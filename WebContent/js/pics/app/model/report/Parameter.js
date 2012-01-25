Ext.define('PICS.model.report.Parameter', {
    extend: 'Ext.data.Model',
    
    hasMany: [{
        model: 'SimpleField', 
        name: 'columns'
    }, {
        model: 'SimpleFilter', 
        name: 'filters'
    }, {
        model: 'SimpleField', 
        name: 'orderBy'
    }],
    
    belongsTo: 'Report'
});