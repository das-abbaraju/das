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
            ['50', '50'],
            ['100', '100'],
            ['250', '250'],
            ['500', '500'],
            ['1000', '1000']
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
        text: '<i class="icon-plus icon-large"></i> Add Column'
    }],

    getPagingItems: function() {
        var me = this;

        return [{
            cls: 'paging-icon',
            height: 22,
            itemId: 'refresh',
            overCls: 'paging-icon-over',
            overflowText: me.refreshText,
            pressedCls: 'paging-icon-pressed',
            scale: 'large',
            scope: me,
            text: '<i class="icon-refresh icon-large"></i>',
            tooltip: me.refreshText
        },{
            xtype: 'tbseparator',
            height: 28
        }, {
            cls: 'paging-icon',
            disabled: true,
            handler: me.moveFirst,
            height: 22,
            itemId: 'first',
            overCls: 'paging-icon-over',
            overflowText: me.firstText,
            pressedCls: 'paging-icon-pressed',
            scale: 'large',
            scope: me,
            text: '<i class="icon-step-backward icon-small"></i>',
            tooltip: me.firstText
        }, {
            cls: 'paging-icon',
            disabled: true,
            handler: me.movePrevious,
            height: 22,
            itemId: 'prev',
            overCls: 'paging-icon-over',
            overflowText: me.prevText,
            pressedCls: 'paging-icon-pressed',
            scale: 'large',
            scope: me,
            text: '<i class="icon-caret-left icon-small"></i>',
            tooltip: me.prevText,
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
            cls: 'paging-icon',
            disabled: true,
            handler: me.moveNext,
            height: 22,
            itemId: 'next',
            overCls: 'paging-icon-over',
            overflowText: me.nextText,
            pressedCls: 'paging-icon-pressed',
            scale: 'large',
            scope: me,
            text: '<i class="icon-caret-right icon-small"></i>',
            tooltip: me.nextText
        }, {
            cls: 'paging-icon',
            disabled: true,
            handler: me.moveLast,
            height: 22,
            itemId: 'last',
            overCls: 'paging-icon-over',
            overflowText: me.lastText,
            pressedCls: 'paging-icon-pressed',
            scale: 'large',
            scope: me,
            text: '<i class="icon-step-forward icon-small"></i>',
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