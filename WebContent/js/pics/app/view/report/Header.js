Ext.define('PICS.view.report.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheader'],

    border: false,
    defaults: {
        border: false
    },
    layout: {
        align: 'middle',
        type: 'hbox'
    },
    items: [{
        flex: 1,
          xtype: 'component',
          autoEl: {
              tag: 'h1',
              html: 'Contractor List'
         },
         style: {
            fontSize: '25px'
         }
    },{
        flex: 3
    },{
        xtype: 'button',
        text: 'Save Report'
    }],
    margin: '5 0',
    padding: '0 20'
});