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
 *    A) For a translation with key 'my.translation.key' and value 'My translation value':
 *       <p translate>{{ text['my.translation.key'] }}</p>
 *
 *    B) Translation values may also include parameters, e.g.:
 *       
 *       For a translation with key 'my.first.translation.key' and value 'My {1} value for translation #{2}':
 *       <p translate>{{ text['my.first.translation.key'] | translationValues:['translation', '1'] }}</p>
 *
 * Translation keys for each route must be added to the routePathToTranslationKeys value in translationKeys.js.
 * The value of routeKeyToTranslationKeys is a JavaScript object mapping each route path to an array of translation keys
 * associated with that route path.
 *
 * These key-value pairs may added to translationKeys.js manually, or they may be added automatically by following these steps:
 *
 * 1) Inject translationsService into your main app module's run block, and call setDevelopmentMode,
 *    passing the string 'on', e.g.:
 *
 *    app.module('app').run(function (translationsService) {
 *        translationsService.setDevelopmentMode('on');
 *    });
 *
 * 2) In Terminal, navigate to /angular, and type: node translation.js.
 *
 * 3) Open your browser, and navigate to the target route path.
 *
 * 4) The translation keys in translationKeys.js will be added if they were missing or updated if they existed previously.
 *
 * 5) To turn logging off (e.g., for production), remove the call to setDevelopmentMode, or pass to it any value other than 'on'.
 *
 */
(function () {
    var translationKeys = [],
        routePath;

    angular.module('PICS.translations', [])

    .config(function ($provide) {
        $provide.factory('translationsService', function ($http, $rootScope, $q, routePathToTranslationKeys) {
            var deferred = $q.defer();

            function setDevelopmentMode(value) {
                isDevelopmentMode = (value == 'on');
            }

            function isDevelopmentMode() {
                return isDevelopmentMode;
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
                var translations = value;

                if (isDevelopmentMode) {
                    replaceEmptyStringValuesWithKeys(translations);
                }

                $rootScope.text = translations;
                deferred.resolve(translations);
            }

            function replaceEmptyStringValuesWithKeys(obj) {
                angular.forEach(obj, function (value, key) {
                    obj[key] = value || key;
                });
            }

            function getTranslations() {
                return deferred.promise;
            }

            return {
                setDevelopmentMode: setDevelopmentMode,
                isDevelopmentMode: isDevelopmentMode,
                fetchTranslations: fetchTranslations,
                createRouteParamsFromKeys: createRouteParamsFromKeys,
                getRoutePathToTranslationKeys: getRoutePathToTranslationKeys,
                getTranslations: getTranslations,
                setTranslations: setTranslations
            };
        });
    })

    .run(function ($rootScope, $http, $q, translationsService) {    
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
        });
    })

    .directive('translatedPage', function ($rootScope, $http, $log, translationsService) {
        function getKeyValueJson(key, value) {
            return '"' + routePath + '":' + JSON.stringify(translationKeys);
        }

        return {
            restrict: 'A',
            link: function (scope) {
                scope.$on('$viewContentLoaded', function () {
                    if (translationsService.isDevelopmentMode()) {
                        var newKeyValuePair = {};

                        newKeyValuePair[routePath] = translationKeys;

                        $http.post('http://localhost:8081', newKeyValuePair);

                        $log.info(getKeyValueJson(routePath, JSON.stringify(translationKeys)));
                    }
                });
            }
        };
    })

    .directive('translate', function (translationsService) {
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
                if (translationsService.isDevelopmentMode()) {
                    addKeysFromElementText(element.text());
                }
            }
        };
    })

    .filter('translationValues', function () {
        return function (translationExpression, replaceValues) {
            if (!translationExpression) return;

            function replaceFn (replaceParam, replaceValueIndex) {
                return replaceValues[replaceValueIndex];
            }

            return translationExpression.replace(/{([0-9]+)}/g, replaceFn) || '';
        };
    });
}());