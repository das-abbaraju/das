Ext.define('PICS.model.report.Reports', {
    extend: 'Ext.data.Model',

    // fields : ,
    
    proxy : {
        type : 'ajax',
        url : reportURL,
        reader : {
            type : 'json'
        }
    }
});