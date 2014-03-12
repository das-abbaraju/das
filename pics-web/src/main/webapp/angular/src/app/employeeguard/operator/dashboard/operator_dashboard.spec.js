describe('The Operator Dashboard', function() {
    var scope, $http, httpMock, result;

    beforeEach(angular.mock.module('PICS.employeeguard'));
    beforeEach(inject(function($injector, $rootScope, $controller, $http, $httpBackend, $routeParams) {
            scope = $rootScope.$new();
            $controller("operatorDashboardCtrl", {
                $scope: scope
            });
        scope.site = result;
        httpMock = $httpBackend;
    }));

    describe('request for corporate site information', function() {
        beforeEach(function() {
            result = {
                "id": 1,
                "employees": 852,
                "completed": 714,
                "pending": 0,
                "expiring": 24,
                "expired": 114,
                "projects": [{
                        "name": "Test Project",
                        "startDate": "01-01-2013",
                        "endDate": "01-02-2015",
                        "location": "Main Building",
                        "completed": 3,
                        "pending": 2,
                        "expiring": 4,
                        "expired": 2
                    }, {
                        "name": "Red Dwarf",
                        "startDate": "02-28-2013",
                        "endDate": "03-02-2015",
                        "location": "Offsite",
                        "completed": 1078,
                        "pending": 2,
                        "expiring": 345,
                        "expired": 992
                    }]
                };

            site_list = [{
                        id: 24,
                        name: "Fish Warehouse"
                    },{
                        id: 21,
                        name: "Dougs Ranch"
                    },{
                        id: 2,
                        name: "Barneys Warehouse"
                    }];

            httpMock.when('GET', '/employee-guard/corporates/sites').respond(site_list);
            httpMock.when('GET', /\/employee-guard\/corporates\/sites\/[0-9]+/).respond(result);
            httpMock.flush();
        });

        it('should return a list of sites', function() {
            expect(scope.siteList.length).toEqual(site_list.length);
            expect(scope.siteList[0].id).toEqual(site_list[0].id);
            expect(scope.siteList[0].name).toEqual(site_list[0].name);
        });

        it('should return individual site info if a corporate user', function() {
            expect(scope.isCorporateSiteList(scope.siteList)).toBeTruthy();
            expect(scope.selected_site_details.employees).toEqual(result.employees);
            expect(scope.selected_site_details.projects).toEqual(result.projects);
        });


        describe('updating the selected site', function() {
                var new_result = {
                    "id": 23,
                    "employees": 12,
                    "completed": 2,
                    "pending": 0,
                    "expiring": 13,
                    "expired": 3,
                    "projects": [{
                            "name": "Green Dwarf",
                            "startDate": "02-28-2013",
                            "endDate": "03-02-2015",
                            "location": "Offsite",
                            "completed": 1078,
                            "pending": 2,
                            "expiring": 345,
                            "expired": 992
                        }]
                    };

            it('should select a new selected site', function() {
                scope.updateSelectedSite();

                expect(scope.selected_site_details.employees).toEqual(result.employees);
                expect(scope.selected_site_details.id).toEqual(result.id);
                expect(scope.selected_site_details.projects).toEqual(result.projects);
            });
        });
    });


    describe('request for operator site information', function() {
        var operator_summary;

        beforeEach(function() {
            operator_summary = {
                "id": 52,
                "employees": 1,
                "complete": 1,
                "pending": 0,
                "expiring": 0,
                "expired": 0,
                "projects": [{
                        "name": "New Project",
                        "start_date": "03-22-2013",
                        "end_date": "12-02-2015",
                        "location": "Main Building",
                        "complete": 1,
                        "pending": 0,
                        "expiring": 0,
                        "expired": 0
                    }]
            };

            httpMock.when('GET', '/employee-guard/corporates/sites').respond('');
            httpMock.when('GET', '/employee-guard/operators/summary').respond(operator_summary);
            httpMock.flush();
        });

        it('should return operator summary if site operator user', function() {
            expect(scope.isCorporateSiteList(scope.selected_site_details)).toBeFalsy();

            expect(scope.selected_site_details.id).toEqual(operator_summary.id);
            expect(scope.selected_site_details.employees).toEqual(operator_summary.employees);
            expect(scope.selected_site_details.projects).toEqual(operator_summary.projects);
        });
    });

    describe('call to calculateStatusPercentage', function() {

        it('should calculate a percentage', function() {
            expect(scope.calculateStatusPercentage(5, 10)).toEqual(50);
        });
    });

    describe('call to getProjectStatus', function() {
        it('should return a progress bar object', function() {
            var project = {
                "name": "Red Dwarf",
                "startDate": "02-28-2013",
                "endDate": "03-02-2015",
                "location": "Offsite",
                "completed": 18,
                "pending": 2,
                "expiring": 40,
                "expired": 20
            };

            var progress_bar = {
                success: {
                    amount: 20,
                    width: 25
                },
                warning: {
                    amount: 40,
                    width: 50
                },
                danger: {
                    amount: 20,
                    width: 25
                }
            };

            expect(scope.getProjectStatus(project)).toEqual(progress_bar);
        });
    });


});