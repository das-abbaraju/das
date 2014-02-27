describe('An Operator Employee Data Model', function() {
    var employee, data;

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function(Employee) {
        data = {
               "id":1,
               "projects":[
                  {
                     "id":1,
                     "skills":[
                        {
                           "id":14,
                           "projects":[

                           ],
                           "status":"Expired",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Expired"
                        },
                        {
                           "id":21,
                           "projects":[

                           ],
                           "status":"Complete",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Complete"
                        },
                        {
                           "id":34,
                           "projects":[

                           ],
                           "status":"Expiring",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Expiring"
                        },
                        {
                           "id":34,
                           "projects":[

                           ],
                           "status":"Pending",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Pending"
                        }
                     ],
                     "name":"Phase 3 Hermanos Building Construction Site",
                     "status":"Expiring",
                     "roles":[
                        {
                           "skills":[
                              {
                                 "id":21,
                                 "projects":[

                                 ],
                                 "status":"Complete",
                                 "roles":[
                                    1
                                 ],
                                 "name":"Defensing Driving"
                              }
                           ],
                           "id":1,
                           "status":"Expired",
                           "name":"Soccer Mom"
                        },
                        {
                           "skills":[
                              {
                                 "id":22,
                                 "projects":[

                                 ],
                                 "status":"Expiring",
                                 "roles":[
                                    1
                                 ],
                                 "name":"Getaway Driving"
                              }
                           ],
                           "id":1,
                           "status":"Expired",
                           "name":"Driver"
                        }
                     ]
                  },
                  {
                     "id":32,
                     "skills":[
                        {
                           "id":14,
                           "projects":[

                           ],
                           "status":"Expired",
                           "roles":[
                              1
                           ],
                           "name":"This skill sucks"
                        },
                        {
                           "id":21,
                           "projects":[

                           ],
                           "status":"Complete",
                           "roles":[
                              1
                           ],
                           "name":"Blow up building"
                        },
                        {
                           "id":34,
                           "projects":[

                           ],
                           "status":"Expiring",
                           "roles":[
                              1
                           ],
                           "name":"Blow up buliding more"
                        },
                        {
                           "id":99,
                           "projects":[

                           ],
                           "status":"Pending",
                           "roles":[
                              1
                           ],
                           "name":"Building go boom"
                        }
                     ],
                     "name":"Building Blow upping",
                     "status":"Complete",
                     "roles":[
                        {
                           "skills":[
                              {
                                 "id":99,
                                 "projects":[

                                 ],
                                 "status":"Expiring",
                                 "roles":[
                                    1
                                 ],
                                 "name":"Steel Grinding"
                              }
                           ],
                           "id":1,
                           "status":"Expired",
                           "name":"Equipment Operator"
                        },
                        {
                           "skills":[
                              {
                                 "id":51,
                                 "projects":[

                                 ],
                                 "status":"Complete",
                                 "roles":[
                                    1
                                 ],
                                 "name":"Wall Building"
                              },
                              {
                                 "id":23,
                                 "projects":[

                                 ],
                                 "status":"Expired",
                                 "roles":[
                                    1
                                 ],
                                 "name":"Bullseye hitting"
                              }
                           ],
                           "id":1,
                           "status":"Expired",
                           "name":"Defensive Architect"
                        }
                     ]
                  }
               ],
               "overallStatus":"Expired",
               "roles":[{
                     "skills":[
                        {
                           "id":1,
                           "projects":[
                              1,
                              7,
                              10
                           ],
                           "status":"Expiring",
                           "roles":[
                              1,
                              23
                           ],
                           "name":"Personal Driver's License"
                        },
                        {
                           "id":13,
                           "projects":[
                              7
                           ],
                           "status":"Complete",
                           "roles":[
                              1
                           ],
                           "name":"General Destruction Training"
                        },
                        {
                           "id":51,
                           "projects":[

                           ],
                           "status":"Expired",
                           "roles":[
                              1
                           ],
                           "name":"Offensive Driving"
                        }
                     ],
                     "id":1,
                     "status":"Expired",
                     "name":"Equipment Operator"
                  },
                  {
                     "skills":[
                        {
                           "id":24,
                           "projects":[
                              1,
                              7,
                              10
                           ],
                           "status":"Complete",
                           "roles":[
                              1,
                              23
                           ],
                           "name":"Commercial Driver's License"
                        },
                        {
                           "id":13,
                           "projects":[
                              7
                           ],
                           "status":"Complete",
                           "roles":[
                              1
                           ],
                           "name":"General Safety Training"
                        },
                        {
                           "id":51,
                           "projects":[

                           ],
                           "status":"Expired",
                           "roles":[
                              1
                           ],
                           "name":"Defensing Driving"
                        }
                     ],
                     "id":24,
                     "status":"Expiring",
                     "name":"Destruction"
                  }
               ],
               "name":"Bob Roberts",
               "companies":[
                  {
                     "id":"54578",
                     "title":"Forklift Operator",
                     "name":"ACME Co."
                  }
               ],
               "image":"http://www.i-mockery.com/minimocks/ghostbusters2/psychic3.gif"
            };

        employee = new Employee(data);
    }));

    it('should have an id', function() {
        expect(employee.data.id).toBeDefined();
        expect(employee.data.id).not.toEqual('');
    });

    it('should have an name', function() {
        expect(employee.data.name).toBeDefined();
        expect(employee.data.name).not.toEqual('');
    });

    it('should return the employee data', function() {
        expect(employee.getData()).toEqual(data);
    });

    it('should get all project skills', function() {
        var projectSkills = [
           {
              "id":14,
              "projects":[

              ],
              "status":"Expired",
              "roles":[
                 1
              ],
              "name":"Site Orientation Expired"
           },
           {
              "id":21,
              "projects":[

              ],
              "status":"Complete",
              "roles":[
                 1
              ],
              "name":"Site Orientation Complete"
           },
           {
              "id":34,
              "projects":[

              ],
              "status":"Expiring",
              "roles":[
                 1
              ],
              "name":"Site Orientation Expiring"
           },
           {
              "id":34,
              "projects":[

              ],
              "status":"Pending",
              "roles":[
                 1
              ],
              "name":"Site Orientation Pending"
           },
           {
              "id":14,
              "projects":[

              ],
              "status":"Expired",
              "roles":[
                 1
              ],
              "name":"This skill sucks"
           },
           {
              "id":21,
              "projects":[

              ],
              "status":"Complete",
              "roles":[
                 1
              ],
              "name":"Blow up building"
           },
           {
              "id":34,
              "projects":[

              ],
              "status":"Expiring",
              "roles":[
                 1
              ],
              "name":"Blow up buliding more"
           },
           {
              "id":99,
              "projects":[

              ],
              "status":"Pending",
              "roles":[
                 1
              ],
              "name":"Building go boom"
           }
        ];

        expect(employee.getAllProjectSkills()).toEqual(projectSkills);
    });

    it('should get all project roles', function() {
        var projectRoles = [
            {
               "skills":[
                  {
                     "id":21,
                     "projects":[

                     ],
                     "status":"Complete",
                     "roles":[
                        1
                     ],
                     "name":"Defensing Driving"
                  }
               ],
               "id":1,
               "status":"Expired",
               "name":"Soccer Mom"
            },
            {
               "skills":[
                  {
                     "id":22,
                     "projects":[

                     ],
                     "status":"Expiring",
                     "roles":[
                        1
                     ],
                     "name":"Getaway Driving"
                  }
               ],
               "id":1,
               "status":"Expired",
               "name":"Driver"
            },{
               "skills":[
                  {
                     "id":99,
                     "projects":[

                     ],
                     "status":"Expiring",
                     "roles":[
                        1
                     ],
                     "name":"Steel Grinding"
                  }
               ],
               "id":1,
               "status":"Expired",
               "name":"Equipment Operator"
            },
            {
               "skills":[
                  {
                     "id":51,
                     "projects":[

                     ],
                     "status":"Complete",
                     "roles":[
                        1
                     ],
                     "name":"Wall Building"
                  },
                  {
                     "id":23,
                     "projects":[

                     ],
                     "status":"Expired",
                     "roles":[
                        1
                     ],
                     "name":"Bullseye hitting"
                  }
               ],
               "id":1,
               "status":"Expired",
               "name":"Defensive Architect"
            }
        ];

        expect(employee.getAllProjectRoles()).toEqual(projectRoles);
    });

    it('should return only the selected role', function() {
        var selected_role = {
                     "skills":[
                        {
                           "id":1,
                           "projects":[
                              1,
                              7,
                              10
                           ],
                           "status":"Expiring",
                           "roles":[
                              1,
                              23
                           ],
                           "name":"Personal Driver's License"
                        },
                        {
                           "id":13,
                           "projects":[
                              7
                           ],
                           "status":"Complete",
                           "roles":[
                              1
                           ],
                           "name":"General Destruction Training"
                        },
                        {
                           "id":51,
                           "projects":[

                           ],
                           "status":"Expired",
                           "roles":[
                              1
                           ],
                           "name":"Offensive Driving"
                        }
                     ],
                     "id":1,
                     "status":"Expired",
                     "name":"Equipment Operator"
        };

        expect(employee.getRoleByName('Equipment Operator')).toEqual(selected_role);
    });

    it('should return only the selected project', function() {
        var selected_project = {
                     "id":1,
                     "skills":[
                        {
                           "id":14,
                           "projects":[

                           ],
                           "status":"Expired",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Expired"
                        },
                        {
                           "id":21,
                           "projects":[

                           ],
                           "status":"Complete",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Complete"
                        },
                        {
                           "id":34,
                           "projects":[

                           ],
                           "status":"Expiring",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Expiring"
                        },
                        {
                           "id":34,
                           "projects":[

                           ],
                           "status":"Pending",
                           "roles":[
                              1
                           ],
                           "name":"Site Orientation Pending"
                        }
                     ],
                     "name":"Phase 3 Hermanos Building Construction Site",
                     "status":"Expiring",
                     "roles":[
                        {
                           "skills":[
                              {
                                 "id":21,
                                 "projects":[

                                 ],
                                 "status":"Complete",
                                 "roles":[
                                    1
                                 ],
                                 "name":"Defensing Driving"
                              }
                           ],
                           "id":1,
                           "status":"Expired",
                           "name":"Soccer Mom"
                        },
                        {
                           "skills":[
                              {
                                 "id":22,
                                 "projects":[

                                 ],
                                 "status":"Expiring",
                                 "roles":[
                                    1
                                 ],
                                 "name":"Getaway Driving"
                              }
                           ],
                           "id":1,
                           "status":"Expired",
                           "name":"Driver"
                        }
                     ]
        };

        expect(employee.getProjectByName('Phase 3 Hermanos Building Construction Site')).toEqual(selected_project);
    });
});



    // describe('filtering roles', function() {
    //     beforeEach(function () {
    //         scope.highlightedStatus = "Complete";
    //         scope.changeSubView('role', 'Offensive Driving');
    //     });

    //     it('should return only the selected role', function() {
    //         scope.getRoleByName('Offensive Driving');

    //         // var selected_role = {
    //         //     id:24,
    //         //     status:"Expiring",
    //         //     name:"Offensive Driving"
    //         // };

    //         // expect(scope.currentRole).toEqual(selected_role);
    //     });

    //     // it('should set the visible status icon to the role status', function() {
    //     //     var selected_role = {
    //     //         id:24,
    //     //         status:"Expiring",
    //     //         name:"Destruction"
    //     //     };
    //     //     scope.getRoleByName(selected_role);


    //     //     expect(scope.highlightedStatus).toEqual(selected_role.status);
    //     // });
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