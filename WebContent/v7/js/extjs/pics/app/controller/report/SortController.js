Ext.define('PICS.controller.report.SortController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportSorts',
        selector: 'reportsorts'
    }, {
        ref: 'dataSetGrid',
        selector: 'reportdatasetgrid'
    }],

    stores: [
        'report.Reports',
        'report.AvailableFields'

    ],

    init: function () {
    	var that = this;

        this.control({
        	'reportsorts': {
        		render: function () {
        			if (this.getReportReportsStore().isLoading()) {
			        	this.getReportReportsStore().addListener({
				    		load: function (store, records, successful, eOpts) {
				    			that.application.fireEvent('refreshsorts');
				    		}
				    	});
			        } else {
			        	this.application.fireEvent('refreshsorts');
			        }
        		}
        	},
            'reportsorts button[action=sort-report]': {
                click: this.setSortItemProperties
            },
            'reportsorts menuitem[action=remove-sort]': {
                click: this.removeSort
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
            sorts = this.getReportSorts();

        sortStore.each(function (record) {
            button = me.createSortButton(record);
            sorts.add(button);
        });
    },

    //entry point from column controls
    addSortItem: function (columnName, selectedDirection) {
        var sortStore = this.getReportReportsStore().first().sorts(),
            sorts = this.getReportSorts();

        if (this.sortItemExists(columnName)) {
            this.updateSortItemDirection(columnName, selectedDirection);
        } else {
            var sortItem = Ext.create('PICS.model.report.Sort', {
                'name': columnName,
                'direction': selectedDirection
            });

            sortStore.add(sortItem);

            button = this.createSortButton(sortItem);

            sorts.add(button);

            this.application.fireEvent('refreshreport');
        }
    },

    createSortButton: function (record) {
        var buttons = [];
        var sort_container = Ext.create('Ext.toolbar.Toolbar');

        var availableFields = this.getReportAvailableFieldsStore();

        var sortName = record.get('name');

        var button_name = availableFields.findRecord('name', sortName).get('text')

        var sort = {
            xtype: 'splitbutton',
            action: 'sort-report',
            cls: 'sort default',
            height: 26,
            icon: 'v7/js/extjs/pics/resources/themes/images/default/grid/sort_asc.gif',
            iconAlign: 'left',
            menu: new Ext.menu.Menu({
                items: [{
                    text: 'Remove',
                    name: 'remove_sort',
                    action: 'remove-sort',
                    record: record
                }],
                plain: true
            }),
            record: record,
            text: button_name
        };

        if (record.get('direction') === 'DESC') {
            sort.icon = 'v7/js/extjs/pics/resources/themes/images/default/grid/sort_desc.gif';
        }

        buttons.push(sort);

        sort_container.add(buttons);

        return sort_container;
    },

    refreshSorts: function () {
        this.getReportSorts().removeAll();
        this.addReportStoreSorts();
    },

    removeSort: function (component) {
        var sortStore = this.getReportReportsStore().first().sorts();

        sortStore.remove(component.record);

        this.refreshSorts();

        this.application.fireEvent('refreshreport');
    },

    setSortItemProperties: function (component) {
        if (component.record.get('direction') === 'ASC') {
            component.setIcon('v7/js/extjs/pics/resources/themes/images/default/grid/sort_desc.gif');
            component.record.set('direction', 'DESC');
        } else {
            component.setIcon('v7/js/extjs/pics/resources/themes/images/default/grid/sort_asc.gif');
            component.record.set('direction', 'ASC');
        }

        this.application.fireEvent('refreshreport');
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
            sorts = this.getReportSorts();

        previousDirection = sortStore.findRecord('name', columnName).get('direction');

        if (selectedDirection !== previousDirection) {
            var component = sorts.child('toolbar button[text=' + columnName + ']');
            this.setSortItemProperties(component);
        }
    }
});