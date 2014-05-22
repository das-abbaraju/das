angular.module('PICS.services')

// Based on https://gist.github.com/mrgamer/6139485
.factory('throttle', ['$timeout', function ($timeout) {
    return function (delay, noTrailing, callback, debounceMode) {
        var lastExecution = 0,
            timeoutId;

        if (typeof noTrailing !== 'boolean') {
            debounceMode = callback;
            callback = noTrailing;
            noTrailing = null;
        }

        return function () {
            var that = this,
                elapsed = +new Date() - lastExecution,
                args = arguments;

            function exec() {
                lastExecution = +new Date();
                callback.apply(that, args);
            }

            function clear() {
                timeoutId = null;
            }

            if (debounceMode && !timeoutId) {
                exec();
            }

            if (timeoutId) {
                $timeout.cancel(timeoutId);
            }

            if (debounceMode === null && elapsed > delay) {
                exec();
            } else if (noTrailing !== true) {
                timeoutId = $timeout(debounceMode ? clear : exec, debounceMode === null ? delay - elapsed : delay);
            }
        };
    };
}]);