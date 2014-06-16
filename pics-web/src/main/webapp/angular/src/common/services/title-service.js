angular.module('PICS.services')

.provider('titleService', function () {
    var prefix = '';
  
    this.setPrefix = function(value) {
        prefix = value;
    };

    this.$get = function($rootScope, $document) {
        function init() {
            $rootScope.$on('$routeChangeStart', function (event, current, previous) {
                $rootScope.$watch('text', function () {
                    if ($rootScope.text) {
                        $document[0].title = prefix + ' - ' + $rootScope.text[current.$$route.title];
                    }
                });
            }, true);
        }

        return {
            init: init
        };
    };
});