Ext.define('PICS.view.report.filter.FilterTitle', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterfiltertitle'],

    border: 0,
    height: 30,
    layout: {
        type: 'hbox',
        align: 'middle'
    },
    name: 'filter_title',

    constructor: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        var field = this.record.getAvailableField();
        var text = field.get('text');

        var filter_name = this.createFilterName(text);
        var fill = {
            xtype: 'tbfill'
        };
        var filter_remove = this.createFilterRemove();

        this.add([
            filter_name,
            fill,
            filter_remove
        ]);
    },

    createFilterName: function (name) {
        if (name.length >= 29) {
            name = name.substring(0, 29) + '...';
        }

        return {
            xtype: 'displayfield',
            cls: 'filter-name',
            name: 'filter_name',
            value: name
        };
    },

    createFilterRemove: function () {
        return {
            xtype: 'button',
            action: 'remove-filter',
            cls: 'remove-filter',
            height: 16,
            text: '<i class="icon-remove-sign"></i>',
            tooltip: 'Remove',
            width: 16
        };
    }
});