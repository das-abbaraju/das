Ext.define('PICS.view.report.DataSetGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportdatasetgrid'],
    requires: [
        'PICS.view.report.LinkColumn',
        'PICS.view.report.SortToolbar'
    ],
    store: 'report.DataSets',

    border: 0,
    columns: [{
        xtype: 'rownumberer'
    }],
    dockedItems: [{
        xtype: 'reportsorttoolbar',
        dock: 'top'
    }, {
        xtype: 'pagingtoolbar',
        store: 'report.DataSets',

        cls: 'paging-toolbar',
        displayInfo: true,
        dock: 'top',
        height: 50,
        items: [{
            xtype: 'combo',
            cls: 'rows-per-page',
            editable: false,
            height: 25,
            name: 'rows_per_page',
            store: [
                ['10', '10'],
                ['25', '25'],
                ['50', '50'],
                ['100', '100'],
                ['150', '150'],
                ['200', '200'],
                ['250', '250'],
            ],
            width: 50,
            value: 50
        }, {
            xtype: 'tbtext',
            text: 'Per Page'
        }],

        getPagingItems: function() {
            var me = this;

            return [{
                cls: 'refresh',
                handler: me.doRefresh,
                itemId: 'refresh',
                overflowText: me.refreshText,
                scale: 'large',
                scope: me,
                text: '<i class="icon-refresh icon-large"></i>',
                tooltip: me.refreshText
            }, {
                xtype: 'tbseparator',
                height: 28
            }, {
                cls: 'page-first',
                disabled: true,
                handler: me.moveFirst,
                itemId: 'first',
                overflowText: me.firstText,
                scale: 'large',
                scope: me,
                text: '<i class="icon-fast-backward icon-large"></i>',
                tooltip: me.firstText
            }, {
                cls: 'page-prev',
                disabled: true,
                handler: me.movePrevious,
                itemId: 'prev',
                overflowText: me.prevText,
                scale: 'large',
                scope: me,
                text: '<i class="icon-caret-left icon-large"></i>',
                tooltip: me.prevText
            }, {
                xtype: 'tbseparator',
                height: 28
            },
            me.beforePageText,
            {
                xtype: 'numberfield',
                allowDecimals: false,
                cls: Ext.baseCSSPrefix + 'tbar-page-number',
                enableKeyEvents: true,
                hideTrigger: true,
                // mark it as not a field so the form will not catch it when getting fields
                isFormField: false,
                itemId: 'inputItem',
                keyNavEnabled: false,
                listeners: {
                    scope: me,
                    keydown: me.onPagingKeyDown,
                    blur: me.onPagingBlur
                },
                margins: '-1 2 3 2',
                minValue: 1,
                name: 'inputItem',
                selectOnFocus: true,
                submitValue: false,
                width: me.inputItemWidth
            }, {
                xtype: 'tbtext',
                itemId: 'afterTextItem',
                text: Ext.String.format(me.afterPageText, 1)
            }, {
                xtype: 'tbseparator',
                height: 28
            }, {
                cls: 'page-next',
                disabled: true,
                handler: me.moveNext,
                itemId: 'next',
                overflowText: me.nextText,
                scale: 'large',
                scope: me,
                text: '<i class="icon-caret-right icon-large"></i>',
                tooltip: me.nextText
            }, {
                cls: 'page-last',
                disabled: true,
                handler: me.moveLast,
                itemId: 'last',
                overflowText: me.lastText,
                scale: 'large',
                scope: me,
                text: '<i class="icon-fast-forward icon-large"></i>',
                tooltip: me.lastText
            }, {
                xtype: 'tbseparator',
                height: 28
            }];
        },
    }],
    id: 'data_grid',
    margin: '0 20 20 20'
});