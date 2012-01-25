Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id'
    }, {
        name: 'modelType'
    }, {
        name: 'summary' // Custom title for report
    }, {
        name: 'description'
    }],
    
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
    
    proxy: {
        type: 'ajax',
        api: {
            read: 'js/pics/data/report.json'
        },
        reader: {
            type: 'json',
            root: 'report'
        }
    }
});