$(function() {
    $( "#project-statuses" ).change(function() {
        loadProjectsForSelectedStatus();
    });

    $('#plannedStartDateCreate').datepicker();
    $('#plannedEndDateCreate').datepicker();

    $('#create-task').click(function () {
        if($(this).hasClass('disabled')) {
            return;
        }

        clearCreateTaskPopup();
        $('#saving-create-task').hide();
        $('#create-task-modal').modal('show');
    });

    $('#save-created-task').click(async function (event) {
        event.preventDefault();
        await createTask();
    });
});

async function loadProjectsForSelectedStatus() {
    const projects = (await axios.get('/projects/byStatus/' + $('#project-statuses option:selected').val())).data;

    const projectsAsString = projects.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '');
    $('#projects').html(projectsAsString);

    if(projects.length == 0) {
        $('#create-task').addClass('disabled');
    }
    else {
        $('#create-task').removeClass('disabled');
    }
}

function clearCreateTaskPopup() {
    $('#nameCreate').val('');
    $('#descriptionCreate').val('');
    $('#plannedStartDateCreate').val('');
    $('#plannedEndDateCreate').val('');
    $('span.validation-error').remove();
}

async function createTask() {
    $('#saving-create-task').show();
    $('span.validation-error').remove();

    const data = {
        name: $('#nameCreate').val(),
        description: $('#descriptionCreate').val(),
        plannedStartDate: $('#plannedStartDateCreate').val(),
        plannedEndDate: $('#plannedEndDateCreate').val(),
        projectId: $('#projects option:selected').val(),
        parentTaskId: $('#parentTaskCreate option:selected').val()
    };

    try {
        const response = await axios({
            method: 'post',
            url: '/tasks/add',
            data
        });

        $('#create-task-modal').modal('hide');
        clearCreateTaskPopup();
        location.reload();
    }
    catch (error) {
        if(error.response) {
            if(error.response.status == 400) {

                error.response.data.forEach((item, index) => {
                    if($("#" + item.fst + "Create").length > 0) {
                        $("<span class='validation-error'>" + item.snd + "</span>").insertBefore($("#" + item.fst + "Create"));
                    }
                });
            }
        }
        else {
            console.log(error);
        }
    }

    $('#saving-create-task').hide();
}