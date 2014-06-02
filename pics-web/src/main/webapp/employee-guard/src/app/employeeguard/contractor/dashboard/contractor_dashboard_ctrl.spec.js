describe('An Employee', function() {
    var scope, httpMock, status;

    var contractor_status_url = '/employee-guard/contractor/summary';
    var contractor_status_dev_url = '/employee-guard/json/contractor/dashboard.json';

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $http, $httpBackend, $routeParams) {
        status = {
            "completed":12,
            "pending":0,
            "expiring":0,
            "expired":10,
            "requested":21
        };

        //Backend definition common for all tests
        $httpBackend.when('GET', contractor_status_url).respond(status);
        $httpBackend.when('GET', contractor_status_dev_url).respond(status);

        scope = $rootScope.$new();
        $controller("contractorDashboardCtrl", {
            $scope: scope
        });

        $httpBackend.flush();
        httpMock = $httpBackend;
    }));

    it("should set status", function() {
        expect(scope.status).toBeDefined();
    });

    afterEach(function() {
        httpMock.verifyNoOutstandingExpectation();
        httpMock.verifyNoOutstandingRequest();
    });
});