Ext.define('PICS.view.report.report.ReportColumnTooltip', {
    extend: 'Ext.tip.ToolTip',
    alias: 'widget.reportcolumntooltip',
    
    anchor: 'bottom',
    showDelay: 0,
    tpl: '<div><h3>{text}</h3><p>{help}</p></div>'
});