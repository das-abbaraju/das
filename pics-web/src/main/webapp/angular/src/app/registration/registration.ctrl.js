angular.module('PICS.registration')

.controller('registrationCtrl', function ($sce, $scope, $location, $routeParams, $window, accountService, countryService, dialectService, languageService, registrationRequestService, timeZoneService, userService, vatService, translationsService) {
    var registrationKey = $routeParams.registrationKey;

    $scope.isDevEnvironment = function () {
        var origin = window.location.origin;

        return origin.indexOf('localhost') != -1 || origin.indexOf('alpha') != -1;
    };

    $scope.$watch('localeForm.language', updateDialectList);
    $scope.$watch('registrationForm.countryISOCode', function (newCountryISOCode) {
        updateTimeZoneList(newCountryISOCode);
        updateVatRequirement(newCountryISOCode, $scope.localeForm.language, $scope.localeForm.dialect);
        updateLanguage();
    });
    $scope.$watch('localeForm.dialect', function (newDialect) {
        updateVatRequirement($scope.registrationForm.countryISOCode, $scope.localeForm.language, newDialect);
        updateLanguage();
    });

    if (registrationKey) {
        registrationRequestService.get({
                registrationKey: registrationKey
            }, function (data) {
                $scope.localeForm = data.localeForm;
                $scope.registrationForm = data.registrationForm;
        });
    }

    languageService.query(function (data) {
        $scope.languages = data;
    });

    countryService.query(function (data) {
        $scope.countries = data;
    });

    function updateLanguage() {
        translationsService.updateTranslations(getRequestLocale());
    }

    function getRequestLocale() {
        var language = $scope.localeForm.language,
            dialect = $scope.localeForm.dialect;

        return dialect ? language + '_' + dialect : language;
    }

    function updateDialectList(language) {
        dialectService.query({id:language}, function (data) {
            if (data.length) {
                $scope.dialects = data;

                if (language == 'en') {
                    $scope.localeForm.dialect = data[getIndexForEnglish(data)].id;
                } else {
                    $scope.localeForm.dialect = data[0].id;
                }
            } else {
                $scope.dialects = null;
            }
        });
    }

    function getIndexForEnglish(data) {
        var englishIndex;

        angular.forEach(data, function (dialect, index) {
            if (dialect.id == 'US') {
                englishIndex = index;
            }
        });

        return englishIndex;
    }

    function updateTimeZoneList(country) {
        timeZoneService.get({country:country}, function (data) {
            $scope.timeZones = data.result;
            $scope.registrationForm.timezoneId = data.result[0].id;
        });
    }

    function updateVatRequirement(country, language, dialect) {
        var languageParam = language;

        if (dialect) {
            languageParam = language + '-' + dialect;
        }

        vatService.get({country:country, language:languageParam}, function (data) {
            $scope.vatRequired = data.tax_id_required;
            $scope.vatIdLabel = data.label;
        });
    }

    $scope.autofillForDev = function () {
        var email = 'my.email' + new Date().getTime() + '@test.com';

        angular.extend($scope.registrationForm, {
            legalName: 'My Company' +  new Date().getTime(),
            addressBlob: '123 Anywhere St, Irvine, CA',
            zip: '12345',
            firstName: 'John',
            lastName: 'Doe',
            email: email,
            phone: '555-555-5555',
            username: email,
            password: 'password1',
            passwordConfirmation: 'password1'
        });
    };

    $scope.setUsernameDefault = function () {
        if (!$scope.registrationForm.username) {
            $scope.registrationForm.username = $scope.registrationForm.email;
        }
    };

    $scope.validateInline = function (formKey, fieldModel, fieldKey, fieldValue) {
        accountService.validateCreateAccountParams(getCreateAccountParams(formKey, fieldKey, fieldValue))
        .then(function (response) {
            $scope.validationErrors = {
                fieldModel: fieldModel,
                errors: response
            };
        });
    };

    function getCreateAccountParams(formKey, fieldKey, fieldValue) {
        var createAccountParams = {
            localeForm: {},
            registrationForm: {}
        };
 
        if (formKey && createAccountParams[formKey] && fieldKey) {
            if (fieldKey == 'passwordConfirmation') {
                createAccountParams.registrationForm.password = $scope.registrationForm.password; 
            } else if (fieldKey == 'zip' || fieldKey == 'vatId') {
                createAccountParams.registrationForm.countryISOCode = $scope.registrationForm.countryISOCode;
            }

            fieldValue = fieldValue || '';
            createAccountParams[formKey][fieldKey] = fieldValue;
        } else {
            createAccountParams.localeForm = $scope.localeForm;
            createAccountParams.registrationForm = $scope.registrationForm;
        }

        return createAccountParams;
    }
    
    $scope.onSubmitClick = function () {
        accountService.createAccount(getCreateAccountParams())
        .then(function () {
            $window.location = '/RegistrationAddClientSite.action';
        }, function (response) {
            if (response.status == 406) {
                var addressBlobErrors = response.data['registrationForm.addressBlob'],
                    errorCount = 0;

                angular.forEach(response.data, function () {
                    errorCount += 1;
                });

                if (addressBlobErrors && errorCount == 1) {
                    handleAddressBlobErrors(addressBlobErrors);
                } else {
                    $scope.validationErrors = $scope.validationErrors || {};
                    $scope.validationErrors.errors = response.data;

                    window.scroll(0,0);
                }
            }
        });
    };

    function handleAddressBlobErrors(addressBlobErrors) {
        if (addressBlobErrors[0] == 'Requires Manual Confirmation') {
            $location.path('/registration/address-confirmation.action');
        } else {
            $location.path('/registration/address-editor.action');
        }
    }
});