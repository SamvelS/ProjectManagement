$(function() {
    loadUsersData().then(() => initializeUsersDataGrid()).catch(err => console.log(err));
    loadAndInitializeRoles();

    $('#create-user').click(function () {
        clearCreateUserPopup();
        $('#saving-user').hide();
        $('#edit-user-modal').modal('show');
    });

    $('#save-created-user').click(async function (event) {
        event.preventDefault();
        await saveUser();
    });
});

function clearCreateUserPopup() {
    $('#firstName').val('');
    $('#lastName').val('');
    $('#email').val('');
    $('#password').val('');
    $('span.validation-error').remove();
}

function initializeUsersDataGrid() {
    const usersDatagrid = $('#users-datagrid');
    usersDatagrid.datagrid({pageSize:20});
    usersDatagrid.datagrid({pageList:[20,30,40,50]});
    usersDatagrid.datagrid('getPager').pagination({
        layout:['list','sep','first','prev','sep','links','sep','next','last','info']
    });
}

async function loadUsersData(from = 1, count = 20) {
    const totalUsersCount = await axios.get('/users/count');
    const usersDatagrid = $('#users-datagrid');

    const users = (await axios.get('/users/data?from=' + from + '&count=' + count)).data.map(({id,firstName, lastName, email}) => ({
        id:`
                <div style="height: 12px;">
                    <input type="checkbox" value="${id}">
                </div>`,
            firstName,
            lastName,
            email
    }));

    usersDatagrid.datagrid({data: (users)});

    usersDatagrid.datagrid('getPager').pagination({
        total: totalUsersCount
    });
}

async function loadAndInitializeRoles() {
    const roles = (await axios.get('/users/roles')).data;
    const optionsAsString = roles.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '');
    $('#roles').html(optionsAsString);
    $('#roles').prop("selectedIndex", 0).val();
}

async function saveUser() {
    $('#saving-user').show();

    var roles = [];
    $.each($("#roles option:selected"), function(){
        roles.push({id:$(this).val()});
    });

    const data = {
        firstName: $('#firstName').val(),
        lastName: $('#lastName').val(),
        email: $('#email').val(),
        password: $('#password').val(),
        roles:roles
    };

    console.log(data);

    try {
        const response = await axios({
            method: 'post',
            url: '/users/add',
            data
        });

        $('#edit-user-modal').modal('hide');
        clearCreateUserPopup();
    }
    catch (error) {
        if(error.response) {
            if(error.response.status == 400) {

                $('span.validation-error').remove();

                error.response.data.forEach((item, index) => {
                    if($("#" + item.fst).length > 0) {
                        $("<span class='validation-error'>" + item.snd + "</span>").insertBefore($("#" + item.fst));
                    }
                });
            }
        }
        else {
            console.log(error);
        }
    }

    $('#saving-user').hide();
}