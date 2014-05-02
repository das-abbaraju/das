/**
 * @name Translations
 * @author Jason Roos
 * @date 2013-4-23
 *
 * @description
 *
 * This is an AngularJS module to handle content translation.
 *
 * To implement:
 *
 * 1) Include translations.js and translationsKeys.js in your index.html.
 * 2) Inject the translationsService into your main app module.
 * 3) Add the attribute directive "translated-page" to your "ng-view" element directive.
 * 4) Bind the promise returned by getTranslations to the resolve object of the target route, e.g.:
 *
 *     .when('/my-route', {
 *         resolve: {
 *              text: function (translationsService) {
 *                  return translationsService.getTranslations();
 *              }
 *          }
 *      });
 *
 * 5) Use the following syntax in your template files:
 *
 *    For a translation with key 'my.first.translation.key' and value 'My {1} value for translation #{2}':
 *    <p>{{ text['my.first.translation.key'] | translationValues:['translation', '1'] }}</p>
 *
 * Translation keys for each route must be added to the routePathToTranslationKeys value
 * in translationKeys.js. The value of routeKeyToTranslationKeys is a JavaScript object mapping
 * each route path to an array of translation keys associated with that route path.
 *
 * You can generate these key-value pairs automatically by doing the following:
 *
 * 1) Inject translationsService into your main app module's run block, and call setLogKeysToConsole,
 *    passing a boolean value of true, e.g.:
 *
 *    app.module('app').run(function (translationsService) {
 *        translations.Service.setLogKeysToConsole(true);
 *    });
 *
 * 2) Open your browser, and navigate to the target route path.
 *
 * 3) Open the JavaScript console of your browser to view the key-value pair.
 *
 * 4) Copy-and-paste the key-value pair into the translationKeys file.
 *
 * 5) To turn logging off (e.g., for production), change the value passed to setLogKeysToConsole to false.
 */
(function () {
    var logKeysToConsole = true,
        translationKeys = [],
        routePath;

    angular.module('PICS.translations', [])

    .config(['$provide', function ($provide) {
        $provide.factory('translationsService', ['$http', '$rootScope', '$q', 'routePathToTranslationKeys',
            function ($http, $rootScope, $q, routePathToTranslationKeys) {
                var deferred = $q.defer();

                function setLogKeysToConsole(value) {
                    logKeysToConsole = !!value;
                }

                function createRouteParamsFromKeys(keys) {
                    return {
                        translationKeys: keys
                    };
                }

                function getRoutePathToTranslationKeys() {
                    return routePathToTranslationKeys;
                }

                function fetchTranslations(requestParams) {
                    return $http.post('/translations.action', requestParams);
                }

                function setTranslations(value) {
                    $rootScope.text = value;
                    deferred.resolve(value);
                }

                function getTranslations() {
                    return deferred.promise;
                }

                return {
                    setLogKeysToConsole: setLogKeysToConsole,
                    fetchTranslations: fetchTranslations,
                    createRouteParamsFromKeys: createRouteParamsFromKeys,
                    getRoutePathToTranslationKeys: getRoutePathToTranslationKeys,
                    getTranslations: getTranslations,
                    setTranslations: setTranslations
                };
            }
        ]);
    }])

    .run(['$rootScope', '$http', '$q', 'translationsService',
        function ($rootScope, $http, $q, translationsService) {    
            $rootScope.$on('$routeChangeStart', function (event, next) {
                var routePathToTranslationKeys = translationsService.getRoutePathToTranslationKeys(),
                    keys, requestParams;

                routePath = next.$$route.originalPath;
                translationKeys = [];

                keys = routePathToTranslationKeys[routePath];
                requestParams = translationsService.createRouteParamsFromKeys(keys);

                translationsService.fetchTranslations(requestParams)
                .then(function (response) {
                    translationsService.setTranslations(response.data.translationsMap);
                });
            }
        );
    }])

    .directive('translatedPage', ['$rootScope', '$http', '$log',
        function ($rootScope, $http, $log) {
            function getKeyValueJson(key, value) {
                return '"' + routePath + '":' + JSON.stringify(translationKeys);
            }

            return {
                restrict: 'A',
                link: function (scope) {
                    scope.$on('$includeContentLoaded', function () {
                        if (logKeysToConsole) {
                            $log.info(getKeyValueJson(routePath, JSON.stringify(translationKeys)));
                        }
                    });
                }
            };
        }
    ])

    .directive('translate', function () {
        function getKeysFromText(text) {
            var expressions = text.match(/[{\s*]text\[('|\")[\w.]+('|\")\]/g),
                keys = [];

            angular.forEach(expressions, function (expression, index) {
                keys.push(expression.match(/\['[\w.]+\']/)[0].match(/[\w.]+/)[0]);
            });

            return keys;
        }

        function addKeysFromElementText(text) {
            var keys = getKeysFromText(text);

            angular.forEach(keys, function (key, index) {
                translationKeys.push(key);
            });
        }

        return {
            restrict: 'A',
            link: function (scope, element) {
                if (logKeysToConsole) {
                    addKeysFromElementText(element.text());
                }
            }
        };
    })

    .filter('translationValues', function () {
        return function (translationExpression, replaceValues) {
            function replaceFn (replaceParam, replaceValueIndex) {
                return replaceValues[replaceValueIndex-1];
            }

            return translationExpression.replace(/{([0-9]+)}/g, replaceFn);
        };
    });
}());