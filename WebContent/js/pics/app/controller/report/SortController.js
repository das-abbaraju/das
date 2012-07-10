Ext.define('PICS.controller.report.SortController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'sortGrid',
        selector: 'reportoptionssorts gridpanel'
    }],
    
    init: function() {
        this.control({
            'reportoptionssorts gridpanel':  {
                itemclick: this.changeSortDirection
            },
            'reportoptionssorts button[action=apply]': {
                click: function () {
                    this.application.fireEvent('refreshreport');
                }                
            }
        });
    },
    changeSortDirection: function (view, record, item, index, event, options) {
        if (record.get('direction') === 'ASC') {
            record.set('direction', 'DESC');
        } else {
            record.set('direction', 'ASC');
        }
        this.getSortGrid().getSelectionModel().deselect(record);
        this.application.fireEvent('refreshreport');
    }
});
