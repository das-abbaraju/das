Ext.define('PICS.view.report.filter.base.Boolean', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbaseboolean',
    
    createOperatorField: function () {
        return {
            xtype: 'hiddenfield',
            name: 'operator'
        };
    },

    createValueField: function () {
        return {
            xtype      : 'fieldcontainer',
            defaultType: 'radiofield',
            defaults: {
                flex: 1,
                name: 'value',
                margin: '0 10 0 0'
            },
            layout: 'hbox',
            items: [
                {
                    boxLabel  : 'Yes',
                    inputValue: true,
                }, {
                    boxLabel  : 'No',
                    inputValue: false
                }, {
                    boxLabel  : 'All',
                    inputValue: ''
                }
            ]
        };
    }
});