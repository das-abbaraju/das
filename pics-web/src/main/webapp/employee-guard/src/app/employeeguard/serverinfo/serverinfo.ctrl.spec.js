describe('An Operator Project List', function() {
    var scope;
    var live = {
        url: '',
        response: {
            "os": "Macintosh",
            "browser": "Chrome",
            "environment": "localhost",
            "name": "DB@pics_alpha1",
            "time": "2014-05-10 13:58:31"
        }
    };
    var dev = {
        url: '/angular/json/serverinfo/serverinfo.json',
        response: {
            "os": "Macintosh",
            "browser": "Chrome",
            "environment": "localhost",
            "name": "DB@pics_alpha1",
            "time": "2014-05-10 13:58:31"
        }
    };


    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend) {
        scope = $rootScope.$new();
        $controller("serverInfoCtrl", {
            $scope: scope
        });

        $httpBackend.when('GET', dev.url).respond(dev.response);
        $httpBackend.when('GET', live.url).respond(live.response);
        $httpBackend.flush();
    }));

    it("should return server information", function() {
        expect(scope.server).toBeDefined();
        expect(scope.server.name).toEqual('DB@pics_alpha1');
    });
});