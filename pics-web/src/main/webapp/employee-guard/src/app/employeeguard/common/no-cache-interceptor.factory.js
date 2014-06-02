angular.module('PICS.employeeguard')

.factory('noCacheInterceptor', function () {
    return {
        request: function (config) {
            if (config.url.indexOf('tpl.html') === -1) {
                if(config.method === 'GET'){
                    var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                    config.url = config.url+separator+'noCache=' + new Date().getTime();
                }
            }

            return config;
       }
    };
});