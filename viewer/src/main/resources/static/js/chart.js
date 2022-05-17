function makeChart(channelData) {
    let root = am5.Root.new("chartdiv");
    root.utc = true;

    root.setThemes([am5themes_Animated.new(root)]);

    let chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                panX: true,
                panY: true,
                wheelX: "panX",
                wheelY: "zoomX",
                pinchZoomX: true
            })
    );

    let dateAxis = chart.xAxes.push(
            am5xy.DateAxis.new(root, {
                maxDeviation: 10000000,
                baseInterval: {timeUnit: "minute", count: 15},
                groupData: true,
                autoGapCount: 10,
                renderer: am5xy.AxisRendererX.new(root, {
                    minGridDistance: 50
                }),
                tooltip: am5.Tooltip.new(root, {})
            })
    );
    let valueAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {}),
            })
    );

    let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
        showTooltipOn: "always"
    }))
    cursor.lineX.set("forceHidden", true);
    cursor.lineY.set("forceHidden", true);

    chart.set("scrollbarX", am5.Scrollbar.new(root, {
        orientation: "horizontal",
    }));
    chart.set("scrollbarY", am5.Scrollbar.new(root, {
        orientation: "vertical",
    }));

    dateAxis.set("tooltip", am5.Tooltip.new(root, {
        themeTags: ["axis"]
    }));

    valueAxis.set("tooltip", am5.Tooltip.new(root, {
        themeTags: ["axis"]
    }));

    let legend = chart.bottomAxesContainer.children.push(am5.Legend.new(root, {
        height: 150,
        paddingTop: 15,
        width: am5.percent(100),
        verticalScrollbar: am5.Scrollbar.new(root, {
            orientation: "vertical"
        })
    }));

////////////////////////////////////////////////// SERIES

    let allSeries = {};

    function createSeries(id, name) {
        if (allSeries.hasOwnProperty(id)) {
            return allSeries[id];
        }

        let series = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: name,
                    xAxis: dateAxis,
                    yAxis: valueAxis,
                    valueYField: "balance",
                    valueXField: "date",
                    connect: false,
                    minBulletDistance: 20,
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: "[bold]{name}[/]\n{valueX.formatDate()}: {valueY} - {reason}"
                    })
                })
        );
        allSeries[id] = series;

        series.bullets.push(function () {
            return am5.Bullet.new(root, {
                sprite: am5.Circle.new(root, {
                    radius: 5,
                    fill: series.get("fill")
                })
            });
        });

        series.strokes.template.set("strokeWidth", 2);

        legend.data.setAll(chart.series.values);
        return series;
    }

    for (const channelIndex in channelData) {
        const channel = channelData[channelIndex];
        createSeries(channel['id'], channel['username']);
    }

////////////////////////////////////////////////// DATA LOADING

    let currentDate = new Date();

    let min = currentDate.getTime() - am5.time.getDuration("day", 1);
    let max = currentDate.getTime();

    let seriesFirst = {};
    let seriesLast = {};

    function loadChannelData(id, name, data, min, max, side) {
        if (data.length <= 0) {
            return;
        }

        let series = createSeries(id, name);

        let processor = am5.DataProcessor.new(root, {
            dateFields: ["date"],
            dateFormat: "i", //ISO 8601
        });
        processor.processMany(data);

        let start = dateAxis.get("start");
        let end = dateAxis.get("end");

        if (side === "none") {
            dateAxis.set("min", min);
            dateAxis.set("max", max);
            dateAxis.setPrivate("min", min);
            dateAxis.setPrivate("max", max);

            series.data.pushAll(data);
            series.data.pushAll(data);

            dateAxis.zoom(0, 1, 0);
        } else if (side === "left") {
            // save dates of first items so that duplicates would not be added
            if (series.data.length > 0) {
                seriesFirst[series.uid] = series.data.getIndex(0).date;
            }

            for (let i = data.length - 1; i >= 0; i--) {
                let date = data[i].date;

                // only add if first items date is bigger then newly added items date
                if (!seriesFirst.hasOwnProperty(series.uid) || seriesFirst[series.uid] > date) {
                    series.data.unshift(data[i]);
                }
            }

            // update axis min
            dateAxis.set("min", min);
            dateAxis.setPrivate("min", min);
            // recalculate start and end so that the selection would remain
            dateAxis.set("start", 0);
            dateAxis.set("end", (end - start) / (1 - start));
        } else if (side === "right") {
            // save dates of last items so that duplicates would not be added
            if (series.data.length > 0) {
                seriesLast[series.uid] = series.data.getIndex(series.data.length - 1).date;
            }

            for (let i = 0; i < data.length; i++) {
                let date = data[i].date;

                // only add if last items date is smaller than newly added items date
                if (!seriesLast.hasOwnProperty(series.uid) || seriesLast[series.uid] < date) {
                    series.data.push(data[i]);
                }
            }

            // update axis max
            dateAxis.set("max", max);
            dateAxis.setPrivate("max", max);
            // recalculate start and end so that the selection would remain
            dateAxis.set("start", start / end);
            dateAxis.set("end", 1);
        }
    }

    function loadData(min, max, side) {
        min = min.toFixed(0);
        max = max.toFixed(0);

        let url = "/api/balance/all/range?start=" + min + "&end=" + max;

        am5.net.load(url).then(function (result) {
            let data = am5.JSONParser.parse(result.response, {
                delimiter: ",",
                reverse: false,
                skipEmpty: true,
                useColumnNames: true
            });

            for (const [key, value] of Object.entries(data)) {
                loadChannelData(key, value['username'], value['balance'], min, max, side)
            }
        });
    }

    function loadSomeData() {
        let start = dateAxis.get("start");
        let end = dateAxis.get("end");

        let selectionMin = dateAxis.getPrivate("selectionMin");
        let selectionMax = dateAxis.getPrivate("selectionMax");

        let min = dateAxis.getPrivate("min");
        let max = dateAxis.getPrivate("max");

        // if start is less than 0, means we are panning to the right, need to load data to the left (earlier days)
        if (start < 0) {
            loadData(selectionMin, min, "left");
        }
        // if end is bigger than 1, means we are panning to the left, need to load data to the right (later days)
        if (end > 1) {
            loadData(max, selectionMax, "right");
        }
    }

    chart.events.on("panended", function () {
        loadSomeData();
    });

    var wheelTimeout;
    chart.events.on("wheelended", function () {
        // load data with some delay when wheel ends, as this is event is fired a lot
        // if we already set timeout for loading, dispose it
        if (wheelTimeout) {
            wheelTimeout.dispose();
        }

        wheelTimeout = chart.setTimeout(function () {
            loadSomeData();
        }, 50)
    });


////////////////////////////////////////////////// INITIAL LOADING

    loadData(min, max, "none");
}

// fetch('/api/channel')
//         .then(response => response.json())
//         .then(data => makeChart(data));

makeChart([]);
