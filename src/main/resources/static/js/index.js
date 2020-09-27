var dataBaseUrl;
var dataBaseName;
var checkedParam = {};//管理所有输入参数;//管理所有输入参数
var outputCheckedParam = {};//管理所有输出参数
$(function () {
    $(".previewTable").hide();
    $.ajax({
        url: '/dzh/getDatasource',
        success: function (result) {
            for (index in result) {
                $("#dataSource").append("<option value=" + result[index].dataBaseUrl + ">" + result[index].dataBaseName + "</option>");
            }
        },
        dataType: "json"
    })

});

//改变选择的表格
function changeSelectTable() {
    console.log('改变选择的表格');
    $('.tableList').hide();
    $(".allParamsList").empty();
    $("input[name='tableName']:checked").checke;
    $("input[name='tableName']:checked").each(function (j) {
        var tableName=$(this).val();
        $(".allParamsList").append(" <div class=\"tableParam\">\n" +
            "<div class=\"title\">"+$(this).val()+"</div>\n" +
            "<div class=\"paramsList\">\n"+
            "</div>\n"+
            "</div>");
        $.ajax({
            url:"/dzh/getColumnAndComments",
            data: {
                tableName:tableName ,
                dataBaseUrl: dataBaseUrl
            },
            success: function (result) {
                for (index in result) {
                    $(".paramsList").append("<div class=\"param\"><input class=\"checkbox-input\" type=\"checkbox\" name='columnName' value="+result[index].columnName+"> <span>"+result[index].columnDes+"</span></div>")
                }
            },
            dataType: "json"
        })
    })

}

//显示表格选择弹窗
function showTableSelect(boolean) {
    if (boolean) {
        $('.tableList').show();
        $(".tables").empty();
        dataBaseUrl = $("#dataSource option:selected").val();
        dataBaseName = $("#dataSource option:selected").text();
        $.ajax({
            url: "/dzh/getTables",
            data: {
                dataBaseName: dataBaseName,
                dataBaseUrl: dataBaseUrl
            },
            success: function (result) {
                for (index in result) {
                    $(".tables").append("<div class=\"table\"><input class=\"checkbox-input\" name=\"tableName\" type=\"checkbox\" value="+result[index].tableName+" >\n" +
                        "                                    <span>" + result[index].tableDes + "</span></div>")
                }
            },
            dataType: "json"
        })
    } else {
        $('.tableList').hide();
    }
    console.log('显示表格选择弹窗');
}

function checkTable(boolean) {
    if (boolean) {
        $('.selectedTable').show();
        $(".selectedTable").empty();
        $("input[name='tableName']:checked").each(function (j) {
            var tableName=$(this).val();
            $(".selectedTable").append("<div value=\"查看已选择数据表\">"+$(this).val()+"</div>");
        })

    } else {
        $('.selectedTable').hide();
    }
}
//添加参数到输入参数 prev
function  addInParam() {
    $(".paramsList input[name='columnName']:checked").each(function (i) {
        var value = $(this).val();
        var text = $(this).next().text();
        var tableName = $(this).parent().parent().prev().text();
        //避免重复添加  。。简陋避免。。。
        if(checkedParam[value]) {
            return;
        }
        checkedParam[value] = {
            value:value,
            text:text,
            tableName:tableName
        };
        $(".inparamsList").append(
            " <div class=\"param\">" +
            "<input  class=\"checkbox-input\" name='inparam' type=\"checkbox\" value="+value+">" +
            "<input  class=\"checkbox-input\" name='tableName' style='display: none' type=\"text\" value="+tableName+">" +
            "<span>"+text+"</span>" +
            "</div>");

        $.ajax({
            url: "/dzh/getColumnValue",
            data: {
                columnName: value,
                tableName: tableName,
                dataBaseUrl: dataBaseUrl
            },
            success: function (result) {

                var newParam = "<div class=\"inputParamItem\"name=\"" + value + "\">\n" +
                    "<div class=\"col-sm-2\" >"+text+":</div>\n" +
                    "<div class=\"col-sm-10\">\n" +
                    "<select class=\"form-control\">\n";
                //参数列表
                for (var index in result) {//.preview .inputParamList
                    newParam +="<option>"+result[index]+"</option>";
                }

                newParam += "</select>\n" +
                    "</div>\n" +
                    "</div>";
                $(".preview .inputParamList").append(newParam);
            },
            dataType: "json"
        });
    });
}

