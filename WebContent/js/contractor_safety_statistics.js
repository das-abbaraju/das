(function ($) {
    PICS.define('contractor.SafetyStatistics', {
        methods: {
            init: function () {
                $('.hurdle-rate').bind('click', this.showHurdleRates);
                $('#contractor_dashboard .weighted-industry-average').bind('click', this.showWeightedIndustryAverage);
            },
            
            showHurdleRates: function (event) {
                var element = $(this);
                var hurdle_row = $('tr.hurdle');
                
                if (hurdle_row.is(':visible')) {
                    element.text(element.attr('data-show-text'));
                    
                    $('tr.hurdle').hide();
                } else {
                    element.text(element.attr('data-hide-text'));
                    
                    $('tr.hurdle').show();
                }
            },
            
            showWeightedIndustryAverage: function (event) {
                var element = $(this);
                
                PICS.ajax({
                    url: element.attr('data-url'),
                    success: function(data, textStatus, XMLHttpRequest) {
                        var modal = PICS.modal({
                            modal_class: 'modal trir-weighted-industry-average-modal',
                            height: 450,
                            width: 650,
                            title: element.attr('title'),
                            content: data
                        });
                        
                        modal.show();
                    }
                });
            }
        }
    });
})(jQuery);