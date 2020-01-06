var selectedPageSize = 20;

$(function() {
    $( "#project-statuses" ).change(async function() {
        await loadProjectsForSelectedStatus();
        await loadTasks();
        updateButtonStates();
    });

    $("#projects").change(async function() {
        await loadTasks();
    });

    $("#users").change(async function() {
        await loadTasks();
    });

    $('#plannedStartDateCreate').datepicker();
    $('#plannedEndDateCreate').datepicker();
    $('#plannedStartDateEdit').datepicker();
    $('#plannedEndDateEdit').datepicker();
    $('#actualStartDateEdit').datepicker();
    $('#actualEndDateEdit').datepicker();

    $('#users-edit option[value="-1"]').remove();
    $('#users-create option[value="-1"]').remove();

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

    $('#tasks-grid-area').on('change', '.select-task-check', function () {
        updateButtonStates();
    });

    $('#edit-task').click(async function () {
        if ($(this).hasClass('disabled')) {
            return;
        }

        $('#users-edit option:selected').prop("selected", false);

        $('#saving-edit-task').hide();
        $('#edit-task-modal').modal('show');

        await loadEditingTaskData($('.select-task-check:checked').val());
        updateUserForAssignment();
        $('.remove-user').click(function (event) {
            event.preventDefault();
            removeUserFromEdit($(this).val());
        });
    });

    $('#save-edited-task').click(async function (event) {
        event.preventDefault();
        await editTask();
    });

    $('.add-assignment-user').click(function (event) {
        event.preventDefault();
        addUserForAssignment();
    });

    updateUserForAssignment();
    loadTasks();
});

function updateButtonStates(){
    const selectedTasksCount = $('.select-task-check:checked').length;

    if (selectedTasksCount == 1) {
        $('#edit-task').removeClass('disabled');
        $('#delete-task').removeClass('disabled');
    } else {
        $('#edit-task').addClass('disabled');
        $('#delete-task').addClass('disabled');
    }
}

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
    loadTasksData().then(() => initializeTasksDataGrid()).then(async () => await loadTasksCount()).then(() => updateButtonStates()).catch(err => console.log(err));
}

async function loadAllTasks() {
    const projectId = (typeof $('#projects option:selected').val() === "undefined" ? -1 : $('#projects option:selected').val());
    const tasks = (await axios.get('/tasks/allDataInfo?projectId=' + projectId + '&userId=' + $('#users option:selected').val())).data.map(({ id, name }) => ({
        id,
        name
    }));
    const optionsAsString = tasks.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '<option value="">none</option>');
    $('#parentTaskCreate').html(optionsAsString);
    $('#parentTaskEdit').html(optionsAsString);
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
    $('#users-create option:selected').prop("selected", false);
}

function clearEditTaskPopup() {
    $('#nameEdit').val('');
    $('#descriptionEdit').val('');
    $('#plannedStartDateEdit').val('');
    $('#plannedEndDateCEdit').val('');
    $('#actualStartDateEdit').val('');
    $('#actualEndDateEdit').val('');
    $('span.validation-error').remove();
}

