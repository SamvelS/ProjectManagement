$(function() {
    loadProjectsData();//.then(() => initializeUsersDataGrid()).then(async () => await loadUsersCount()).catch(err => console.log(err));

    $('#plannedStartDateCreate').datepicker();
    $('#plannedEndDateCreate').datepicker();

    $('#create-project').click(function () {
        clearCreateProjectPopup();
        $('#saving-create-project').hide();
        $('#create-project-modal').modal('show');
    });

    $('#save-created-project').click(async function (event) {
        event.preventDefault();
        await createProject();
    });
});

function clearCreateProjectPopup() {
    $('#nameCreate').val('');
    $('#descriptionCreate').val('');
    $('#plannedStartDateCreate').val('');
    $('#plannedEndDateCreate').val('');
    $('span.validation-error').remove();
}

async function createProject() {
    $('#saving-create-project').show();
    $('span.validation-error').remove();

    const data = {
        name: $('#nameCreate').val(),
        description: $('#descriptionCreate').val(),
        plannedStartDate: $('#plannedStartDateCreate').val(),
        plannedEndDate: $('#plannedEndDateCreate').val()
    };

    try {
        const response = await axios({
            method: 'post',
            url: '/projects/add',
            data
        });

        $('#create-project-modal').modal('hide');
        clearCreateProjectPopup();
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

    $('#saving-create-project').hide();
}

async function loadProjectsData(from = 1, count = 20) {
    const projectsDatagrid = $('#projects-datagrid');

    const projects = (await axios.get('/projects/data?from=' + from + '&count=' + count))

    // const projects = (await axios.get('/projects/data?from=' + from + '&count=' + count)).data.map(({ id, name, description }) => ({
    //     id:`
    //             <div class="checkbox-for-grid">
    //                 <input type="checkbox" class="select-project-check" value="${id}">
    //             </div>`,
    //     name,
    //     description
    // }));

    projectsDatagrid.datagrid({ data: (projects) });
}