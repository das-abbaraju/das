(function ($) {
    PICS.define('contractor.ContractorNumbers', {
        methods: {
            init: function () {
                if ($('#contractor_operator_numbers').length > 0) {
                    var that = this;

                    $('#contractor_operator_numbers').on('showcontractornumbers', function (event, event_trigger) {
                        that.createModal.apply(that, [event_trigger]);
                    });
                }
            },

            createModal: function (event_trigger) {
                var element = $(event_trigger.currentTarget),
                    contractor = element.attr('data-contractor'),
                    data_number = element.attr('data-number'),
                    that = this;

                function addModalEvents () {
                    var contractor_modal =  $('.contractor-operator-number-modal');

                    if (contractor_modal) {
                        contractor_modal.delegate('.negative.closeButton', 'click', function() {
                            PICS.getClass('modal.Modal').hide();
                        });

                        contractor_modal.delegate('#contractor_numbers_client, #contractor_numbers_type', 'change', function () {
                            that.updateModal(contractor, data_number);
                        });

                        contractor_modal.delegate('.positive', 'click', that.saveContractorNumber);
                    }
                }

                PICS.ajax({
                    url : 'ManageContractorOperatorNumber!edit.action',
                    data : {
                        contractor : contractor,
                        number : data_number
                    },
                    success : function(data, textStatus, jqXHR) {
                        var modal = PICS.modal({
                            height : 300,
                            width : 700,
                            title : 'Contractor Numbers',
                            modal_class: 'modal contractor-operator-number-modal',
                            content : data,
                        });

                        modal.show();

                        addModalEvents();
                    }
                });
            },

            saveContractorNumber : function(event) {
                var element = $(this),
                    form = $('#contractor_operator_numbers_form');

                PICS.ajax({
                    url : 'ManageContractorOperatorNumber!save.action',
                    data : form.serialize(),
                    success : function(data, textStatus, jqXHR) {
                        if (data.indexOf('error') > 0) {
                            var modal = $('.contractor-operator-number-modal'),
                            content = modal.find('.modal-body');

                            content.html(data);
                        } else {
                            //update contractor dashboard numbers table
                            $('#contractor_operator_numbers').html(data);

                            var modal = PICS.getClass('modal.Modal');
                            modal.hide();
                        }
                    }
                });
            },

            updateModal: function (contractor, data_number) {

                //get updated select values
                var client = $('#contractor_numbers_client'),
                    type = $('#contractor_numbers_type');

                PICS.ajax({
                    url: 'ManageContractorOperatorNumber!edit.action',
                    data: {
                        clientSite: client.val(),
                        clientType: type.val(),
                        contractor: contractor,
                        number: data_number
                    },
                    success: function (data, textStatus, jqXHR) {
                        var modal = $('.contractor-operator-number-modal'),
                        content = modal.find('.modal-body');

                        content.html(data);
                    }
                });
            }
        }
    });
})(jQuery);