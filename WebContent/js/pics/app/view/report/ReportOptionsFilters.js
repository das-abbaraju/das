Ext.define('PICS.view.report.ReportOptionsFilters', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionsfilters'],
    
    tbar: [{
        text: 'Add Filter',
        
        handler: function () {
            var window = Ext.ComponentQuery.query('reportcolumnselector');
            
            if (!window.length) {
                var window = Ext.create('PICS.view.report.ColumnSelector');
            } else {
                window = window[0];
            }
            
            window.show();
        }
    }],
    title: 'Filters'
});