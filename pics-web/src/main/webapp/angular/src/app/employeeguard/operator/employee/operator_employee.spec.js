describe('operator employee', function() {
    var scope, model, mockFactory, data;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(function () {
        angular.mock.inject(function ($injector) {
            $httpBackend = $injector.get('$httpBackend');
            mockUserResource = $injector.get('EmployeeSkills');
        });
    });


    beforeEach(inject(function($rootScope, $controller, Model) {
        scope = $rootScope.$new();
        $controller("operatorEmployeeCtrl", {
            $scope: scope
        });


            // $httpBackend.expectGET('/angular/json/dummyData.json')
                // .respond([{
                //    id:1,
                //    projects:[
                //       {
                //          id:1,
                //          skills:[
                //             {
                //                id:14,
                //                projects:[

                //                ],
                //                status:"Expired",
                //                roles:[
                //                   1
                //                ],
                //                name:"Site Orientation Expired"
                //             }
                //          ],
                //          name:"Phase 3 Hermanos Building Construction Site",
                //          status:"Expiring",
                //          roles:[
                //             {
                //                skills:[
                //                   {
                //                      id:21,
                //                      projects:[

                //                      ],
                //                      status:"Complete",
                //                      roles:[
                //                         1
                //                      ],
                //                      name:"Defensing Driving"
                //                   }
                //                ],
                //                id:1,
                //                status:"Expired",
                //                name:"Soccer Mom"
                //             }
                //          ]
                //       }
                //    ],
                //    overallStatus:"Expired",
                //    roles:[
                //       {
                //          skills:[
                //             {
                //                id:51,
                //                projects:[

                //                ],
                //                status:"Expired",
                //                roles:[
                //                   1
                //                ],
                //                name:"Offensive Driving"
                //             }
                //          ],
                //          id:1,
                //          status:"Expired",
                //          name:"Equipment Operator"
                //       },
                //       {
                //          skills:[
                //             {
                //                id:13,
                //                projects:[
                //                   7
                //                ],
                //                status:"Complete",
                //                roles:[
                //                   1
                //                ],
                //                name:"General Safety Training"
                //             }
                //          ],
                //          id:24,
                //          status:"Expiring",
                //          name:"Destruction"
                //       }
                //    ],
                //    name:"Bob Roberts",
                //    companies:[
                //       {
                //          id:54578,
                //          title:"Forklift Operator",
                //          name:"ACME Co."
                //       }
                //    ],
                //    image:"http://www.i-mockery.com/minimocks/ghostbusters2/psychic3.gif"
                // }]);

            // data = mockUserResource.get();

            // $httpBackend.flush();

    }));

    describe('changing a sub view', function() {
        it('should update the subview template name', function() {
            scope.changeSubView('all');

            expect(scope.subview).toEqual('all');
        });

        // it('should set selected project to menu item name', function() {
        //     scope.changeSubView('project', 'Building Blow upping');

        //     expect(scope.subview).toEqual('project');
        //     expect(scope.subview_name).toEqual('Building Blow upping');
        // });

        it('should set selected role to menu item name', inject(function (EmployeeSkills, Model) {
            // $httpBackend.expectGET('/angular/json/dummyData.json')
            //     .respond([{
            //        id:1,
            //        projects:[
            //           {
            //              id:1,
            //              skills:[
            //                 {
            //                    id:14,
            //                    projects:[

            //                    ],
            //                    status:"Expired",
            //                    roles:[
            //                       1
            //                    ],
            //                    name:"Site Orientation Expired"
            //                 }
            //              ],
            //              name:"Phase 3 Hermanos Building Construction Site",
            //              status:"Expiring",
            //              roles:[
            //                 {
            //                    skills:[
            //                       {
            //                          id:21,
            //                          projects:[

            //                          ],
            //                          status:"Complete",
            //                          roles:[
            //                             1
            //                          ],
            //                          name:"Defensing Driving"
            //                       }
            //                    ],
            //                    id:1,
            //                    status:"Expired",
            //                    name:"Soccer Mom"
            //                 }
            //              ]
            //           }
            //        ],
            //        overallStatus:"Expired",
            //        roles:[
            //           {
            //              skills:[
            //                 {
            //                    id:51,
            //                    projects:[

            //                    ],
            //                    status:"Expired",
            //                    roles:[
            //                       1
            //                    ],
            //                    name:"Offensive Driving"
            //                 }
            //              ],
            //              id:1,
            //              status:"Expired",
            //              name:"Equipment Operator"
            //           },
            //           {
            //              skills:[
            //                 {
            //                    id:13,
            //                    projects:[
            //                       7
            //                    ],
            //                    status:"Complete",
            //                    roles:[
            //                       1
            //                    ],
            //                    name:"General Safety Training"
            //                 }
            //              ],
            //              id:24,
            //              status:"Expiring",
            //              name:"Destruction"
            //           }
            //        ],
            //        name:"Bob Roberts",
            //        companies:[
            //           {
            //              id:54578,
            //              title:"Forklift Operator",
            //              name:"ACME Co."
            //           }
            //        ],
            //        image:"http://www.i-mockery.com/minimocks/ghostbusters2/psychic3.gif"
            //     }]);

            // var data = mockUserResource.get();
            // console.log(data);

            // $httpBackend.flush();
            var data = {
               id:1,
               projects:[
                  {
                     id:1,
                     skills:[
                        {
                           id:14,
                           projects:[

                           ],
                           status:"Expired",
                           roles:[
                              1
                           ],
                           name:"Site Orientation Expired"
                        }
                     ],
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
                           id:51,
                           projects:[

                           ],
                           status:"Expired",
                           roles:[
                              1
                           ],
                           name:"Offensive Driving"
                        }
                     ],
                     id:1,
                     status:"Expired",
                     name:"Equipment Operator"
                  },
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

            model = new Model(data);

            console.log(model);

            // scope.changeSubView('role', 'Destruction');

            // expect(scope.subview).toEqual('role');
            // expect(scope.subview_name).toEqual('Equipment Operator');
        }));
    });

    // describe('filtering roles', function() {
    //     beforeEach(function () {
    //         scope.highlightedStatus = "Complete";
    //         scope.changeSubView('role', 'Destruction');
    //     });

    //     it('should return only the selected role', function() {
    //         scope.getRoleByName('Destruction');

    //         var selected_role = {
    //             id:24,
    //             status:"Expiring",
    //             name:"Destruction"
    //         };

    //         expect(scope.currentRole).toEqual(selected_role);
    //     });

    //     it('should set the visible status icon to the role status', function() {
    //         var selected_role = {
    //             id:24,
    //             status:"Expiring",
    //             name:"Destruction"
    //         };
    //         scope.getRoleByName(selected_role);


    //         expect(scope.highlightedStatus).toEqual(selected_role.status);
    //     });
    // });
















    // describe('filtering projects', function() {
    //     beforeEach(function () {
    //         scope.highlightedStatus = "Complete";
    //         scope.changeSubView('project', 'Destruction');
    //     });

    //     it('should return only the selected role', function() {
    //         var selected_role = {
    //             id:24,
    //             status:"Expiring",
    //             name:"Destruction"
    //         };

    //         expect(scope.roleFilter(selected_role)).toEqual(selected_role);
    //     });

    //     it('should set the visible status icon to the role status', function() {
    //         var selected_role = {
    //             id:24,
    //             status:"Expiring",
    //             name:"Destruction"
    //         };
    //         scope.roleFilter(selected_role);


    //         expect(scope.highlightedStatus).toEqual(selected_role.status);
    //     });
    // });

    // it('should set selected item', function() {
    //     scope.select('Green Eyes');

    //     expect(scope.selected).toEqual('Green Eyes');
    // });

    // it('should be true when current selection is the same as selected', function() {
    //     scope.select('Rosebud');

    //     expect(scope.isSelected('Rosebud')).toBeTruthy();
    // });

    // it('should be false when current selection is NOT the same as selected', function() {
    //     scope.select('Rosebud');

    //     expect(scope.isSelected('Rose')).toBeFalsy();
    // });
});