//设置输入参数所有可选数据项   e.....有点乱先不写了，就在删除的时候吧对应的select删除掉吧。。。gnn
function setInparamAvalible(){
    for(var key in checkedParam){

    }
}
//删除输入参数
function  deleteInParam() {
    //checkedParam 中移除选中的项
    $(".inparamsList input:checked").each(function (i) {
        var value = $(this).val();
        $(".preview .inputParamItem[name='"+value+"']").remove();
        checkedParam[value] = undefined;
    })
    $('.inparamsList input:checked').parent().remove();
    // $(".preview .inputParamList")
}

//添加参数到输入参数
function  addOutParam() {
    $(".paramsList input[name='columnName']:checked").each(function (i) {
        var value = $(this).val();
        var text = $(this).next().text();
        var tableName = $(this).parent().parent().prev().text();
        $(".outparamsList").append(" <div class=\"param\"><input class=\"checkbox-input\" name='outparam' type=\"checkbox\" value="+$(this).val()+"><span>"+$(this).next().text()+"</span></div>");
        //避免重复添加  。。简陋避免。。。
        if(outputCheckedParam[value]) {
            return;
        }
        outputCheckedParam[value] = {
            value:value,
            text:text,
            tableName:tableName
        }
    })
}
//删除输入参数
function  deleteOutParam() {
    $(".outparamsList input:checked").each(function (i) {
        var value = $(this).val();
        $(".preview .inputParamItem[name='"+value+"']").remove();
        outputCheckedParam[value] = undefined;
    });
    $('.outparamsList input:checked').parent().remove();
}

function  showResult() {
    var outParma;
    var tableName;
    var inParma;
    var count=0;
    var condition;
    for(var key in outputCheckedParam){
        if(count==0){
            outParma=outputCheckedParam[key].value;
        }else{
            outParma+=','+outputCheckedParam[key].value;
        }
        tableName=outputCheckedParam[key].tableName;
        count++;
    }
    count=0;
    for(var key in checkedParam){
        if(count==0){
            inParma=checkedParam[key].value;
            condition= checkedParam[key].value+"='"+$(".inputParamItem[name='"+checkedParam[key].value+"']  option:selected").text()+"'";
        }else{
            inParma+=','+checkedParam[key].value;
            condition+=','+checkedParam[key].value+"='"+$(".inputParamItem[name='"+checkedParam[key].value+"']  option:selected").text()+"'";

        }
        count++;
    }
    $.ajax({
        url: "/dzh/getData",
        data: {
            url: dataBaseUrl,
            tableName: tableName,
            conditions:condition,
            outParma:outParma
        },
        success: function (result) {
            console.log(result);
            $("#result").text(JSON.stringify(result,null,"\t"));
        },
        dataType: "json"
    })


}

function save() {
    var outParma;
    var tableName;
    var inParma;
    var count=0;
    var condition;
    var ztName;
    for(var key in outputCheckedParam){
        if(count==0){
            outParma=outputCheckedParam[key].value;
        }else{
            outParma+=','+outputCheckedParam[key].value;
        }
        tableName=outputCheckedParam[key].tableName;
        count++;
    }
    count=0;
    for(var key in checkedParam){
        if(count==0){
            inParma=checkedParam[key].value;
            condition= checkedParam[key].value+"="+$(".inputParamItem[name="+checkedParam[key].value+"]  option:selected").text();
        }else{
            inParma+=','+checkedParam[key].value;
            condition+=','+checkedParam[key].value+"="+$(".inputParamItem[name="+checkedParam[key].value+"]  option:selected").text();

        }
        count++;
    }

    ztName=$("#ztName").val();
    $.ajax({
        url: "/info/insertZtInfo",
        data: {
            ztName:ztName,
            url: dataBaseUrl,
            tableName: tableName,
            conditions:condition,
            outParma:outParma
        },
        success: function (result) {
            console.log(result);
        },
        dataType: "json"
    })
}