// Load the Visualization API.
google.load('visualization', '1.0', {
    'packages' : [ 'corechart' ]
});

// Set a callback to run when the Google Visualization API is loaded.
google.setOnLoadCallback(loadData);

function loadData() {
    $.get("/1/chart/network-usage", drawChart).error(dataLoadError);
}

function dataLoadError() {
    $("#network-usage-spinner").empty();
    $("#network-usage-spinner").append("Données non disponibles pour le moment");
}

function drawChart(jsonData) {
    users = jsonData["users"];
    $("#users").append(users);

    days = jsonData["days"];
    $("#days").append(days);

    onOrange = jsonData["orange"];
    onFreeMobile = jsonData["freeMobile"];

    var data = new google.visualization.DataTable();
    data.addColumn("string", "Réseau");
    data.addColumn("number", "Utilisation");

    data.addRows(2);
    data.setCell(0, 0, "Orange");
    data.setCell(0, 1, onOrange, "");
    data.setCell(1, 0, "Free Mobile");
    data.setCell(1, 1, onFreeMobile, "");

    var options = {
        "width" : 800,
        "height" : 350,
        "colors" : [ "#FF6600", "#CD1E25" ],
        "chartArea" : {
            left : 300,
            top : 15,
            width : "100%",
            height : "325",
        },
    };

    var chart = new google.visualization.PieChart(document
            .getElementById("network-usage-chart"));
    chart.draw(data, options);

    $("#network-usage-spinner").remove();
    $("#network-usage-chart").fadeIn(function() {
        $("#chart-help").slideDown();
    });
}
