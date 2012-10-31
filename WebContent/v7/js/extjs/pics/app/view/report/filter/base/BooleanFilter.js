Ext.define('PICS.view.report.filter.base.BooleanFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasebooleanfilter'],

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid filter record');
        }

        var checkbox = this.createCheckbox(this.record);

        this.add(checkbox);
    },

    createCheckbox: function (record) {
        var value = record.get('not');

        return {
            xtype: 'checkbox',
            boxLabel: 'True',
            inputValue: true,
            name: 'filter_value',
            uncheckedValue: false,
            value: value
        };
    }
});