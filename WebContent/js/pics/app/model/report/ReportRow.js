Ext.define('PICS.model.report.ReportRow', {
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