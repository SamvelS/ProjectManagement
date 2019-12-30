var selectedPageSize = 20;

$(function() {
    loadProjectsData().then(() => initializeProjectsDataGrid()).then(async () => await loadProjectsCount()).catch(err => console.log(err));

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

    $('#projects-grid-area').on('change', '.select-project-check', function () {
        const selectedProjectsCount = $('.select-project-check:checked').length;

        if (selectedProjectsCount == 1) {
            $('#edit-project').removeClass('disabled');
        } else {
            $('#edit-project').addClass('disabled');
        }

        if(selectedProjectsCount) {
            $('#delete-project').removeClass('disabled');
        }
        else {
            $('#delete-project').addClass('disabled');
        }
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

    const projects = (await axios.get('/projects/data?from=' + from + '&count=' + count)).data.map(({ id, name, description, plannedStartDate, plannedEndDate, actualStartDate, actualEndDate, status }) => ({
        id:`
                <div class="checkbox-for-grid">
                    <input type="checkbox" class="select-project-check" value="${id}">
                </div>`,
        name,
        description,
        plannedStartDate,
        plannedEndDate,
        actualStartDate,
        actualEndDate,
        status
    }));

    projectsDatagrid.datagrid({ data: (projects) });
}

function initializeProjectsDataGrid() {
    const projectsDataGrid = $('#projects-datagrid');
    projectsDataGrid.datagrid({singleSelect:true});
    projectsDataGrid.datagrid({pageList:[20,30,40,50]});
    projectsDataGrid.datagrid({pageSize:selectedPageSize});
    projectsDataGrid.datagrid('getPager').pagination({
        layout:['list','sep','first','prev','sep','links','sep','next','last','info']
    });

    projectsDataGrid.datagrid('getPager').pagination({
        onSelectPage: (pageNumber, pageSize) => loadProjectsDataForPage(pageNumber, pageSize)
    });

    projectsDataGrid.datagrid('getPager').pagination({
        onChangePageSize: (pageSize) => { selectedPageSize = pageSize }
    });
}

async function loadProjectsDataForPage(pageNumber, pageSize) {
    await loadProjectsData((pageNumber - 1) * pageSize + 1, pageSize)
        .then(async () => await loadProjectsCount()).catch(err => console.log(err));

    const pager = $('#projects-datagrid').datagrid('getPager');
    pager.pagination({
        layout:['list','sep','first','prev','sep','links','sep','next','last','info']});
    pager.pagination({
        onSelectPage: (pageNumber, pageSize) => loadProjectsDataForPage(pageNumber, pageSize)
    });
    pager.pagination({
        onChangePageSize: (pageSize) => { selectedPageSize = pageSize }
    });
    pager.pagination({
        pageNumber: pageNumber
    });
    pager.pagination({
        pageSize: selectedPageSize
    });
}

async function loadProjectsCount() {
    const totalProjectsCount = await axios.get('/projects/count');

    $('#projects-datagrid').datagrid('getPager').pagination({
        total: totalProjectsCount.data
    });
}