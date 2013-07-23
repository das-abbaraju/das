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
            xtype      : 'radiogroup',
            defaults: {
                flex: 1,
                name: 'value',
                margin: '0 10 0 0'
            },
            layout: 'hbox',
            items: [
                {
                    boxLabel: PICS.text('Report.execute.booleanFilter.yes'),
                    inputValue: 'true',
                }, {
                    boxLabel: PICS.text('Report.execute.booleanFilter.no'),
                    inputValue: 'false'
                }, {
                    boxLabel: PICS.text('Report.execute.booleanFilter.all'),
                    inputValue: 'all'
                }
            ]
        };
    }
});