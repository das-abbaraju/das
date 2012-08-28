Ext.define('PICS.controller.dashboard.DashboardController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'viewport',
        selector: 'viewport'
    }],
    
    onLaunch: function() {
        for ( var column = 0; column < dashboard.length; column++) {
            var columnPanel = Ext.create('PICS.view.dashboard.Column', dashboard[column].config);
            this.getViewport().add(columnPanel);
            var panels = dashboard[column].panels;
            for ( var row = 0; row < panels.length; row++) {
                var config = panels[row];
                if (config.type == 'report') {
                    columnPanel.add(this.createPanelReport(config));
                } else if (config.type == 'chart') {
                    columnPanel.add(this.createPanelChart(config));
                } else if (config.type == 'html') {
                    columnPanel.add(this.createPanelHTML(config));
                }
            }
        }
    },
	
    createPanelHTML: function(config) {
        console.log(config.url);
        return Ext.create('PICS.view.dashboard.Panel', {
            title: config.name,
            insetPadding: 25,
            loader: {
                autoLoad: true,
                loadMask: true,
                renderer: 'html',
                scripts: true,
                url: config.url
            }
        });
    },
    
    createPanelChart: function(config) {
        return Ext.create('PICS.view.dashboard.Panel', {
            title: config.name,
            items: [ {
                xtype: 'chart',
                series: config.series,
                animate: true,
                shadow: true,
                legend: {
                    position: 'right'
                },
                insetPadding: 25,
                store: {
                    autoLoad: true,
                    fields: config.fields,
                    proxy: {
                        type: 'ajax',
                        reader: {
                            messageProperty: 'message',
                            root: 'data',
                            type: 'json'
                        },
                        url: 'ReportData.action?report=' + config.id
                    }
                }
            } ]
        });
    },
    
    createPanelReport: function(config) {
        return Ext.create('PICS.view.dashboard.Panel', {
            title: config.name,
            items: [{
                xtype: 'gridpanel',
                store: {
                    autoLoad: true,
                    fields: config.fields,
                    proxy: {
                        type: 'ajax',
                        reader: {
                            messageProperty: 'message',
                            root: 'data',
                            type: 'json'
                        },
                        url: 'ReportData.action?report=' + config.id
                    }
                },
                columns: config.columns
            }],
        });
    }
});
