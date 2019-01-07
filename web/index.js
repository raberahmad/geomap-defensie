//import mapboxgl from 'mapbox-gl';
mapboxgl.accessToken = 'pk.eyJ1IjoicmFiZXJhaG1hZCIsImEiOiJjamx3Ymw3ZW0xMmhnM2tvMXZkODhvYnRtIn0.nZ52rlBNTgOSnHDcaMKjVw';
const map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/dark-v9',
    zoom: 4,
    center: [4.28, 52.07]
});

var users = "https://localhost/geodata/"
map.on('load', function() {
    window.setInterval(function() {
        map.getSource("userlogins").setData(users);
    }, 2000);

    map.addSource("userlogins", {
        type: "geojson",
        data: users,
        cluster: true,
        clusterMaxZoom: 14, // Max zoom to cluster points on
        clusterRadius: 50 // Radius of each cluster when clustering points (defaults to 50)
    });

    map.addLayer({
        id: "clusters",
        type: "circle",
        source: "userlogins",
        filter: ["has", "point_count"],
        paint: {
            // Use step expressions (https://www.mapbox.com/mapbox-gl-js/style-spec/#expressions-step)
            // with three steps to implement three types of circles:
            //   * Blue, 20px circles when point count is less than 100
            //   * Yellow, 30px circles when point count is between 100 and 750
            //   * Pink, 40px circles when point count is greater than or equal to 750
            "circle-color": [
                "step",
                ["get", "point_count"],
                "#51bbd6",
                100,
                "#f1f075",
                750,
                "#f28cb1"
            ],
            "circle-radius": [
                "step",
                ["get", "point_count"],
                20,
                100,
                30,
                750,
                40
            ]
        }
    });

    map.addLayer({
        id: "cluster-count",
        type: "symbol",
        source: "userlogins",
        filter: ["has", "point_count"],
        layout: {
            "text-field": "{point_count_abbreviated}",
            "text-font": ["DIN Offc Pro Medium", "Arial Unicode MS Bold"],
            "text-size": 12
        }
    });

    map.addLayer({
        id: "unclustered-point",
        type: "circle",
        source: "userlogins",
        filter: ["!", ["has", "point_count"]],
        paint: {
            "circle-color": "#11b4da",
            "circle-radius": 4,
            "circle-stroke-width": 1,
            "circle-stroke-color": "#fff"
        }
    });

    // inspect a cluster on click
    map.on('click', 'clusters', function (e) {
        var features = map.queryRenderedFeatures(e.point, { layers: ['clusters'] });
        var clusterId = features[0].properties.cluster_id;
        map.getSource('userlogins').getClusterExpansionZoom(clusterId, function (err, zoom) {
            if (err)
                return;

            map.easeTo({
                center: features[0].geometry.coordinates,
                zoom: zoom
            });
        });
    });

    map.on('mouseenter', 'clusters', function () {
        map.getCanvas().style.cursor = 'pointer';
    });
    map.on('mouseleave', 'clusters', function () {
        map.getCanvas().style.cursor = '';
    });


    readTextFile(users, function(text){
        var data = JSON.parse(text);
        console.log(data);
        document.getElementById("amountusers").innerHTML = "Users: "+data.features.length;
    });

    map.on('click', 'unclustered-point', function (e) {
        var coordinates = e.features[0].geometry.coordinates.slice();
        var userid = e.features[0].properties.userID.slice();
        var naam = e.features[0].properties.Naam.slice();
        var ip = e.features[0].properties.IP.slice();
        var date = e.features[0].properties.Date.slice();
        var app = e.features[0].properties.Applicatie.slice();
        // Ensure that if the map is zoomed out such that multiple
        // copies of the feature are visible, the popup appears
        // over the copy being pointed to.
        while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
            coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
        }

        new mapboxgl.Popup()
            .setLngLat(coordinates)
            .setHTML("<strong> User: "+userid+"<br>Naam: "+naam+"<br>Ip: "+ip+"<br>Date: "+date+"<br>App: "+app+"</strong><p>"+coordinates+"</p>")
            .addTo(map);
    });



    // Change the cursor to a pointer when the mouse is over the places layer.
    map.on('mouseenter', 'unclustered-point', function () {
        map.getCanvas().style.cursor = 'pointer';
    });

    // Change it back to a pointer when it leaves.
    map.on('mouseleave', 'unclustered-point', function () {
        map.getCanvas().style.cursor = '';
    });
    
   
});

function readTextFile(file, callback) {
    var rawFile = new XMLHttpRequest();
    rawFile.overrideMimeType("application/json");
    rawFile.open("GET", file, true);
    rawFile.onreadystatechange = function() {
        if (rawFile.readyState === 4 && rawFile.status == "200") {
            callback(rawFile.responseText);
        }
    }
    rawFile.send(null);
}





