$(function() {
    loadUsersData().then(() => initializeUsersDataGrid()).catch(err => console.log(err));
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

    const users = (await axios.get('/users/data?from=' + from + '&count=' + count)).data;

    usersDatagrid.datagrid({data: (users)});

    usersDatagrid.datagrid('getPager').pagination({
        total: totalUsersCount
    });
}