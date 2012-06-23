Ext.scopeResetCSS = true;

Ext.Loader.setConfig({
    enabled: true,
    paths: {
        PICS: './js/pics/app'
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
            xy[1] += 5;

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

window.onload = function () {
    Ext.onReady(function ()  {
        var menu = Ext.create('PICS.view.layout.Menu', {
        	renderTo: 'site_navigation'
        });

        Ext.EventManager.onWindowResize(function () {
            menu.doLayout();
        });
    });
};