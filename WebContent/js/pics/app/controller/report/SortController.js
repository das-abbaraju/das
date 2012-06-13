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
                click: this.setSortItemProperties
            },
            'sortbuttons menuitem[action=remove-sort]': {
                click: this.removeSort
            },
            'reportsorttoolbar button[name=downloadexcel]': {
                click: function (component) {
                    var params = PICS.app.getController('report.ReportSaveController').getReportParameters();

                    var reports = this.getReportReportsStore().first();

                    var reportId = reports.getId();

                    var url = 'ReportDynamic!download.action?' + params;

                    document.location.href = url;
                }
            }
        });

        this.application.on({
            refreshsorts: this.refreshSorts,
            scope: this
        });
    },

    addReportStoreSorts: function () {
        var me = this,
            sortStore = this.getReportReportsStore().first().sorts(),
            toolbar = this.getSortButtons();

        sortStore.each(function (record) {
            button = me.createSortButton(record);
            toolbar.add(button);
        });
    },

    //entry point from column controls
    addSortItem: function (columnName, selectedDirection) {
        var sortStore = this.getReportReportsStore().first().sorts(),
            toolbar = this.getSortButtons();

        if (this.sortItemExists(columnName)) {
            this.updateSortItemDirection(columnName, selectedDirection);
        } else {
            var sortItem = Ext.create('PICS.model.report.Sort', {
                'name': columnName,
                'direction': selectedDirection
            });

            sortStore.add(sortItem);

            button = this.createSortButton(sortItem);

            toolbar.add(button);

            PICS.app.fireEvent('refreshreport');
        }
    },

    createSortButton: function (record) {
        var buttons = [];
        var sort_container = Ext.create('Ext.toolbar.Toolbar');

        var sort = {
            xtype: 'splitbutton',
            action: 'sort-report',
            text: record.get('name'),
            height: 26,
            icon: '../js/pics/resources/themes/images/default/grid/sort_asc.gif',
            iconAlign: 'left',
            menu: new Ext.menu.Menu({
                items: [{
                    text: 'Remove',
                    name: 'remove_sort',
                    action: 'remove-sort',
                    record: record
                }]
            }),
            record: record
        };

        if (record.get('direction') === 'DESC') {
            sort.icon = '../js/pics/resources/themes/images/default/grid/sort_desc.gif';
        }

        buttons.push(sort);

        sort_container.add(buttons);

        return sort_container;
    },

    refreshSorts: function () {
        this.getSortButtons().removeAll();
        this.addReportStoreSorts();
    },

    removeSort: function (component) {
        var sortStore = this.getReportReportsStore().first().sorts(),
            toolbar = this.getSortButtons();

        sortStore.remove(component.record);

        this.refreshSorts();

        PICS.app.fireEvent('refreshreport');
    },

    setSortItemProperties: function (component) {
        if (component.record.get('direction') === 'ASC') {
            component.setIcon('../js/pics/resources/themes/images/default/grid/sort_desc.gif');
            component.record.set('direction', 'DESC')
        } else {
            component.setIcon('../js/pics/resources/themes/images/default/grid/sort_asc.gif');
            component.record.set('direction', 'ASC')
        }

        PICS.app.fireEvent('refreshreport');
    },

    sortItemExists: function (columnName) {
        var sortStore = this.getReportReportsStore().first().sorts(),
            duplicateSort = [];

        sortStore.each(function (record) {
            if (record.get('name') === columnName) {
                duplicateSort.push(record.get('name'));
            }
        });

        return (duplicateSort.length > 0) ? true : false;
    },

    updateSortItemDirection: function (columnName, selectedDirection) {
        var previousDirection = '',
            sortStore = this.getReportReportsStore().first().sorts(),
            toolbar = this.getSortButtons();

        previousDirection = sortStore.findRecord('name', columnName).get('direction');

        if (selectedDirection !== previousDirection) {
            var component = toolbar.child('toolbar button[text=' + columnName + ']');
            this.setSortItemProperties(component);
        }
    }
});