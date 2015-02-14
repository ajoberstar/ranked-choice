function addCandidate() {
    var candidate = $('#candidate').val().trim()
    if (candidate) {
        $('#candidates').append('<a href="#" class="list-group-item">' + candidate + '<span class="label label-danger pull-right hidden">Remove</span></li>')
        $('#candidate').val('')
    }
}

function clearCandidates() {
    $('#candidates').empty()
}

$(document).on('click', '#candidates a', function() {
    $(this).remove()
})

$(document).on('mouseover', '#candidates a', function() {
    $(this).find('span.label').removeClass('hidden')
})

$(document).on('mouseleave', '#candidates a', function() {
    $(this).find('span.label').addClass('hidden')
})
