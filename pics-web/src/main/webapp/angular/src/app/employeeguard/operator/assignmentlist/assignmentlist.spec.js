describe('The Operator Assignment Page', function() {
    var scope, $http, httpMock, result;

    var site_assignments = {
        "id": 1,
        "employees": 852,
        "completed": 714,
        "pending": 0,
        "expiring": 24,
        "expired": 114
        };

    var project_assignments = [
       {
          "employees":4,
          "completed":3,
          "pending":0,
          "expiring":0,
          "expired":1,
          "id":4,
          "name":"ABC Project",
          "location":"Onsite "
       },
       {
          "employees":4,
          "completed":2,
          "pending":0,
          "expiring":0,
          "expired":2,
          "id":1,
          "name":"Phase 3 Hermanos Building Construction Site",
          "location":"London, UK",
          "startDate":1385884800000,
          "endDate":1404457200000
       },
       {
          "employees":6,
          "completed":4,
          "pending":0,
          "expiring":0,
          "expired":2,
          "id":2,
          "name":"Walter Site Demolition",
          "location":"Birmingham, UK",
          "startDate":1393747200000,
          "endDate":1418112000000
       }
    ];

    var site_list = [{
        id: 24,
        name: "Fish Warehouse"
    },{
        id: 21,
        name: "Dougs Ranch"
    },{
        id: 2,
        name: "Barneys Warehouse"
    }];

    beforeEach(angular.mock.module('PICS.employeeguard'));
    beforeEach(inject(function($injector, $rootScope, $controller, $http, $httpBackend, $routeParams) {
            scope = $rootScope.$new();
            $controller("operatorAssignmentsCtrl", {
                $scope: scope
            });
        scope.site = result;
        httpMock = $httpBackend;
    }));

    describe('request for corporate site information', function() {
        beforeEach(function() {
            httpMock.when('GET', /\employee-guard\/corporates\/sites/).respond(site_list);
            httpMock.when('GET', /\employee-guard\/operators\/assignments\/summary\/[0-9]+/).respond(site_assignments);
            httpMock.when('GET', /\employee-guard\/operators\/assignments\/summary/).respond(site_assignments);
            httpMock.when('GET', /\employee-guard\/operators\/assignments\/projects/).respond(project_assignments);
            httpMock.when('GET', /\employee-guard\/operators\/assignments\/projects\/[0-9]+/).respond(project_assignments);
            httpMock.flush();
        });

        it('should return a list of sites', function() {
            expect(scope.siteList.length).toEqual(site_list.length);
            expect(scope.siteList[0].id).toEqual(site_list[0].id);
            expect(scope.siteList[0].name).toEqual(site_list[0].name);
        });

        it('should return individual site info if a corporate user', function() {
            expect(scope.hasSites(scope.siteList)).toBeTruthy();
            expect(scope.site_assignments.employees).toEqual(site_assignments.employees);
            expect(scope.project_assignments[0].employees).toEqual(project_assignments[0].employees);
        });

        it("should return the name of selected_site", function() {
            expect(scope.getSiteNameFromSiteList(24)).toEqual('Fish Warehouse');
        });
    });


    describe('request for operator site information', function() {
        var operator_summary,
            project_summary;

        beforeEach(function() {
            operator_summary = {
                "id": 52,
                "employees": 1,
                "complete": 1,
                "pending": 0,
                "expiring": 0,
                "expired": 0
            };

            project_summary = [{
                "name": "New Project",
                "start_date": "03-22-2013",
                "end_date": "12-02-2015",
                "location": "Main Building",
                "complete": 1,
                "pending": 0,
                "expiring": 0,
                "expired": 0
            }];

            httpMock.when('GET', /\employee-guard\/corporates\/sites/).respond('');
            httpMock.when('GET', /\employee-guard\/operators\/assignments\/summary/).respond(operator_summary);
            httpMock.when('GET', /\employee-guard\/operators\/assignments\/projects/).respond(project_summary);
            httpMock.flush();
        });

        it('should return operator summary if site operator user', function() {
            expect(scope.hasSites(scope.siteList)).toBeFalsy();

            expect(scope.site_assignments.id).toEqual(operator_summary.id);
            expect(scope.site_assignments.employees).toEqual(operator_summary.employees);
            expect(scope.project_assignments[0].name).toEqual(project_summary[0].name);
        });
    });
});
