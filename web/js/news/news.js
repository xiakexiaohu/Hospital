var contextPath = '';

$(function () {
    setPagination("#list");
    $("#statusSearch").combobox({
        onChange: function (n, o) {
            if (n != o) {
                doSearch();
            }
        }
    });
});

function add() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        $.post(contextPath + "/news/add",
            $("#addForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    $("#addWin").window("close");
                    dataGridReload("list");
                    $("#addForm").form("clear");
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function () {
                        toAdminLoginPage();
                    });
                } else {
                    $.messager.alert("提示", data.message, "info");
                }
            }
        );
    }
}

function showEdit() {
    var row = selectedRow("list");
    if (row) {
        $("#editForm").form("load", row);
        UE.getEditor("editEditor").setContent(row.content, false);
        openWin("editWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的新闻信息", "info");
    }
}

function showDelete(){
    var row = selectedRows("list");
    if (row.length!=0) {
        if(window.confirm("确认要删除？")){
            //批量删除，判断数组长度
            var ids="";
            for(var i=0;i<row.length;i++){
                if(i!=row.length-1){
                    ids+=row[i].id+",";
                }else{
                    ids+=row[i].id;
                }
            }
            $.post(contextPath + "/news/delete",{"ids":ids},function (data) {
                if (data.result == "success") {
                    dataGridReload("list");
                }
                $.messager.alert("提示",data.message,"info");
            });
        }
    }else {
        $.messager.alert("提示", "请选择需要删除的新闻信息", "info");
    }
}

function edit() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        $.post(contextPath + "/news/update",
            $("#editForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    closeWin("editWin");
                    $.messager.alert("提示", data.message, "info", function () {
                        dataGridReload("list");
                    });
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function () {
                        toAdminLoginPage();
                    });
                } else {
                    $("#errMsg").html(data.message);
                }
            }
        );
    }
}

function doSearch() {
    $("#list").datagrid({
        url: contextPath + '/news/search_pager',
        pageSize: 20,
        queryParams: getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url: contextPath + '/news/search_pager',
        pageSize: 20,
        queryParams: getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}

