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