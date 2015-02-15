var socket = new WebSocket("ws://" + location.host + location.pathname + "?socket=true")

socket.onmessage = function(event) {
    console.log("Received: " + event.data)
    var data = JSON.parse(event.data)

    var thead = $('#results thead')
    $(thead).empty()
    if (data.length > 0) {
        var cells = data[0].map(function (ignore, index) {
            return '<th>Round ' + index + '</th>'
        })
        cells.shift()
        $(thead).append('<tr><th>#</th><th>Candidate</th>' + cells + '</tr>')
    }

    var tbody = $('#results tbody')
    $(tbody).empty()
    data.forEach(function (result, index) {
        var cells = result.map(function(cell) {
            return '<td>' + cell + '</td>'
        }).join('')
        var tclass = index == 0 ? "success" : ""
        $(tbody).append('<tr class="' + tclass + '"><td>' + (index + 1) + '</td>' + cells + '</tr>')
    })
}
