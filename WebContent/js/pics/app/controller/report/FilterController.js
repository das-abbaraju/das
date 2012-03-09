Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',

    filterStyle: null,
    showOptionsPanel: null,    
    applyFilter: function (view, record, item, index, e, options) {

        console.log(Ext.StoreMgr.lookup("report.Reports"));
        var values = Ext.ComponentQuery.query('reportoptionsfilters ' + this.filterPanelStyle);
        values = values[0];
        //console.log(values.getValues());
    },
    init: function() {
        this.control({
            "reportoptionsfilters gridpanel":  {
                itemclick: this.showFilterOptions
            },
            "reportoptionsfilters button[action=apply]":  {
                click: this.applyFilter
            }         
        });
    },
    filterByBoolean: function (fieldLabel) {
        this.filterPanelStyle = "booleanfilter";
        this.showOptionsPanel = Ext.create('PICS.view.form.BooleanFilter', {
            listeners: {
                beforerender: function () {
                    var items = Ext.ComponentQuery.query("booleanfilter panel")
                    items[0].html = "<h1>" + fieldLabel + "</h1>";
                }
            }
        });
    },
    filterByString: function (fieldLabel) {
        this.filterPanelStyle = "stringfilter";        
        this.showOptionsPanel = Ext.create('PICS.view.form.StringFilter', {
            listeners: {
                beforerender: function () {
                    var items = Ext.ComponentQuery.query("stringfilter panel")
                    items[0].html = "<h1>" + fieldLabel + "</h1>";
                }
            }
        });
    },
    showFilterOptions: function (view, record, item, index, e, options) {
        var optionsPanel = Ext.ComponentQuery.query('reportoptionsfilters #options')[0],
            availableStore = Ext.StoreMgr.lookup('report.AvailableFields'),
            storeRecord = availableStore.findRecord('name', record.get("name")),
            filterType = storeRecord.get('filterType');

        this.filterType = filterType;
        
        /*console.log(availableStore);
        for (x = 0; x < availableStore.data.length; x++){
            console.log("Filter Type: " + availableStore.data.items[x].data.filterType + "; Is Filterable: " + availableStore.data.items[x].data.filterable);
        }*/
        
        if (this.showOptionsPanel != null) {
            this.showOptionsPanel.destroy();    
        }
        
        if (this.filterType === "String") {
            this.filterByString(storeRecord.get("text"), record);
        } else if (this.filterType === "Boolean") {
            this.filterByBoolean(storeRecord.get("text"));
        } else {
            console.log(this.filterType + " is not supported at this time");
        }
        optionsPanel.add(this.showOptionsPanel);        
    }
});
