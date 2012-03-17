Ext.define('PICS.controller.dashboard.DashboardController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'viewport',
        selector: 'viewport'
    }],
    
    onLaunch: function() {
        for ( var column = 0; column < dashboard.length; column++) {
            console.log(column);
            var columnPanel = Ext.create('PICS.view.dashboard.Column', dashboard[column].config);
            this.getViewport().add(columnPanel);
            var panels = dashboard[column].panels;
            for ( var row = 0; row < panels.length; row++) {
                var panel = this.createPanelReport(panels[row]);
                columnPanel.add(panel);
            }
        }
    },
	
    createPanelReport: function(report) {
        return Ext.create('PICS.view.dashboard.Panel', {
            title: report.name,
            items: [{
                xtype: 'gridpanel',
                store: {
                    autoLoad: true,
                    fields: report.fields,
                    proxy: {
                        type: 'ajax',
                        reader: {
                            messageProperty: 'message',
                            root: 'data',
                            type: 'json'
                        },
                        url: 'ReportDynamic!data.action?report=' + report.id
                    }
                },
                columns: report.columns
            }],
        });
    }
});
