$(function() {
    loadUsersData().then(() => initializeUsersDataGrid()).then(async () => await loadUsersCount()).catch(err => console.log(err));
    loadAndInitializeRoles();

    $('#create-user').click(function () {
        clearCreateUserPopup();
        $('#saving-create-user').hide();
        $('#loading-edit-user').show();
        $('#create-user-modal').modal('show');
    });

    $('#save-created-user').click(async function (event) {
        event.preventDefault();
        await createUser();
    });

    $('#save-edited-user').click(async function (event) {
        event.preventDefault();
        await editUser();
    });

    $('#users-grid-area').on('change', '.select-user-check', function () {
        const selectedUsersCount = $('.select-user-check:checked').length;

        if (selectedUsersCount == 1) {
            $('#edit-user').removeClass('disabled');
        } else {
            $('#edit-user').addClass('disabled');
        }

        if(selectedUsersCount) {
            $('#delete-user').removeClass('disabled');
        }
        else {
            $('#delete-user').addClass('disabled');
        }
    });

    $('#edit-user').click(async function () {
        if ($(this).hasClass('disabled')) {
            return;
        }

        $('#saving-edit-user').hide();
        $('#edit-user-modal').modal('show');
        loadEditingUserData($('.select-user-check:checked').val());
    });

    $('#delete-user').click(async function () {
        if($(this).hasClass('disabled')) {
            return;
        }

        deleteSelectedUser();
    });
});

function clearCreateUserPopup() {
    $('#firstNameCreate').val('');
    $('#lastNameCreate').val('');
    $('#emailCreate').val('');
    $('#passwordCreate').val('');
    $('span.validation-error').remove();
}

function clearEditUserPopup() {
    $('#firstNameEdit').val('');
    $('#lastNameEdit').val('');
    $('#emailEdit').val('');
    $('span.validation-error').remove();
}

function initializeUsersDataGrid() {
    const usersDatagrid = $('#users-datagrid');
    usersDatagrid.datagrid({singleSelect:true});
    usersDatagrid.datagrid({pageList:[20,30,40,50]});
    usersDatagrid.datagrid({pageSize:20});
    usersDatagrid.datagrid('getPager').pagination({
        layout:['list','sep','first','prev','sep','links','sep','next','last','info']
    });

    usersDatagrid.datagrid('getPager').pagination({
        onSelectPage: (pageNumber, pageSize) => loadUsersDataForPage(pageNumber, pageSize)
    });
}

async function loadUsersDataForPage(pageNumber, pageSize) {
    console.log('pageNumber: ' + pageNumber + ' pageSize: ' + pageSize);
    await loadUsersData((pageNumber - 1) * pageSize + 1, pageSize)
        .then(async () => await loadUsersCount()).catch(err => console.log(err));

    const pager = $('#users-datagrid').datagrid('getPager');
    pager.pagination({
        layout:['list','sep','first','prev','sep','links','sep','next','last','info']});
    pager.pagination({
        onSelectPage: (pageNumber, pageSize) => loadUsersDataForPage(pageNumber, pageSize)
    });
    pager.pagination({
        pageNumber: pageNumber
    });
}

async function loadUsersCount() {
    const totalUsersCount = await axios.get('/users/count');

    $('#users-datagrid').datagrid('getPager').pagination({
        total: totalUsersCount.data
    });
}

async function loadUsersData(from = 1, count = 20) {
    const usersDatagrid = $('#users-datagrid');

    const users = (await axios.get('/users/data?from=' + from + '&count=' + count)).data.map(({id, firstName, lastName, email, roles}) => ({
        id:`
                <div class="checkbox-for-grid">
                    <input type="checkbox" class="select-user-check" value="${id}">
                </div>`,
            firstName,
            lastName,
            email,
            roles: `<div class="roles-presenter">${roles.reduce((accumulator, {name, id}) => accumulator + `${name}, `, '').trim().slice(0, -1)}
</div>`
    }));

    usersDatagrid.datagrid({data: (users)});
}

async function loadEditingUserData(id) {
    const user = (await axios.get('/users/' + id)).data;

    $('#editingUserId').val(user.id);
    $('#firstNameEdit').val(user.firstName);
    $('#lastNameEdit').val(user.lastName);
    $('#emailEdit').val(user.email);

    const roles = (await axios.get('/users/roles')).data;
    console.log(roles);
    const optionsAsString = roles.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '');
    $('#roles-edit').html(optionsAsString);

    $('#roles-edit option').each(function() {
        if(user.roles.some(role => role.id === parseInt($(this).val()))) {
            $(this).attr('selected', 'selected');
        }
    });

    $('#loading-edit-user').hide();
}

async function loadAndInitializeRoles() {
    const roles = (await axios.get('/users/roles')).data;
    const optionsAsString = roles.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '');
    $('#roles-create').html(optionsAsString);
    $('#roles-create').prop("selectedIndex", 0).val();
}

async function createUser() {
    $('#saving-create-user').show();
    $('span.validation-error').remove();

    var roles = [];
    $.each($("#roles-create option:selected"), function(){
        roles.push({id:$(this).val()});
    });

    const data = {
        firstName: $('#firstNameCreate').val(),
        lastName: $('#lastNameCreate').val(),
        email: $('#emailCreate').val(),
        password: $('#passwordCreate').val(),
        roles:roles
    };

    try {
        const response = await axios({
            method: 'post',
            url: '/users/add',
            data
        });

        $('#create-user-modal').modal('hide');
        clearCreateUserPopup();
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

    $('#saving-create-user').hide();
}

async function editUser() {
    $('#saving-edit-user').show();
    $('span.validation-error').remove();

    var roles = [];
    $.each($("#roles-edit option:selected"), function(){
        roles.push({id:$(this).val()});
    });

    const data = {
        id: $('#editingUserId').val(),
        firstName: $('#firstNameEdit').val(),
        lastName: $('#lastNameEdit').val(),
        email: $('#emailEdit').val(),
        password: 'ignore',
        roles:roles
    };

    try {
        const response = await axios({
            method: 'post',
            url: '/users/edit',
            data
        });

        $('#edit-user-modal').modal('hide');
        clearEditUserPopup();
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

    $('#saving-edit-user').hide();
}

async function deleteSelectedUser() {
    try {
        const response = await axios({
            method: 'delete',
            url: '/users/' + $('.select-user-check:checked').val()
        });

        location.reload();
    }
    catch (error) {
        console.log(error);
    }
}