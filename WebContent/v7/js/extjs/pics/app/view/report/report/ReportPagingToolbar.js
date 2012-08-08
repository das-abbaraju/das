Ext.define('PICS.view.report.report.ReportPagingToolbar', {
    extend: 'Ext.toolbar.Paging',
    alias: ['widget.reportpagingtoolbar'],

    store: 'report.ReportDatas',

    border: 0,
    cls: 'paging-toolbar',
    displayInfo: false,
    enableOverflow: true,
    height: 50,
    id: 'paging_toolbar',
    items: [{
        xtype: 'combobox',
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
            ['250', '250']
        ],
        width: 50,
        value: 50
    }, {
        xtype: 'tbtext',
        id: 'display_info'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        action: 'add-column',
        cls: 'add-column default',
        height: 26,
        text: '<i class="icon-plus icon-large"></i>Add Column'
    }],

    getPagingItems: function() {
        var me = this;

        return [{
            cls: 'refresh',
            handler: me.doRefresh,
            height: 22,
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
            height: 16,
            itemId: 'first',
            overflowText: me.firstText,
            scale: 'large',
            scope: me,
            text: '<i class="icon-step-backward icon-large"></i>',
            tooltip: me.firstText
        }, {
            cls: 'page-prev',
            disabled: true,
            handler: me.movePrevious,
            height: 16,
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
            height: 16,
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
            height: 16,
            itemId: 'last',
            overflowText: me.lastText,
            scale: 'large',
            scope: me,
            text: '<i class="icon-step-forward icon-large"></i>',
            tooltip: me.lastText
        }, {
            xtype: 'tbseparator',
            height: 28
        }];
    },

    updateDisplayInfo: function (count) {
        var msg;

        if (count === 0) {
            msg = this.emptyMsg;
        } else {
            msg = Ext.String.format('displayed of {0}', Ext.util.Format.number(count, '0,000'));
        }

        this.down('#display_info').setText(msg);
    }
});