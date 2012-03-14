(function ($) {
    PICS.define('operator.Edit', {
        methods: {
            init: function () {
                $('#FacilitiesEdit-page').bind('#opCountry', this.countryChanged);
                $('#FacilitiesEdit-page').delegate('#opCountry', 'change', {operatorEdit: this}, this.countryChanged);
                $('#FacilitiesEdit-page').delegate('#general_contractor_checkbox', 'click', this.toggleLinkedAccountField);
            },
            
            changeState: function(event) {
                var countryString = $('#FacilitiesEdit-page #opCountry').val();
                var stateString = $('#FacilitiesEdit-page #operatorState').val();
                
                PICS.ajax({
                    url: "StateListAjax.action",
                    data: {
                        countryString: countryString,
                        stateString: stateString
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#FacilitiesEdit-page #state_li').html(data);
                    }
                });
            },
            
            countryChanged: function(event) {
                var operatorEdit = event.data.operatorEdit;
                var country = $(this).val();
                
                if (country == 'AE') {
                    $('#zip_li').hide();
                } else {
                    $('#zip_li').show();
                }
                
                operatorEdit.changeState();
            },
            
            toggleLinkedAccountField: function(event) {
                $('#FacilitiesEdit-page #linked_contractor').toggle();
            }
        }
    });
})(jQuery);