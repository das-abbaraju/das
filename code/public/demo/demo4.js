var app = angular.module('demo4', []);

app.controller('Demo4Ctrl', function($scope) {
});

app.directive('accordion', function() {
  return {
    restrict: 'E',
    transclude: true,
    templateUrl: 'demo4Accordion.html',
    scope: {},

    controller: function($scope, $element, $attrs) {
      self = this;
      this.children = []

      this.registerItem = function(element, hide) {
        var id = self.children.length;
        self.children.push({
          id:      id,
          element: element,
          hide:    hide,
        });

        return id;
      };

      this.itemShown = function(id) {
console.log("Click on " + id);
        for (var i = 0; i < self.children.length; i++) {
          var child = self.children[i];
          if (i === id) {
console.log("marking active: " + i);
            child.element.addClass('active');
          }
          else {
console.log("hiding: " + i);
            child.element.removeClass('active');
            child.hide();
          }
        }
      }
    }
  }
});

app.directive('accordionItem', function() {
  return {
    restrict: 'E',
    require: '^accordion',
    transclude: true,
    scope: {
      label: "@"
    },
    templateUrl: 'demo4AccordionItem.html',

    link: function($scope, $element, attrs, controller) {
      $scope.shown = false;
      $scope.id = null;
      $scope.show = function() {
        $scope.shown = true;
        controller.itemShown($scope.id);
      }
      $scope.id = controller.registerItem($element.find("li > a"), function() {
        $scope.shown = false;
      });
    }
  }
});


