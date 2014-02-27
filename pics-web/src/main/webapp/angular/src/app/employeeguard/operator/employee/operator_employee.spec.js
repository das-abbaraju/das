describe('An Operator Employee', function() {
    var scope, model, mockFactory, data;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, Model) {
        scope = $rootScope.$new();
        $controller("operatorEmployeeCtrl", {
            $scope: scope
        });

        var result = {
            id: 24,
            projects:[
              {
                 id:1,
                 name:"Phase 3 Hermanos Building Construction Site",
                 status:"Expiring",
                 roles:[
                    {
                       skills:[
                          {
                             id:21,
                             projects:[

                             ],
                             status:"Complete",
                             roles:[
                                1
                             ],
                             name:"Defensing Driving"
                          }
                       ],
                       id:1,
                       status:"Expired",
                       name:"Soccer Mom"
                    }
                 ]
              }
            ],
            overallStatus:"Expired",
            roles:[
              {
                 skills:[
                    {
                       id:13,
                       projects:[
                          7
                       ],
                       status:"Complete",
                       roles:[
                          1
                       ],
                       name:"General Safety Training"
                    }
                 ],
                 id:24,
                 status:"Expiring",
                 name:"Destruction"
              }
            ],
            name:"Bob Roberts",
            companies:[
              {
                 id:54578,
                 title:"Forklift Operator",
                 name:"ACME Co."
              }
            ],
            image:"http://www.i-mockery.com/minimocks/ghostbusters2/psychic3.gif"
        };

        scope.model = new Model(result);

        scope.highlightedStatus = result.overallStatus;
    }));

    describe('data model', function() {
        it('should have an id', function() {
            expect(scope.model.data.id).toBeDefined();
            expect(scope.model.data.id).not.toEqual('');
        });

        it('should have an name', function() {
            expect(scope.model.data.name).toBeDefined();
            expect(scope.model.data.name).not.toEqual('');
        });
    });

    describe('sub view change', function() {
        it('should update the template name', function() {
            scope.changeSubView('all');

            expect(scope.subview).toEqual('all');
        });

        it('should update the subview name from item selected', function() {
            scope.changeSubView('role', 'Destruction');

            expect(scope.subview_name).toEqual('Destruction');
        });

        it('should set the selected role', function() {
            var selected_role = {
                 skills:[
                    {
                       id:13,
                       projects:[
                          7
                       ],
                       status:"Complete",
                       roles:[
                          1
                       ],
                       name:"General Safety Training"
                    }
                 ],
                 id:24,
                 status:"Expiring",
                 name:"Destruction"
              };

            scope.changeSubView('role', 'Destruction');

            expect(scope.currentRole).toEqual(selected_role);
        });

        it('should set the selected project', function() {
            var selected_project = {
                id:1,
                name:"Phase 3 Hermanos Building Construction Site",
                status:"Expiring",
                roles:[
                {
                   skills:[
                      {
                         id:21,
                         projects:[

                         ],
                         status:"Complete",
                         roles:[
                            1
                         ],
                         name:"Defensing Driving"
                      }
                   ],
                   id:1,
                   status:"Expired",
                   name:"Soccer Mom"
                }
                ]
            };

            scope.changeSubView('project', 'Phase 3 Hermanos Building Construction Site');

            expect(scope.currentProject).toEqual(selected_project);
        });
    });
});