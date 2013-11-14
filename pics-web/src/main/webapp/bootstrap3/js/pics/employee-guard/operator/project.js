PICS.define('employee-guard.Operator.Project', {
    methods: (function () {

        var initialCompanies = [];

        function init() {
            var $body = $('body');

            //TODO Find out why this fires twice when "on" is used
            $body.one('intialized.select2', '.companies-requested.select2', saveInitialCompanies);

            $body.on('click', '.save-requested-comapanies', submitRequestedCompaniesForm);

            $body.on('click', '#removeRequestedCompaniesModal .cancel', onModalCancelClick);

            $body.on('click', '#removeRequestedCompaniesModal .remove', onModalRemoveClick);

        }

        //TODO: Replace click trigger for a better solution
        function onModalCancelClick(event) {
            var $select2 = $('.companies-requested.select2'),
                $form = $select2.closest('form'),
                $cancel = $form.find('.cancel');

            $('#removeRequestedCompaniesModal').modal('hide');
            $cancel.click();
        }

        //TODO: Replace click trigger for a better solution
        function onModalRemoveClick() {
            var $select2 = $('.companies-requested.select2'),
                $form = $select2.closest('form'),
                $save = $form.find('.btn-success');

                initialCompanies = [];

            $save.click();
        }

        function saveInitialCompanies(event) {
            var $element = $(event.target),
                companies = $element.select2('val');

            initialCompanies = companies;
        }

        function submitRequestedCompaniesForm(event) {
            if (checkForDeletedCompanies()) {
                event.preventDefault();
                displayRemoveCompanyModal();
            }
        }

        function checkForDeletedCompanies() {
            var $element = $('.companies-requested.select2'),
                current_companies = $element.select2('val'),
                deletedCompany = false;

            for (var x = 0; x < initialCompanies.length; x++) {
                if ($.inArray(initialCompanies[x], current_companies) == -1) {
                    deletedCompany = true;
                }
            }

            return deletedCompany;
        }

        function displayRemoveCompanyModal() {
            $('#removeRequestedCompaniesModal').modal('show');
        }

        return {
            init: init
        };
    }())
});