async function createTask() {
    $('#saving-create-task').show();
    $('span.validation-error').remove();

    var assignees = [];

    $.each($('#users-create option:selected'), function(){
        assignees.push({id:$(this).val()});
    });

    const data = {
        name: $('#nameCreate').val(),
        description: $('#descriptionCreate').val(),
        plannedStartDate: $('#plannedStartDateCreate').val(),
        plannedEndDate: $('#plannedEndDateCreate').val(),
        projectId: $('#projects option:selected').val(),
        parentTask: { id: (typeof $('#parentTaskCreate option:selected').val() === "" ? null : $('#parentTaskCreate option:selected').val())},
        assignees: assignees
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

async function loadEditingTaskData(id) {
    const task = (await axios.get('/tasks/details/' + id)).data;

    $('#editingTaskId').val(task.id);
    $('#nameEdit').val(task.name);
    $('#descriptionEdit').val(task.description);
    $('#plannedStartDateEdit').val(task.plannedStartDate);
    $('#plannedEndDateEdit').val(task.plannedEndDate);
    $('#actualStartDateEdit').val(task.actualStartDate);
    $('#actualEndDateEdit').val(task.actualEndDate);

    const userStatusesAsString = task.assignees.reduce((accumulator, {id, firstName, lastName, email, statusId}) =>
    accumulator + `<li value="${id}">${firstName} ${lastName} [${email}]<button class="btn-primary remove-user float-right" value="${id}">Remove</button><select class="float-right"></select></li>`, '');
    $('#status-by-user').html(userStatusesAsString);

    $('#status-by-user li > select').each(function(){
        $(this).html($('#project-statuses').html());
        $(this).find('option').get(0).remove();
    });

    task.assignees.forEach((item, index) => {
        $('#status-by-user li[value="' + item.id + '"] option').each(function() {
            if($(this).val() == item.statusId) {
                $(this).attr('selected', 'selected');
            }
        });

        $('#users-edit option').each(function () {
            if($(this).val() == item.id) {
                $(this).attr('selected', 'selected');
            }
        });
    });
    const projects = (await axios.get('/projects/byStatus/-1')).data;

    const projectsAsString = projects.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '');
    $('#projects-edit').html(projectsAsString);

    $('#projects-edit option').each(function() {
        if($(this).val() == task.projectId) {
            $(this).attr('selected', 'selected');
        }
    });

    await loadAllTasks();

    $('#parentTaskEdit option').each(function () {
        if($(this).val() == task.parentTask.id) {
            $(this).attr('selected', 'selected');
        }
    });

    $('#loading-edit-task').hide();
}

async function editTask() {
    $('#saving-edit-task').show();
    $('span.validation-error').remove();

    var assignees = [];

    $.each($('#status-by-user li'), function(){
        assignees.push({id:$(this).val(), statusId:$(this).children("select").children("option:selected").val()});
    });

    const data = {
        id: $('#editingTaskId').val(),
        name: $('#nameEdit').val(),
        description: $('#descriptionEdit').val(),
        plannedStartDate: $('#plannedStartDateEdit').val(),
        plannedEndDate: $('#plannedEndDateEdit').val(),
        actualStartDate: $('#actualStartDateEdit').val(),
        actualEndDate: $('#actualEndDateEdit').val(),
        projectId: $('#projects-edit option:selected').val(),
        parentTask: { id: (typeof $('#parentTaskEdit option:selected').val() === "" ? null : $('#parentTaskEdit option:selected').val())},
        assignees: assignees
    };

    try {
        const response = await axios({
            method: 'post',
            url: '/tasks/edit',
            data
        });

        $('#edit-task-modal').modal('hide');
        clearEditTaskPopup();
        location.reload();
    }
    catch (error) {
        if(error.response) {
            if(error.response.status == 400) {

                error.response.data.forEach((item, index) => {
                    if($("#" + item.fst + "Edit").length > 0) {
                        $("<span class='validation-error'>" + item.snd + "</span>").insertBefore($("#" + item.fst + "Edit"));
                    }
                });
            }
        }
        else {
            console.log(error);
        }
    }

    $('#saving-edit-task').hide();
}

function removeUserFromEdit(id) {
    $('#status-by-user li[value="' + id + '"]').remove();
    updateUserForAssignment();
}

function addUserForAssignment() {
    const userId = $('#users-to-add-for-assignment option:selected').val();
    const userDetails = $('#users-to-add-for-assignment option:selected').text();
    $('#status-by-user').append(`<li value="` + userId + `">` + userDetails + `<button class="btn-primary remove-user float-right" value="` + userId + `">Remove</button><select class="float-right"></select></li>`);

    var lastLi = $('#status-by-user li > select').last();
    lastLi.html($('#project-statuses').html());
    lastLi.find('option').get(0).remove();

    var lastBtn = $('#status-by-user li > button').last();
    lastBtn.click(function (event) {
        event.preventDefault();
        removeUserFromEdit($(this).val());
    });
    updateUserForAssignment();
}

function updateUserForAssignment() {
    $('#users-to-add-for-assignment').html($('#users-edit').html());
    $.each($('#status-by-user li'), function() {
        $('#users-to-add-for-assignment option[value="' + $(this).val() +'"]').remove()
    });
}