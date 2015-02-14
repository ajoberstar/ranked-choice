function addCandidate(event) {
    var candidate = $('#candidate').val().trim()
    if (candidate) {
        $('#candidates').append('<a href="" class="list-group-item"><span class="candidate">' + candidate + '</span><span class="label label-danger pull-right hidden">Remove</span></li>')
        $('#candidate').val('')
    }
    event.preventDefault()
}

function clearCandidates() {
    $('#candidates').empty()
}

function openPolls() {
    var candidates = $('#candidates .candidate').map(function() {
        return $(this).text()
    }).get()
    console.log(candidates)
}

$(document).on('click', '#candidates a', function(event) {
    $(this).remove()
    event.preventDefault()
})

$(document).on('mouseover', '#candidates a', function() {
    $(this).find('span.label').removeClass('hidden')
})

$(document).on('mouseleave', '#candidates a', function() {
    $(this).find('span.label').addClass('hidden')
})
