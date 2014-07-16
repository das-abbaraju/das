angular.module('PICS.employeeguard')

.controller('profileSettingsCtrl', function ($scope, $filter, $translate, Language, Dialect, Profile) {
    $scope.user = {
        language: '',
        dialect: ''
    };

    getProfileSettings();
    getLanguageList();

    function getProfileSettings() {
        Profile.get().then(function(settings) {
            $scope.profile_settings = settings;
            getDialectList(settings.language.id);
            setSelectedLanguage(settings.language.id);
            setSelectedDialect(settings.dialect.id);
        });
    }

    function getLanguageList() {
        Language.query(function(languages) {
            $scope.languageList = languages;
        });
    }

    function getDialectList (languageID) {
        Dialect.query({id: languageID},function(dialects) {
            $scope.dialectList = dialects;
        });
    }

    function setSelectedLanguage(languageID) {
        $scope.user.language = languageID;
    }

    function setSelectedDialect(dialectID) {
        $scope.user.dialect = dialectID;
    }

    function formatRequestPayload(user) {
        var user_settings;

        if (user.dialect) {
            user_settings = {
                language:formatLanguageJSON(user.language),
                dialect:formatDialectJSON(user.dialect)
            };
        } else {
            user_settings = {
                language:formatLanguageJSON(user.language)
            };
        }

        return user_settings;
    }

    function formatLanguageJSON(selected_language) {
        var language = $filter('filter')($scope.languageList, { id: selected_language})[0];

        if (language) {
            return {
                id: language.id,
                name: language.name
            };
        }
    }

    function formatDialectJSON(selected_dialect) {
        var dialect = $filter('filter')($scope.dialectList, { id: selected_dialect})[0];

        if (dialect) {
            return {
                id: dialect.id,
                name: dialect.name
            };
        }
    }

    function saveProfileSettings(user) {
        var user_settings = formatRequestPayload(user);
        Profile.save(user_settings);
        updateApplicationWithNewLanguage(user_settings);
    }

    function updateApplicationWithNewLanguage(user_settings) {
        if (user_settings.language) {
            if (user_settings.dialect) {
                setTranslationLanguage(user_settings.language.id, user_settings.dialect.id);
            } else {
                setTranslationLanguage(user_settings.language.id);
            }
        }

        // Profile.cacheProfileSettings(user_settings);

        $scope.toggleFormDisplay();

        $scope.profile_settings = user_settings;
    }

    function setTranslationLanguage(languageID, dialectID) {
        if (languageID) {
            if (dialectID) {
                $translate.use(languageID + '_' + dialectID);
            } else {
                $translate.use(languageID);
            }
        }
    }

    $scope.toggleFormDisplay = function() {
        $scope.showEditForm = !$scope.showEditForm;
    };

    angular.extend($scope, {
        getProfileSettings: getProfileSettings,
        getLanguageList: getLanguageList,
        getDialectList: getDialectList,
        setSelectedLanguage: setSelectedLanguage,
        setSelectedDialect: setSelectedDialect,
        saveProfileSettings: saveProfileSettings,
        formatRequestPayload: formatRequestPayload
    });
});