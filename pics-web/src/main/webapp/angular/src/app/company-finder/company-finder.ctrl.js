angular.module('PICS.companyFinder')

    .controller('companyFinderCtrl', function ($scope, $timeout, $q, locationService, companyFinderService, tradeService, throttle) {
        var map = {};

        var markerClustererConfig = {
            centerCoordinates: { lat: 0, lng: 0 },
            locations: [],
            minimumClusterSize: 1,
            styles: [{
                "url": "/angular/src/app/company-finder/img/cluster-small.png",
                "width": 25,
                "height": 25,
                "textColor": "#ffffff",
                "textSize": 12,
                "fontWeight": 'normal'
            }, {
                "url": "/angular/src/app/company-finder/img/cluster-medium.png",
                "width": 30,
                "height": 30,
                "textColor": "#ffffff",
                "textSize": 12,
                "fontWeight": 'normal'
            }, {
                "url": "/angular/src/app/company-finder/img/cluster-large.png",
                "width": 40,
                "height": 40,
                "textColor": "#ffffff",
                "textSize": 12,
                "fontWeight": 'normal'
            }],
            markerImageUrl: '/angular/src/app/company-finder/img/marker.png'
        };

        var clusterMapLoader;

        // Override ClusterIcon.onAdd to limit icon text to 1-100+
        // TODO: Restore this function when the route changes
        ClusterIcon.prototype.onAdd = function() {
            this.div_ = document.createElement('DIV');
            if (this.visible_) {
                var pos = this.getPosFromLatLng_(this.center_);
                this.div_.style.cssText = this.createCss(pos);
                this.div_.innerHTML = this.sums_.text > 100 ? '100+' : this.sums_.text;
            }

            var panes = this.getPanes();
            panes.overlayMouseTarget.appendChild(this.div_);

            var that = this;
            google.maps.event.addDomListener(this.div_, 'click', function() {
                that.triggerClusterClick();
            });
        };

        function setMapBounds(location) {
            if (!map.setCenter) return;

            // hack to handle garbage place selections
            if (!location || !location.coordinates || !location.coordinates.latitude) {
                if (typeof clusterMapLoader != 'undefined') clusterMapLoader.hide();
                return;
            }

            var centerLat = location.coordinates.latitude,
                centerLng = location.coordinates.longitude,
                viewport = location.viewPort,
                viewportSwLat = viewport.southWest.latitude,
                viewportSwLng = viewport.southWest.longitude,
                viewportNeLat = viewport.northEast.latitude,
                viewportNeLng = viewport.northEast.longitude,
                sw = new google.maps.LatLng(viewportSwLat, viewportSwLng),
                ne = new google.maps.LatLng(viewportNeLat, viewportNeLng),
                bounds = new google.maps.LatLngBounds(),
                latLngBounds = new google.maps.LatLngBounds(sw, ne);

            bounds.union(latLngBounds);

            map.setCenter(new google.maps.LatLng(centerLat, centerLng));

            map.fitBounds(bounds);
        }

        function fetchContractorLocations() {
            var deferred = $q.defer(),
                parameters = getRequestParameters();

            companyFinderService.query(parameters, function (locations) {
                deferred.resolve(locations);
            });

            return deferred.promise;
        }

        $scope.tradeSelect2El = {};

        $scope.autocomplete = {};

        $scope.$watch('autocomplete', function () {
            google.maps.event.addListener($scope.autocomplete, 'place_changed', function() {
                var place = $scope.autocomplete.getPlace();

                clusterMapLoader.show();

                locationService.get({
                    addressQuery: place.name
                }, setMapBounds);
            });
        });

        $scope.filterEditMode = false;
        $scope.googleMapConfig = {
            map: map,
            minZoom: 8
        };

        $scope.$on('loader-ready', function (event, loader, markerClustererConfig) {
            if (markerClustererConfig != $scope.markerClustererConfig) return;

            clusterMapLoader = loader;
        });

        $scope.$on('map-created', function (event, googleMapConfig) {
            if (googleMapConfig != $scope.googleMapConfig) return;

            clusterMapLoader.show();

            // TODO: Resolve this before the user is routed
            locationService.get(function (userLocation) {
                map = googleMapConfig.map;

                $scope.searchQuery = userLocation.address;

                setMapBounds(userLocation);

                google.maps.event.addListener(map, 'bounds_changed', throttle(500, loadContractorsForMarkerClusterer));
            });
        });

        function serverResponseLikelySlow() {
            return map.zoom < 10;
        }

        function loadContractorsForMarkerClusterer() {
            if (serverResponseLikelySlow()) {
                clusterMapLoader.show();
            }

            fetchContractorLocations()

            .then(function (locations) {
                updateLocationsUi(locations);
                clusterMapLoader.hide();
            }, function () {
                clusterMapLoader.hide();                
            });
        }

        function updateLocationsUi(locations) {
            markerClustererConfig.locations = locations;

            if (locations.length < 10) {
                markerClustererConfig.minimumClusterSize = Infinity;
                $scope.locationsUnclustered = locations;
            } else {
                markerClustererConfig.minimumClusterSize = -Infinity;
                $scope.locationsUnclustered = [];
            }

            $scope.markerClustererConfig = markerClustererConfig;
        }

        $scope.onEditClick = function () {
            $scope.filterEditMode = true;
            currentSelectedTrades = $scope.tradeSelect2El.select2('data');
        };

        $scope.onUpdateClick = function () {

            currentSelectedTrades = $scope.tradeSelect2El.select2('data');

            $scope.filterEditMode = false;

            if ($scope.tradeSelect2El.select2('data').length > 0) {                
                $scope.selectedTrade = getTradeSelect2PropCsv('name');
            } else {
                $scope.selectedTrade = null;
            }

            loadContractorsForMarkerClusterer();
        };

        function getTradeSelect2PropCsv(prop) {
            var selectedTrades = [];

            angular.forEach($scope.tradeSelect2El.select2('data'), function (trade, index) {
                selectedTrades.push(trade[prop]);
            });

            return selectedTrades.join(', ');
        }

        $scope.onCancelClick = function () {
            $scope.filterEditMode = false;
            $scope.tradeSelect2El.select2('data', currentSelectedTrades);
        };

        $scope.trades = [];

        function getRequestParameters() {
            var bounds = map.getBounds(),
                ne = bounds.getNorthEast(),
                sw = bounds.getSouthWest(),
                trade = getTradeSelect2PropCsv('id'),
                requestParameters = {
                    neLat: ne.lat(),
                    neLong: ne.lng(),
                    swLat: sw.lat(),
                    swLong: sw.lng()
                };

            if (!trade) {
                return requestParameters;
            } else {
                return angular.extend(requestParameters, {
                    trade: trade
                });
            }
        }

        $scope.onTradeKeyup = function (value, query) {
            tradeService.get({query: value }, function (data) {
                query.callback({
                    results: data.result
                });
            });
        };
    });
