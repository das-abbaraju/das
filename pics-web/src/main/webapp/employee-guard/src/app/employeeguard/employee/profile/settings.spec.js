describe("Profile Settings Controller", function() {
    var scope, ProfileService, $translate;

    var profile_settings = {
        "language": {
          "id":"de",
          "name":"Deutsch"
       },
        "dialect": {
          "id":"CH",
          "name":"Schweiz"
       }
    };

    var languages = [
        {
            "id":"en",
            "name":"English"
        },{
            "id":"de",
            "name":"Deutsch"
        }
    ];

    var dialect = [
        {
          "id":"AT",
          "name":"Österreich"
        },
        {
          "id":"DE",
          "name":"Deutschland"
        }
    ];

    var urls = {
        settings: {
            live: '/employee-guard/api/settings',
            dev: '/employee-guard/json/employee/settings/settings.json'
        },
        language: {
            live: '/employee-guard/api/languages',
            dev: '/employee-guard/json/employee/settings/languages.json'
        },
        dialect: {
            live: '/employee-guard/api/dialects/de',
            dev: '/employee-guard/json/employee/settings/dialects_de.json'
        }
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, _$httpBackend_, _$translate_, _ProfileService_) {
        ProfileService = _ProfileService_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;

        scope = $rootScope.$new();

        $controller("profileSettingsCtrl", {
            $scope: scope
        });

        $httpBackend.when('GET', urls.settings.live).respond(profile_settings);
        $httpBackend.when('GET', urls.language.live).respond(languages);
        $httpBackend.when('GET', urls.dialect.live).respond(dialect);
    }));

    describe("Profile", function() {
        it("should get the current user profile settings", function() {
            scope.getProfileSettings();

            $httpBackend.flush();

            expect(scope.profile_settings.language).toEqual(profile_settings.language);
            expect(scope.profile_settings.dialect).toEqual(profile_settings.dialect);

        });

        it("should toggle the profile edit form", function() {
            scope.showEditForm = false;

            scope.toggleFormDisplay();

            expect(scope.showEditForm).toBeTruthy();

            scope.toggleFormDisplay();

            expect(scope.showEditForm).toBeFalsy();
        });

        describe("Save", function() {
            beforeEach(function () {
                //populate dialect list
                scope.getDialectList('de');
                $httpBackend.flush();
            });

            it("should format the request payload", function() {
                var formatted_settings,
                    user_settings = {
                        language: 'de',
                        dialect: 'AT'
                    },
                    expected = {
                        language: {
                            id: 'de',
                            name: 'Deutsch'
                        },
                        dialect: {
                            id: 'AT',
                            name: 'Österreich'
                        }
                    };

                formatted_settings = scope.formatRequestPayload(user_settings);

                expect(formatted_settings).toEqual(expected);
            });

            it("should set language and dialect to undefined if no match", function() {
                var user_settings = {
                    language: 'fr',
                    dialect: 'EN'
                };

                formatted_settings = scope.formatRequestPayload(user_settings);

                expect(formatted_settings.language).not.toBeDefined();
                expect(formatted_settings.dialect).not.toBeDefined();
            });

            it("should apply selected language settings immediately to a base language", function() {
                var user_settings = {
                    "language": "de"
                };

                scope.saveProfileSettings(user_settings);

                expect($translate.use()).toEqual('de');
            });

            it("should apply selected language settings immediately to a language and dialect", function() {
                var user_settings = {
                    "language": "de",
                    "dialect": "AT"
                };

                scope.saveProfileSettings(user_settings);

                expect($translate.use()).toEqual('de_AT');
            });
        });
    });

    describe("Language", function() {
        it("should load the list of available languages", function() {
            scope.getLanguageList();

            $httpBackend.flush();

            expect(scope.languageList).toBeDefined();
            expect(scope.languageList[0].name).toEqual('English');
        });

        it("should pre-select the language based on the save profile setting", function() {
            scope.setSelectedLanguage('de');

            expect(scope.user.language).toEqual('de');
        });
    });

    describe("Dialect List", function() {
        it("should load the list a dialects based on a language", function() {
            scope.getDialectList('de');

            $httpBackend.flush();

            expect(scope.dialectList).toBeDefined();
            expect(scope.dialectList[1].name).toEqual('Deutschland');
        });

        it("should pre-select the dialect based on the save profile setting", function() {
            scope.setSelectedDialect('CH');

            expect(scope.user.dialect).toEqual('CH');
        });
    });
});
