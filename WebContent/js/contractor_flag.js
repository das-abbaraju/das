(function($) {
    PICS.define('contractor.Flag', {
        methods : {
            init : function() {
                var element = $('.ContractorFlag-page');
                if (element.length) {
                    element.find('.datepicker').datepicker({
                        changeMonth: true,
                        changeYear:true,
                        minDate: new Date(),
                        yearRange: (new Date().getFullYear()) +':'+ (new Date().getFullYear()+5),
                        showOn: 'both',
                        buttonImage: 'images/icon_calendar.gif',
                        buttonImageOnly: true,
                        buttonText: translate('JS.ContractorFlag.ChooseDate'),
                        constrainInput: true,
                        showAnim: 'fadeIn'
                    });
                    
                    element.find('.cluetip').cluetip({
                        arrows: true,
                        cluetipClass: 'jtip',
                        local: true,
                        clickThrough: false,
                        sticky: true,
                        closeText : "<img src='images/cross.png' width='16' height='16'>"
                    });
                    
                    element.find("#tabs").tabs();
                    
                    element.delegate('tr._override_.clickable', 'click', this.removeClickableHoverable);
                    element.delegate('.override_hide', 'click', this.fadeAndMakeParentsClickable);
                    element.delegate('.toggle-override', 'click', this.toggleOverrideVisibility);
                    element.delegate('#individual_flag_override_form', 'submit', this.checkReason);
                }
            },
            
            removeClickableHoverable: function(event) {
                $(this).removeClass('clickable').removeClass('tr-hover-clickable').find('div.override_form').fadeIn('fast');
            },
            
            fadeAndMakeParentsClickable: function(event) {
                var me = $(this);
                me.parent().fadeOut('fast',function() {
                    me.parents('tr._override_').addClass('clickable');
                });
            },
            
            toggleOverrideVisibility: function(event) {
                $('.ContractorFlag-page #override_link').slideToggle();
                $('.ContractorFlag-page #override').slideToggle();
            },
            
            checkReason: function(event) {
                var reason = $(this).find('textarea[name="forceNote"]').val();
                
                if (reason && $.trim(reason) != '') {
                    return true;
                } else {
                    // This is bad
                    alert(translate("JS.ContractorFlag.FillInReason"));
                    return false;
                }
            }
        }
    });
})(jQuery);