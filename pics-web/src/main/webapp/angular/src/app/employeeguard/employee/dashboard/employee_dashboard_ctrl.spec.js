describe('An Employee', function() {
    var scope, $http, $httpBackend, httpMock, employee;

    var employee_info_url = '/employee-guard/employee/summary/employee-info';

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $http, $httpBackend, $routeParams) {
        employee = {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "slug": "PBANDJ",
            "email": "thelennyleonard@gmail.com"
        };

        //To capture assignment call only
        $httpBackend.when('GET', '/employee-guard/employee/summary/assignments').respond('');

        scope = $rootScope.$new();
        $controller("employeeDashboardCtrl", {
            $scope: scope
        });

        httpMock = $httpBackend;
    }));

    afterEach(function() {
        httpMock.verifyNoOutstandingExpectation();
        httpMock.verifyNoOutstandingRequest();
    });

    it('should use the email as a slug if no one defined', function() {
        var employee = {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "email": "thelennyleonard@gmail.com"
        };

        httpMock.when('GET', employee_info_url).respond(employee);
        httpMock.flush();

        expect(scope.employee.slug).toBeDefined();
        expect(scope.employee.slug).toEqual(scope.employee.email);
    });
});