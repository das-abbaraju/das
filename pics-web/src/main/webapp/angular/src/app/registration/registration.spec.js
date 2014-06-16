describe('Registration Page Controller', function() {
    var $scope, $controller, $httpBackend, $location, $routeParams,
        spanishDialects = [{
                "id":"AR",
                "name":"Argentina"
            },{
                "id":"BO",
                "name":"Bolivia"
            },{
                "id":"CL",
                "name":"Chile"
        }];

    beforeEach(angular.mock.module('PICSApp'));

    beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_, _$location_, _$routeParams_) {
        $rootScope = _$rootScope_;
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $location = _$location_;
        $routeParams = _$routeParams_;
    }));

    beforeEach(function () {
        $scope = $rootScope.$new(); 

        $httpBackend.when('GET', '/languages.action').respond([]);
        $httpBackend.when('GET', '/dialects/es.action').respond(spanishDialects);
        $httpBackend.when('GET', '/dialects/en.action').respond([{
                "id":"US",
                "name":"United States"
            },{
                "id":"CA",
                "name":"Canada"
            },{
                "id":"GB",
                "name":"United Kingdom"
        }]);
        $httpBackend.when('GET', '/dialects/da.action').respond([]);
        $httpBackend.when('GET', '/dialects.action').respond([]);
        $httpBackend.when('GET', '/countries.action').respond([]);
        $httpBackend.when('GET', /time-zones/).respond({result: [{}]});
        $httpBackend.when('GET', /tax-id-info/).respond({});
    });

    it('should define a localeForm and registrationForm if a registration key is passed', function () {
        $httpBackend.when('GET', '/registration-requests/12345.action').respond({
            localeForm: 'defaultDataForLocaleForm',
            registrationForm: 'defaultDataForRegistrationForm'
        }); 

        $routeParams.registrationKey = 12345;

        $controller("registrationCtrl", {
            $scope: $scope
        });

        $httpBackend.flush();

        expect($scope.localeForm).toEqual('defaultDataForLocaleForm');
        expect($scope.registrationForm).toEqual('defaultDataForRegistrationForm');
    });

    beforeEach(function () {
        $scope = $rootScope.$new();

        $scope.localeForm = {
            dialect: "US",
            language: "en"
        };

        $scope.registrationForm = {
            addressBlob: "",
            countryISOCode: "US",
            email: "",
            firstName: "",
            lastName: "",
            legalName: "",
            password: "",
            passwordConfirmation: "",
            phone: "",
            timezoneId: "America/Adak",
            username: "",
            vatId: "",
            zip: ""
        };  

        $controller("registrationCtrl", {
            $scope: $scope
        });
    });

    describe('validateInline', function () {
        var validationRequestData;

        beforeEach(function () {
            $httpBackend.when('POST', '/accounts.action').respond({});

            $httpBackend.when('POST', '/registration/validation.action').respond(function (method, url, json) {
                var response;

                validationRequestData = JSON.parse(json);

                if (validationRequestData.registrationForm.legalName) {
                    response = {
                        "registrationForm.legalName":[
                            "Company Name Already Exists",
                            "Required"
                        ]
                    };
                } else {
                    response = {
                        test: 'test'
                    };
                }
                return [
                    200,
                    JSON.stringify(response)
                ];
            });
        });

        it('should create a validation object for a particular field', function () {
            var fieldModel = 'registrationForm.legalName',
                keys = fieldModel.split('.'),
                formKey = keys[0],
                fieldKey = keys[1],
                fieldValue = "Ancon Marine";

            $scope.validateInline(formKey, fieldModel, fieldKey, fieldValue);
            $httpBackend.flush();

            expect($scope.validationErrors.fieldModel).toEqual('registrationForm.legalName');
            expect($scope.validationErrors.errors['registrationForm.legalName']).toEqual([
                'Company Name Already Exists',
                'Required'
            ]);
        });

        it('should add password value to createAccountParams if passwordConfirmation field has been changed', function () {
            $scope.registrationForm.password = 'password';
            $scope.registrationForm.passwordConfirmation = 'passwordConfirmation';

            $scope.validateInline('registrationForm', 'registrationForm.passwordConfirmation', 'passwordConfirmation', $scope.registrationForm.passwordConfirmation);
            $httpBackend.flush();

            expect(validationRequestData).toEqual({
                localeForm: {},
                registrationForm: {
                    password: 'password',
                    passwordConfirmation: 'passwordConfirmation'
                }
            });
        });

        it('should add countryISOCode to createAccountParams if zip field has been changed', function () {
            $scope.registrationForm.countryISOCode = 'us';
            $scope.registrationForm.zip = 'zip';

            $scope.validateInline('registrationForm', 'registrationForm.zip', 'zip', $scope.registrationForm.zip);
            $httpBackend.flush();

            expect(validationRequestData).toEqual({
                localeForm: {},
                registrationForm: {
                    countryISOCode: 'us',
                    zip: 'zip'
                }
            });
        });

        it('should add countryISOCode to createAccountParams if vatId field has been changed', function () {
            $scope.registrationForm.countryISOCode = 'us';
            $scope.registrationForm.vatId = 'vatId';

            $scope.validateInline('registrationForm', 'registrationForm.vatId', 'vatId', $scope.registrationForm.vatId);
            $httpBackend.flush();

            expect(validationRequestData).toEqual({
                localeForm: {},
                registrationForm: {
                    countryISOCode: 'us',
                    vatId: 'vatId'
                }
            });
        });
    });

    describe('onSubmitClick', function () {
        it('should redirect to address confirmation page if address requires manual confirmation', function () {
            $httpBackend.when('POST', '/accounts.action').respond(function () {
                return [
                    406,
                    JSON.stringify({
                        "registrationForm.addressBlob":[
                            "Requires Manual Confirmation"
                        ]
                    })
                ];
            });

            $scope.onSubmitClick();
            $httpBackend.flush();

            expect($location.path()).toEqual('/registration/address-confirmation.action');
        });
    });

    describe('onSubmitClick', function () {
        var errors = {
            "registrationForm.addressBlob":[
                "Required"
            ],
            "registrationForm.legalName":[
                "Required"
            ]
        };

        it('should bind returned errors to the scope', function () {
            $httpBackend.when('POST', '/accounts.action').respond(function () {
                return [
                    406,
                    JSON.stringify(errors)
                ];
            });

            $scope.onSubmitClick();
            $httpBackend.flush();

            expect($scope.validationErrors.errors).toEqual(errors);
        });
    });

    describe('updateDialectList', function () {

        beforeEach(function () {
            $scope.localeForm = {
                dialect: "US",
                language: "en"
            };
        });

        describe('Language is not English', function () {
            beforeEach(function () {
                $scope.localeForm.language = 'es';

                $httpBackend.flush();
            });

            // TODO: Figure out why comparison with spanishDialects is not working
            it ('should populate the dialect list', function () {
                expect($scope.dialects[0].id).toEqual('AR');
            });

            it('should set default to index 0 if not English', function () {
                expect($scope.localeForm.dialect).toEqual(spanishDialects[0].id);
            });
        });

        it('should select US if language is English', function () {
            $scope.localeForm.language = 'en';

            $httpBackend.flush();

            expect($scope.localeForm.dialect).toEqual('US');
        });

        it('should not populate the dialect list if none are available', function () {
            $scope.localeForm.language = 'da';

            $httpBackend.flush();

            expect($scope.dialects).toEqual(null);
        });
    });

    describe('setUsernameDefault', function () {
        it ('should copy email into username if username is blank', function () {
            $scope.registrationForm.username = '';
            $scope.registrationForm.email = 'tester@picsauditing.com';

            $scope.setUsernameDefault();

            expect($scope.registrationForm.username).toEqual($scope.registrationForm.email);
        });

        it ('should not copy email into username if username has been specified', function () {
            $scope.registrationForm.username = 'johnsmith';
            $scope.registrationForm.email = 'tester@picsauditing.com';

            $scope.setUsernameDefault();

            expect($scope.registrationForm.username).toEqual($scope.registrationForm.username);
        });
    });
});