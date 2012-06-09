Ext.define('PICS.view.report.filter.ListFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.listfilter'],

    record: null,

    constructor: function (data) {
        var me = this;

        this.record = data.record;

        this.record.set('operator', 'In');

        this.callParent(arguments);

        // set filter number
        this.child('displayfield[name=filter_number]').fieldLabel = this.panelNumber;

        // set filter name
        this.child('panel displayfield[name=filter_name]').setValue(this.record.get('name'));

        var url = Ext.Object.fromQueryString(document.location.search);

        Ext.Ajax.request({
            url: 'ReportDynamic!list.action?report=' + url.report + "&fieldName=" + this.record.get('name'),
            success: function (result) {
                var returnedValues = Ext.decode(result.responseText);
                me.createFilterCombo(returnedValues.result);
            }
        });
    },

    createFilterCombo: function (returned_list_values) {
       var list_filter = {
            xtype: 'combo',
            editable: false,
            listeners: {
                change: function (obj, newval, oldval, options) {
                   this.up('listfilter').record.set('value', newval);
                }
            },
            multiSelect: true,
            name: 'filter_value',
            store: this.formatStoreData(returned_list_values),
            value: this.record.get('value'),
            width: 258
        };

       // add filter
       this.child('panel [name=filter_input]').add(list_filter);
    },

    formatStoreData: function (listValues) {
        var listStore = [];

        for (x = 0; x < listValues.length; x++) {
            var currentValue = listValues[x];
            listStore.push([currentValue.id, currentValue.name]);
        }

        return listStore;
    }
});