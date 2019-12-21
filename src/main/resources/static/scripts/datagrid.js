$(function() {
    loadUsersData().then(() => initializeUsersDataGrid()).catch(err => console.log(err));
    loadAndInitializeRoles();

    $('#create-user').click(function () {
        $('#edit-user-modal').modal('show');
    });
});

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
    console.log(roles);
    const optionsAsString = roles.reduce((accumulator, {name, id}) => accumulator + `<option value="${id}">${name}</option>`, '');
    console.log(optionsAsString);
    $('#roles').html(optionsAsString);
}