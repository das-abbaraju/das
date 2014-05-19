describe('No Cache Interceptor', function() {
    var scope, noCacheInterceptor;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, _noCacheInterceptor_) {
        noCacheInterceptor = _noCacheInterceptor_;
    }));

    it("should add a random string to all get requests that are not templates", function() {
        var request_config = {
                method: 'GET',
                url: '/employee-guard/operators/who-am-i'
            },
            config = noCacheInterceptor.request(request_config),
            regex = /\/employee-guard\/operators\/who-am-i\?noCache=[0-9]+/;

        expect(config.url).toMatch(regex);
    });

    it("should add '&' if request param already exists", function() {
        var request_config = {
                method: 'GET',
                url: '/employee-guard/operators/who-am-i?param1=true'
            },
            config = noCacheInterceptor.request(request_config),
            regex = /\/employee-guard\/operators\/who-am-i\?param1=true&noCache=[0-9]+/;

        expect(config.url).toMatch(regex);
    });

    it("should NOT add a random string to template request", function() {
        var request_config = {
                method: 'GET',
                url: '/employee-guard/src/app/employeeguard/operator/dashboard/_site-status.tpl.html'
            },
            config = noCacheInterceptor.request(request_config);

        expect(config.url).toMatch(request_config.url);
    });

    it("should NOT add a random string for POST requests", function() {
        var request_config = {
                method: 'POST',
                url: '/employee-guard/operators/who-am-i'
            },
            config = noCacheInterceptor.request(request_config);

        expect(config.url).toMatch(request_config.url);
    });
});

