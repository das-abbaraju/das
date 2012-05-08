Ext.define('PICS.controller.report.SortController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'sortButtons',
        selector: 'sortbuttons'
    }, {
        ref: 'dataSetGrid',
        selector: 'reportdatasetgrid'
    }],

    stores: [
        'report.Reports'
    ],

    init: function () {
        this.control({
            'sortbuttons button[action=sort-report]': {
                click: this.sortReport
            },
            'sortbuttons button[action=remove-sort]': {
                click: this.removeSort
            }
        });

        this.application.on({
            refreshsorts: this.refreshSorts,
            scope: this
        });
    },

    addReportSorts: function () {
        var items = [],
            sortStore = this.getReportReportsStore().first().sorts(),
            toolbar = this.getSortButtons();

        sortStore.each(function (record) {
            
            //sort button
            var button = {
                action: 'sort-report',
                text: record.get('name'),
                icon: '../js/pics/resources/themes/images/default/grid/sort_asc.gif',
                iconAlign: 'right',
                record: record
            };
            if (record.get('direction') === 'DESC') {
                button.icon = '../js/pics/resources/themes/images/default/grid/sort_desc.gif';
            }
            items.push(button);
            
            //remove sort button
            var remove = {
                xtype: 'button',
                action: 'remove-sort',
                icon: 'images/cross.png',
                iconCls: 'remove-filter',
                record: record,
                tooltip: 'Remove'
            };
            items.push(remove);
        });
        toolbar.add(items);

    },

    refreshSorts: function () {
        this.getSortButtons().removeAll();
        this.addReportSorts();
    },
    
    removeSort: function (component) {
        var sortStore = this.getReportReportsStore().first().sorts(),
            toolbar = this.getSortButtons();
        
        sortStore.remove(component.record);
        
        this.refreshSorts();
        
        PICS.app.fireEvent('refreshreport');
    },

    sortReport: function (component) {
        if (component.record.get('direction') === 'ASC') {
            component.setIcon('../js/pics/resources/themes/images/default/grid/sort_desc.gif');
            component.record.set('direction', 'DESC')
        } else {
            component.setIcon('../js/pics/resources/themes/images/default/grid/sort_asc.gif');
            component.record.set('direction', 'ASC')
        }

        PICS.app.fireEvent('refreshreport');
    }

});