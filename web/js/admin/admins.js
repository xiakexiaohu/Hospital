var contextPath = '..';

$(function() {
    setPagination("#list");
    $("#statusSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
});

function add() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        $.post(contextPath + "/admin/add",
            $("#addForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    $("#addWin").window("close");
                    dataGridReload("list");
                    $("#addForm").form("clear");
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toAdminLoginPage();
                    });
                } else {
                    $.messager.alert("提示", data.message, "info");
                }
            }
        );
    }
}

function showDelete(userEmail) {
    var row = selectedRows("list");
    if(row.length!=0){
        if(window.confirm("确认要删除?")){
            var sameUserFlag=false;
            var ids="";
            var emails="";
            for(var i=0;i<row.length;i++){
                if(i!=row.length-1){
                    ids+=row[i].id+",";
                    emails+=row[i].email+",";
                }else{
                    ids+=row[i].id;
                    emails+=row[i].email;
                }

                if (row[i].email == userEmail) {
                    sameUserFlag=true;
                    this.alert("您不能删除当前账户！！！");
                    break;
                }
            }
            //判断是否包含当前用户
            if(!sameUserFlag){
                //执行批量删除操作
                $.post(contextPath + "/admin/delete", {"ids": ids, "emails": emails}, function (data) {
                    if (data.result == "success") {
                        dataGridReload("list");
                    }
                    $.messager.alert("提示", data.message, "info");
                });
            }
        }
    }else{
        $.messager.alert("提示", "请选择需要删除的用户信息", "info");
    }
}

function showEdit() {
    var row = selectedRow("list");
    if (row) {
        $("#editForm").form("load", row);
        openWin("editWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的用户信息", "info");
    }
}


function edit() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        $.post(contextPath + "/admin/update",
            $("#editForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    closeWin("editWin");
                    $.messager.alert("提示", data.message, "info", function () {
                        dataGridReload("list");
                    });
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toAdminLoginPage();
                    });
                } else {
                    $("#errMsg").html(data.message);
                }
            }
        );
    }
}

function showUpdatePwd() {
    var row = selectedRow("list");
    if (row) {
        $("#editPwdForm").form("load", row);
        $("#update_password").textbox("setValue", "");
        openWin("editPwdWin");
    } else {
        $.messager.alert("提示", "请选择需要修改密码的用户", "info");
    }
}

//更新用户密码
function updatePwd() {
    //因为Mysql传递的id，而Mongodb根据username(即mysql中的email)删除
    //对前段jsp页面修改email为hidden属性
    var row=selectedRow("list");
    toValidate("editPwdForm");
    if (validateForm("editPwdForm")) {
        $.messager.confirm("提示", "更新该用户密码，是否继续?", function(r) {
            if (r) {
                $.post(contextPath + "/admin/update_other_pwd",
                    $("#editPwdForm").serialize(),
                    function (data) {
                        if (data.result == "success") {
                            closeWin("editPwdWin");
                            $.messager.alert("提示", data.message, "info", function () {
                                // dataGridReload("list");
                            });
                        } else if (data.result == 'notLogin') {
                            $.messager.alert("提示", data.message, "info", function() {
                                toAdminLoginPage();
                            });
                        } else {
                            $("#errMsg").html(data.message);
                        }
                    }
                );
            }
        });
    }
}

function inactive() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'N') {
            $.messager.alert("提示", "用户不可用,无需冻结", "info");
        } else {
            $.get(contextPath + "/admin/inactive?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toAdminLoginPage();
                        });
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要冻结的用户", "info");
    }
}

function active() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'Y') {
            $.messager.alert("提示", "用户可用,无需激活", "info");
        } else {
            $.get(contextPath + "/admin/active?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toAdminLoginPage();
                        });
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要激活的用户", "info");
    }
}

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/admin/search_pager',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/admin/search_pager',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}
