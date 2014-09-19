angular.module('PICS.companyFinder')

    .controller('companyFinderCtrl', function ($scope, $timeout, $q, locationService, companyFinderService, tradeService, throttle, mapMarkerService) {
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
            redMarkerImageUrl: '/angular/src/app/company-finder/img/marker-danger-light.png',
            yellowMarkerImageUrl: '/angular/src/app/company-finder/img/marker-warning-light.png',
            greenMarkerImageUrl: '/angular/src/app/company-finder/img/marker-success-light.png',
            grayMarkerImageUrl: '/angular/src/app/company-finder/img/marker-generic.png',
            maxZoom: 9
        };

        var clusterMapLoader,
            locationSummaries = [];

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
                    addressQuery: place.formatted_address || place.name
                }, setMapBounds);
            });
        });

        $scope.filterEditMode = false;

        $scope.basicSearchMode = true;

        $scope.safetySensitiveEnabled = false;

        $scope.safetySensitive = true;

        $scope.soleOwnerEnabled = false;

        $scope.soleOwner = true;

        $scope.locationCount = 0;

        $scope.googleMapConfig = {
            map: map,
            minZoom: 6,
            maxZoom: 14
        };

        $scope.currentPage = 1;

        $scope.itemsPerPage = 10;

        $scope.clustered = true;

        $scope.loading = true;

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

        $scope.getMarkerClass = function (flagColor) {
            if (flagColor == 'Red') {
                return 'red-marker';
            } else if (flagColor == 'Amber') {
                return 'yellow-marker';
            } else if (flagColor == 'Green') {
                return 'green-marker';
            } else {
                return 'gray-marker';
            }
        };

        $scope.onBasicSearchClick = function ($event) {
            $event.preventDefault();
            $scope.basicSearchMode = true;
        };

         $scope.onAdvancedSearchClick = function ($event) {
            $event.preventDefault();
            $scope.basicSearchMode = false;
        };

        function serverResponseLikelySlow() {
            return map.zoom < 15;
        }

        function loadContractorsForMarkerClusterer() {
            if (serverResponseLikelySlow()) {
                clusterMapLoader.show();
            }

            $scope.loading = true;

            $scope.locationsUnclustered = [];

            if ($scope.markerClustererConfig) {
                $scope.markerClustererConfig.locations = [];
            }

            fetchContractorLocations()

            .then(function (locations) {
                locationSummaries = locations;
                updateLocationsUi();
                $scope.locationCount = locations.length;
                clusterMapLoader.hide();
            }, function () {
                clusterMapLoader.hide();
            });
        }

        function updateLocationsUi() {
            if (map.zoom > markerClustererConfig.maxZoom || locationSummaries.length <= 10) {
                // unclustered
                $scope.locationsUnclustered = [];
                markerClustererConfig.minimumClusterSize = Infinity;
                updateCurrentPage();
                $scope.totalItems = locationSummaries.length;
                $scope.clustered = false;
            } else {
                // clustered
                markerClustererConfig.minimumClusterSize = -Infinity;
                markerClustererConfig.locations = locationSummaries;
                $scope.locationsUnclustered = [];
                $scope.clustered = true;
                $scope.loading = false;
            }

            $scope.markerClustererConfig = markerClustererConfig;
        }

        function updateCurrentPage() {
            var startingPosition = ($scope.currentPage - 1) * $scope.itemsPerPage,
                query_ids = locationSummaries.slice(startingPosition, startingPosition + $scope.itemsPerPage);

            if (query_ids.length !== 0) {
                $scope.loading = true;

                companyFinderService.query({ids: getLocationIds(query_ids)}, function (locations) {
                    markerClustererConfig.locations = locations;
                    $scope.locationsUnclustered = locations;
                    $scope.loading = false;
                });
            } else {
                $scope.loading = false;
            }
        }

        function getLocationIds(arr) {
            return arr.map(function(item) { return item.id; }).join(',');
        }

        $scope.pageChanged = function () {
            updateCurrentPage();
        };

        $scope.onResultClick = function ($event) {
            var markers = mapMarkerService.getMarkers();

            google.maps.event.trigger(markers[$event.currentTarget.id], 'click');
        };

        $scope.onResultMouseOver = function ($event) {
            var markers = mapMarkerService.getMarkers();

            google.maps.event.trigger(markers[$event.currentTarget.id], 'mouseover');
        };

        $scope.onResultMouseOut = function ($event) {
            var markers = mapMarkerService.getMarkers();

            google.maps.event.trigger(markers[$event.currentTarget.id], 'mouseout');
        };

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

            return selectedTrades.join(',');
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
                    swLong: sw.lng(),
                    safetySensitive: $scope.safetySensitiveEnabled ? ($scope.safetySensitive ? 1 : 0) : -1,
                    soleOwner: $scope.soleOwnerEnabled ? ($scope.soleOwner ? 1 : 0) : -1,
                    summary: true
                };

            if (!trade) {
                return requestParameters;
            } else {
                return angular.extend(requestParameters, {
                    tradeIds: trade
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
