angular.module('PICS.employeeguard')

.controller('settingsCtrl', function ($scope, $cookies, $filter, $cookieStore, Language, Dialect, ProfileSettingsService) {

    // parseCookie($cookies.locale);
    // function parseCookie() {
    //     var locale = $cookies.locale;

    //     var separator = locale.indexOf('_');

    //     var language = locale.substring(0, separator);
    //     var dialect = locale.substring(separator + 1);

    //     // console.log(language);
    //     // console.log(dialect);

    //     Language.query(function(languages) {
    //         $scope.languageList = languages;
    //     });

    //     Dialect.query({id: language}, function(dialects) {
    //         $scope.dialectList = dialects;
    //     });
    // }

    Language.query(function(languages) {
        $scope.languageList = languages;

        ProfileSettingsService.getSettings(setFormValues);
    });

    function formatLanguageJSON(selected_language, list) {
        var language = $filter('filter')($scope.languageList, { id: selected_language})[0];

        if (language) {
            return {
                id: language.id,
                name: language.name
            };
        }
    }

    function formatDialectJSON(selected_dialect, list) {
        var dialect = $filter('filter')($scope.dialectList, { id: selected_dialect})[0];

        if (dialect) {
            return {
                id: dialect.id,
                name: dialect.name
            };
        }
    }

    $scope.updateDialectList = function(language) {
        Dialect.query({id: language},function(dialects) {
            $scope.dialectList = dialects;
        });
    };

    $scope.toggleFormDisplay = function() {
        $scope.showEditForm = !$scope.showEditForm;
    };

    $scope.updateSettings = function(user) {
        var user_settings;

        user_settings = {
            language: formatLanguageJSON(user.language),
            dialect: formatDialectJSON(user.dialect)
        };

        ProfileSettingsService.setSettings(user_settings);

    };
    function setFormValues(settings) {
        $scope.selected_language = settings.language;
        $scope.selected_dialect = settings.dialect;

        $scope.updateDialectList(settings.language.id);


        $scope.user = {
            language: settings.language.id
        };
        if (settings.dialect) {
            $scope.user.dialect = settings.dialect.id;
        }
    }



    angular.extend($scope, {
    });
});