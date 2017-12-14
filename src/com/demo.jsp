<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="global" uri="http://ssj.kingdee.com/tags/html" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <jsp:include page="../../../public/console_lib.jsp"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>产品表</title>
</head>
<body>
<div id="cc" class="easyui-layout" style="width: 100%; height: 630px; margin-left: auto; margin-right: auto">

    <div data-options="region:'north',title:'查询条件',iconCls:'icon-mysearch ',split:true"
         style="width: 100%;height:90px;">
        <form id="queryConditionForm">
            <input name="whatSoEver" style="display:none;"/>
            <table cellspacing="1" cellpadding="0" class="tb_searchbar">
                <tr>
                    [&foreach&]
                    <td class="td_title" style="width: 65px;" align="right">[&fieldName]</td>
                    <td style="width: 120px;">
                        <input name="[&fieldCode]" style="width:110px;" class="easyui-textbox"/>
                    </td>
                    [/&foreach&]
                   
                    <td align="left" rowspan="2">
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search"
                           onclick="query();">查询</a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel"
                           onclick="clearForm('queryConditionForm');">清空</a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                    </td>
                </tr>
            </table>
        </form>

    </div>

    <div data-options="region:'center',border:false" style="width: 100%; height: 850px;">
        <table id="dataGrid" class="easyui-datagrid" title="产品表" style="width:100%;height:100%;"
               url="${ctx}/data/dataProduct/dataGrid" method="post" rownumbers="true" singleSelect="true"
               pagination="true" pageSize="30" pageList="[10, 15 , 20, 30, 40 ]">
            <thead>
            <tr>
				  [&foreach&]
                <th field="[&fieldCode]" width="140" align="center">[&fieldName]</th>
				 [/&foreach&]
            </tr>
            </thead>
        </table>
    </div>

</div>

<script type="text/javascript">

    function query() {
        var data = $("#queryConditionForm").serializeObject();
        $("#dataGrid").datagrid('load', data);
    }

</script>
</body>
</html>