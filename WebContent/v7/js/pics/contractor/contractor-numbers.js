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
                    modal = null,
                    that = this;

                function addModalEvents() {
                    if (modal) {
                        var modal_element = $(modal.getElement());

                        modal_element.on('change', '#contractor_numbers_client, #contractor_numbers_type', function () {
                            that.updateModal(contractor, data_number);
                        });

                        modal_element.on('click', '.positive', that.saveContractorNumber);
                    }
                }

                PICS.ajax({
                    url : 'ManageContractorOperatorNumber!edit.action',
                    data : {
                        contractor : contractor,
                        number : data_number
                    },
                    success : function(data, textStatus, jqXHR) {
                        modal = PICS.modal({
                            height : 300,
                            width : 700,
                            title : 'Contractor Numbers',
                            modal_class: 'modal',
                            modal_id: 'contractor-operator-number-modal',
                            content : data,
                            show: true
                        });

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
                        var modal = PICS.getClass('modal.Modal');

                        if (data.indexOf('error') > 0) {
                            modal.update({body: data});
                        } else {
                            //update contractor dashboard numbers table
                            $('#contractor_operator_numbers').html(data);

                            modal.destroy();
                        }
                    }
                });
            },

            updateModal: function (contractor, data_number) {
                //get updated values
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
                        var modal = PICS.getClass('modal.Modal');

                        modal.update({body: data});
                    }
                });
            }
        }
    });
})(jQuery);