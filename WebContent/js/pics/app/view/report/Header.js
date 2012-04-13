Ext.define('PICS.view.report.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheader'],

    border: false,
    id: 'reportHeader',
    items: [{
        xtype: 'component',
        autoEl: {
            tag: 'h1',
            html: 'Contractor List'
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        text: 'Save Report'
    }],    
    layout: {
        align: 'middle',
        type: 'hbox'
    },
    margin: '5 0',
    padding: '0 20'
});
