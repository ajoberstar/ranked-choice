$(document).ready(function () {
    $('.sortable').sortable({
        placeholder: '<li class="list-group-item">&nbsp;</li>'
    })
})

function votePrep() {
    $('#candidateList .candidate').each(function() {
        $('#candidateVotes').append('<input type="hidden" name="vote" value="' + $(this).text() + '">')
    })
}

$(document).on('mouseover', '#candidateList li', function() {
    $(this).addClass('list-group-item-info')
})

$(document).on('mouseleave', '#candidateList li', function() {
    $(this).removeClass('list-group-item-info')
})
