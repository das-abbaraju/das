Ext.define('PICS.view.report.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheader'],

    border: false,
    height: 90,
    id: 'report_header',
    items: [{
        region: 'center',
        xtype: 'panel',
        border: false,
        height: 90,
        html: new Ext.Template([
            '<h1 class="name"></h1>',
            '<h2 class="description"></h2>'
        ]),
        id: 'report_summary',

        // TODO: not sure if this should be refactored
        afterRender: function () {
            var store = Ext.StoreManager.lookup('report.Reports');
            var cmp = this;

            function updateSummary(title, description) {
                cmp.getEl().query('.name')[0].innerHTML = title;
                cmp.getEl().query('.description')[0].innerHTML = description;
            }

            if (store.isLoading()) {
                store.addListener('load', function (store) {
                    var report = store.first();

                    updateSummary(report.get('name'), report.get('description'));
                });
            } else {
                var report = store.first();

                updateSummary(report.get('name'), report.get('description'));
            }
        }
    }],
    layout: 'border',

    constructor: function () {
        this.callParent(arguments);

        this.addActionMenu();
    },

    addActionMenu: function () {
        //create report buttons
        var configuration = PICS.app.configuration;
        var panel = Ext.create('Ext.panel.Panel', {
            region: 'east',

            border: 0,
            id: 'report_actions',
            width: 190
        });

        //if (configuration.isEditable()) {
            var edit = Ext.create('Ext.button.Button', {
                action: 'edit',
                cls: 'edit',
                height: 40,
                text: '<i class="icon-cog icon-large"></i>',
                width: 40
            });

            var save = Ext.create('Ext.button.Split', {
                action: 'save',
                cls: 'save',
                height: 40,
                menu: new Ext.menu.Menu({
                    items: [{
                        text: 'Copy',
                        name: 'copy',
                        action: 'copy'
                    }, {
                        text: 'Share',
                        action: 'share'
                    }, {
                        text: 'Print',
                        action: 'print'
                    }, {
                        text: 'Export',
                        action: 'export'
                    }],
                    plain: true
                }),
                text: 'Save',
                width: 100
            });

            panel.add(edit, save);
        //}

        this.add(panel);
    }
});