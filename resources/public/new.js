function addCandidate(event) {
    var candidate = $('#candidate').val().trim()
    if (candidate) {
        $('#candidateList').append('<a href="" class="list-group-item"><span class="candidate">' + candidate + '</span><span class="label label-danger pull-right hidden">Remove</span></li>')
        $('#candidate').val('')
    }
    event.preventDefault()
}

function clearCandidates() {
    $('#candidateList').empty()
    $('#candidateResults').empty()
}

function openPolls() {
    $('#candidateList .candidate').each(function() {
        $('#candidateResults').append('<input type="hidden" name="candidates" value="' + $(this).text() + '">')
    })
    $('#vsysResults').append('<input type="hidden" name="vsys" value="' + $('#vsys').val() + '">')
}

$(document).on('click', '#candidateList a', function(event) {
    $(this).remove()
    event.preventDefault()
})

$(document).on('mouseover', '#candidateList a', function() {
    $(this).find('span.label').removeClass('hidden')
})

$(document).on('mouseleave', '#candidateList a', function() {
    $(this).find('span.label').addClass('hidden')
})
