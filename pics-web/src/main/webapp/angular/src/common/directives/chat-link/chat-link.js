angular.module('PICS.directives')

.directive('chatlink', function (chatLinkService) {
    function getFullUrlFromBaseUrl(baseUrl) {
        var url_param = document.location.href,
            referrer_param = document.referrer,
            url = baseUrl + '&url=' + url_param + '&referrer=' + referrer_param;

        return url;
    }

    function getClickHandlerFromFullUrl(fullUrl) {
        return function (event) {
            if (navigator.userAgent.toLowerCase().indexOf('opera') != -1 && window.event.preventDefault) {
                window.event.preventDefault();
            }

            this.newWindow = window.open(fullUrl, 'webim', 'toolbar=0, scrollbars=0, location=0, status=1, menubar=0, width=640, height=480, resizable=1');
            this.newWindow.focus();
            this.newWindow.opener = window;

            return false;
        };
    }

    return {
        restrict: 'E',
        replace: true,
        template: '<a href="{{ href }}" ng-click="openMibew()" target="_blank" class="chat-link">Chat</a>',
        scope: {
            languageId: '='
        },
        link: function (scope) {
            scope.$watch('languageId', function (newLanguageId) {
                chatLinkService.get({
                    languageId: newLanguageId
                }, function (data) {
                    var mibewBaseUrl = data.name,
                        mibewUrl = getFullUrlFromBaseUrl(mibewBaseUrl);
                    
                    scope.href = mibewUrl;
                    scope.openMibew = getClickHandlerFromFullUrl(mibewUrl);
                });
            });
        }
    };
});