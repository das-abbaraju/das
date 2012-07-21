Ext.define('PICS.view.report.filter.base.AutocompleteFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaseautocompletefilter'],

    requires: [
        'Ext.ux.form.BoxSelect'
    ],

    constructor: function () {
        this.callParent(arguments);

        //this.record.set('operator', 'In');

        if (!this.record) {
            // die
        }

        var autocomplete = this.createAutocomplete(this.record);

        this.add(autocomplete);
    },

    createAutocomplete: function (record) {
        var value = record.get('value');
        var store = this.getStoreForAutocomplete();

        return Ext.create('Ext.ux.form.BoxSelect', {
            delimiter: ',',
            displayField: 'name',
            forceSelection: true,
            hideTrigger: true,
            listeners: {
                beforequery: function (queryEvent, options) {
                    //prevent empty queries
                    if (!queryEvent.query) {
                        return false;
                    }
                },
                change: function (obj, newval, oldval, options) {
                    /*newval = newval.replace(/\s/g,"");

                    this.up('autocompletefilter').record.set('value', newval);*/
                }
            },
            margin: '0 5 0 0',
            minChars: 1,
            mode: 'remote',
            multiSelect: true,
            name: 'list_value',
            queryParam: 'searchQuery',
            store: store,
            typeAhead: false,
            value: value,
            valueField: 'searchQuery',
            width: 258
        });
    },

    getStoreForAutocomplete: function (record) {
        var url = Ext.Object.fromQueryString(document.location.search);
        var name = record.get('name');

        return {
            fields: [{
                name: 'id',
                type: 'string'
            }, {
                name: 'name',
                type: 'string'
            }],
            proxy: {
                type: 'ajax',
                url: 'ReportDynamic!list.action?report=' + url.report + '&fieldName=' + name,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            },
            listeners: {
                load: function () {
                    //set valueField to pull from json id
                    //me.child('panel [name=filter_input] boxselect').valueField = 'id';
                }
            }
        };
    }
});