Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'filterOptions',
        selector: 'reportoptionsfilters #options'
    }],

    filterStyle: null,
    showOptionsPanel: null,    

    init: function() {
        this.control({
            "reportoptionsfilters gridpanel":  {
                itemclick: this.showFilterOptions
            }         
        });
    },
    showFilterOptions: function (view, record, item, index, e, options) {
        if (this.showOptionsPanel != null) {
            this.showOptionsPanel.destroy();
        }
        
        this.filterType = record.data.field.data.filterType;

        if (this.filterType === "String") {
            this.showOptionsPanel = Ext.create('PICS.view.form.StringFilter');
        } else if (this.filterType === "Boolean") {
            this.showOptionsPanel = Ext.create('PICS.view.form.BooleanFilter');
        } else {
            console.log(this.filterType + " is not supported at this time");
            return;
        }
        this.showOptionsPanel.setRecord(record);
        this.getFilterOptions().add(this.showOptionsPanel);
    }
});
