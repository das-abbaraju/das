Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'Ext.ux': './v7/js/extjs/pics/app/ux'
    }
});

Ext.override(Ext.menu.Menu, {
    showBy: function(cmp, pos, off) {
        var me = this,
            xy,
            region;

        if (me.floating && cmp) {
            me.layout.autoSize = true;

            // show off-screen first so that we can calc position without causing a visual jump
            me.doAutoRender();
            delete me.needsLayout;

            // Component or Element
            cmp = cmp.el || cmp;

            // Convert absolute to floatParent-relative coordinates if necessary.
            xy = me.el.getAlignToXY(cmp, pos || me.defaultAlign, off);
            if (me.floatParent) {
                region = me.floatParent.getTargetEl().getViewRegion();
                xy[0] -= region.x;
                xy[1] -= region.y;
            }

            // custom menu positioning
            xy[1] += 3;

            me.showAt(xy);
        }
        return me;
    },

    doConstrain : function() {
        var me = this,
            y = me.el.getY(),
            max, full,
            vector,
            returnY = y, normalY, parentEl, scrollTop, viewHeight;

        delete me.height;
        me.setSize();
        full = me.getHeight();
        if (me.floating) {
            //if our reset css is scoped, there will be a x-reset wrapper on this menu which we need to skip
            parentEl = Ext.fly(me.el.getScopeParent());
            scrollTop = parentEl.getScroll().top;
            viewHeight = parentEl.getViewSize().height;
            //Normalize y by the scroll position for the parent element.  Need to move it into the coordinate space
            //of the view.
            normalY = y - scrollTop;
            max = me.maxHeight ? me.maxHeight : viewHeight - normalY;
            if (full > viewHeight) {
                max = viewHeight;
                //Set returnY equal to (0,0) in view space by reducing y by the value of normalY
                returnY = y - normalY;
            } else if (max < full) {
                returnY = y - (full - max);
                max = full;
            }
        }else{
            max = me.getHeight();
        }
        // Always respect maxHeight
        if (me.maxHeight){
            max = Math.min(me.maxHeight, max);
        }
        if (full > max && max > 0){
            me.layout.autoSize = false;
            me.setHeight(max);
            if (me.showSeparator){
                me.iconSepEl.setHeight(me.layout.getRenderTarget().dom.scrollHeight);
            }
        }
        vector = me.getConstrainVector(me.el.getScopeParent());
        if (vector) {
            me.setPosition(me.getPosition()[0] + vector[0]);
        }
        me.el.setY(returnY);
    }
});

Ext.application({
    name: 'PICS',
    appFolder: 'v7/js/extjs/pics/app',

    constants: {
        NUMBERSTORE: [
            ['Equals', '='],
            ['GreaterThan', '>'],
            ['LessThan', '<'],
            ['GreaterThanOrEquals', '>='],
            ['LessThanOrEquals', '<='],
            ['Empty', 'blank']
        ],
        TEXTSTORE: [
            ['Contains', 'contains'],
            ['NotContains', 'does not contain'],
            ['BeginsWith', 'begins with'],
            ['NotBeginsWith', 'does not begin with'],
            ['EndsWith', 'ends with'],
            ['NotEndsWith', 'does not end with'],
            ['Equals', 'equals'],
            ['NotEquals', 'does not equal'],
            ['Empty', 'blank']
        ]
    },

    configuration: null,

    controllers: [
        'report.AvailableFieldModal',
        'report.ReportController',
        'report.Filter',
        'report.ReportData',
        'report.ReportHeaderController'
    ],

    models: [
        'report.AvailableField',
        'report.Report'
    ],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.DataSets',
        'report.Reports'
    ],

    launch: function () {
    	var that = this;

    	// save reference to application
        PICS.app = this;

        this.getConfiguration({
        	success: function () {
        		that.createViewport.apply(that);
        	}
        });
    },

    getConfiguration: function (options) {
        var url = Ext.Object.fromQueryString(document.location.search);

        Ext.Ajax.request({
           url: 'ReportDynamic!configuration.action?report=' + url.report,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               // configuration closure
               PICS.app.configuration = (function config() {
                   return {
                       isEditable: function () {
                           return result.is_editable;
                       }
                   };
               }());

               // success callback
               if (options && options.success && typeof options.success == 'function') {
            	   options.success();
               }
           }
        });
    },

    createViewport: function () {
    	Ext.create('PICS.view.report.Viewport', {
    		listeners: {
    			render: function (component, eOpts) {
    				// remove loading background
    				Ext.get('loadingPage').remove();
     		   }
     	   }
        });
    }
});