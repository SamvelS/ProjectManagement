var selectedPageSize = 20;

$(function() {
    $( "#project-statuses" ).change(async function() {
        await loadProjectsForSelectedStatus();
        await loadTasks();
    });

    $("#projects").change(async function() {
        await loadTasks();
    });

    $("#users").change(async function() {
        loadTasks();
    });

    $('#plannedStartDateCreate').datepicker();
    $('#plannedEndDateCreate').datepicker();

    $('#create-task').click(async function () {
        if($(this).hasClass('disabled')) {
            return;
        }

        clearCreateTaskPopup();
        await loadAllTasks();
        $('#saving-create-task').hide();
        $('#create-task-modal').modal('show');
    });

    $('#save-created-task').click(async function (event) {
        event.preventDefault();
        await createTask();
    });

    loadTasks();
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

async function loadTasks() {
    loadTasksData().then(() => initializeTasksDataGrid()).then(async () => await loadTasksCount()).catch(err => console.log(err));
}

async function loadAllTasks() {
    const projectId = (typeof $('#projects option:selected').val() === "undefined" ? -1 : $('#projects option:selected').val());
    const tasks = (await axios.get('/tasks/allData?projectId=' + projectId + '&userId=' + $('#users option:selected').val())).data.map(({ id, name }) => ({
        id,
        name
    }));
    const optionsAsString = tasks.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '<option value="">none</option>');
    $('#parentTaskCreate').html(optionsAsString);
}

async function loadTasksData(from = 1, count = 20) {
    const tasksDatagrid = $('#tasks-datagrid');

    const projectId = (typeof $('#projects option:selected').val() === "undefined" ? -1 : $('#projects option:selected').val());
    const tasks = (await axios.get('/tasks/data?projectId=' + projectId + '&userId=' + $('#users option:selected').val() +
        '&from=' + from + '&count=' + count)).data.map(({ id, name, description, parentTask }) => ({
        id:`
                <div class="checkbox-for-grid">
                    <input type="checkbox" class="select-task-check" value="${id}">
                </div>`,
        name,
        description,
        parentTask: parentTask.id === null ? '' : `<a href="/tasks/${parentTask.id}">${parentTask.name}</a>`,
        details: `<a href="/tasks/${id}">details</a>`
    }));

    tasksDatagrid.datagrid({ data: (tasks) });
}

function initializeTasksDataGrid() {
    const tasksDataGrid = $('#tasks-datagrid');
    tasksDataGrid.datagrid({singleSelect:true});
    tasksDataGrid.datagrid({pageList:[20,30,40,50]});
    tasksDataGrid.datagrid({pageSize:selectedPageSize});
    tasksDataGrid.datagrid('getPager').pagination({
        layout:['list','sep','first','prev','sep','links','sep','next','last','info']
    });

    tasksDataGrid.datagrid('getPager').pagination({
        onSelectPage: (pageNumber, pageSize) => loadTasksDataForPage(pageNumber, pageSize)
    });

    tasksDataGrid.datagrid('getPager').pagination({
        onChangePageSize: (pageSize) => { selectedPageSize = pageSize }
    });
}

async function loadTasksDataForPage(pageNumber, pageSize) {
    await loadTasksData((pageNumber - 1) * pageSize + 1, pageSize)
        .then(async () => await loadTasksCount()).catch(err => console.log(err));

    const pager = $('#tasks-datagrid').datagrid('getPager');
    pager.pagination({
        layout:['list','sep','first','prev','sep','links','sep','next','last','info']});
    pager.pagination({
        onSelectPage: (pageNumber, pageSize) => loadTasksDataForPage(pageNumber, pageSize)
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

async function loadTasksCount() {
    const projectId = (typeof $('#projects option:selected').val() === "undefined" ? -1 : $('#projects option:selected').val());
    const totalTasksCount = await axios.get('/tasks/count?projectId=' + projectId + '&userId=' + $('#users option:selected').val());

    $('#tasks-datagrid').datagrid('getPager').pagination({
        total: totalTasksCount.data
    });
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
        parentTask: { id: (typeof $('#parentTaskCreate option:selected').val() === "" ? null : $('#parentTaskCreate option:selected').val())}
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