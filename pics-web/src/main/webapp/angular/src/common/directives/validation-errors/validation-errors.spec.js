describe('Validation errors directive', function () {
    var $compile, $rootScope, $httpBackend, template, input;

    beforeEach(angular.mock.module('PICSApp'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$httpBackend_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
    }));

    beforeEach(function () {
        var linkingFn,
            form = [
                '<form validation-errors="validationErrors" inline-validation="validateInline">',
                    '<div class="form-group">',
                        '<input ng-model="registrationForm.email" id="emailInput"></input>',
                    '</div>',
                    '<div class="form-group has-error error-test">',
                        '<input ng-model="registrationForm.phone" id="phoneInput"></input><span class="help-block">Initial Error 1</span>',
                    '</div>',
                    '<div class="form-group has-error error-test">',
                        '<input ng-model="registrationForm.username" id="usernameInput"></input><span class="help-block">Initial Error 2</span>',
                    '</div>',
                '</form>'
            ].join('');

        linkingFn = $compile(form);

        $rootScope.validateInline = angular.noop;

        spyOn($rootScope, 'validateInline');

        template = linkingFn($rootScope);
        emailInput = template.find('#emailInput');

        $rootScope.validationErrors = {
            fieldModel: 'registrationForm.phone',
            errors: {
                'registrationForm.phone': ['Replacement error 1'],
                'registrationForm.username': ['Replacement error 2']
            }
        };

        $rootScope.$digest();
    });

    it('should not call inlineValidation on blur if the input contains a value', function () {
        emailInput.val('tester@picsauditing.com');
        emailInput.blur();
        
        expect($rootScope.validateInline).not.toHaveBeenCalled();
    });

    it('should call inlineValidation on blur if the emailInput does not contain a value', function () {
        emailInput.val('');
        emailInput.blur();
        
        expect($rootScope.validateInline).toHaveBeenCalled();
    });

    it('should not call inlineValidation on change if the emailInput does not contain a value', function () {
        emailInput.val('');
        emailInput.change();
        
        expect($rootScope.validateInline).not.toHaveBeenCalled();
    });

    it('should call inlineValidation on change if the emailInput contains a value', function () {
        emailInput.val('tester@picsauditing.com');
        emailInput.change();
        
        expect($rootScope.validateInline).toHaveBeenCalled();
    });

    it('should clear only one field error if only one field has an error', function () {
        var formGroup = template.find('.form-group.error-test'),
            errors = [];
            
            formGroup.find('input').next().each(function () {
                errors.push($(this).text());
            });

        expect(errors).toEqual(['Replacement error 1', 'Replacement error 2']);
    });

    describe('Duplicate contractor error message', function () {
        beforeEach(function () {
            $rootScope.validationErrors = {
                fieldModel: 'PICS.DUPLICATE',
                errors: {
                    'PICS.DUPLICATE': ['']
                }
            };

            $rootScope.$digest();
        });

        it('should appear if error is PICS.DUPLICATE', function () {
            expect(template.find('.alert').length).toEqual(1);
        });

        it('should disappear if after revalidation, the error is gone', function () {
            $rootScope.validationErrors = {
                fieldModel: 'PICS.DUPLICATE',
                errors: {}
            };

            $rootScope.$digest();

            expect(template.find('.alert').length).toEqual(0);
        });

        it('should disappear on revalidation of the full form', function () {
            $rootScope.validationErrors = {};

            $rootScope.$digest();

            expect(template.find('.alert').length).toEqual(0);
        });
    });
});