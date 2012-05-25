Ext.define('PICS.view.report.filter.ListFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.listfilter'],

    record: null,

    constructor: function (data) {
        var me = this;

        this.record = data.record;
        
        this.record.set('operator', 'In');

        this.callParent(arguments);

        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('name'));
        
        var url = Ext.Object.fromQueryString(document.location.search);

        Ext.Ajax.request({
           url: 'ReportDynamic!list.action?report=' + url.report + "&fieldName=" + this.record.get('name'),
           success: function (result) {
               var returnedValues = Ext.decode(result.responseText);
               me.createFilterCombo(returnedValues.result);
           }
        });
   },

   createFilterCombo: function (returnedListValues) {
       var listFilter = {
            xtype: 'panel',
            items: [{
                xtype: 'combo',
                editable: false,
                listeners: {
                    change: function (obj, newval, oldval, options) {
                       this.up('listfilter').record.set('value', newval);
                    }
                },
                margin: '0 5 0 0',
                multiSelect: true,
                name: 'listValue',
                store: this.formatStoreData(returnedListValues),
                value: this.record.get('value')
            }],
            layout: 'hbox'
        };
        this.add(listFilter);
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