Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',

    fields : storeFields,
    
    proxy : {
        type : 'ajax',
        url : reportURL,
        reader : {
            type : 'json',
            root : 'data'
        }
    }
});