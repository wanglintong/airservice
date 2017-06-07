<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>飞行动态记录</title>
<%@include file="base.jsp"%>
<script type="text/javascript" src="../js/base.js"></script>
</head>

<body>
	<div id="wrapper">
		<nav class="navbar navbar-default top-navbar" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle waves-effect waves-dark" data-toggle="collapse" data-target=".sidebar-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand waves-effect waves-dark" href="${pageContext.request.contextPath }/login/toLogin.do">
					<i class="large material-icons">insert_chart</i>
					<strong>机场调度系统</strong>
				</a>

				<div id="sideNav" href="">
					<i class="material-icons dp48">toc</i>
				</div>
			</div>

			<ul class="nav navbar-top-links navbar-right">
				<li>
					<a class="dropdown-button waves-effect waves-dark" href="#!" data-activates="dropdown1">
						<i class="fa fa-user fa-fw"></i>
						<b>${user.username}</b>
						<i class="material-icons right">arrow_drop_down</i>
					</a>
				</li>
			</ul>
		</nav>
		<!-- Dropdown Structure -->
		<ul id="dropdown1" class="dropdown-content">
			<li>
				<a href="javascript:alert('暂未开放')">
					<i class="fa fa-user fa-fw"></i>
					个人主页
				</a>
			</li>
			<li>
				<a href="javascript:alert('暂未开放')">
					<i class="fa fa-gear fa-fw"></i>
					设置
				</a>
			</li>
			<li>
				<a href="${pageContext.request.contextPath }/login/logout.do">
					<i class="fa fa-sign-out fa-fw"></i>
					退出系统
				</a>
			</li>
		</ul>

		<!--/. NAV TOP  -->
		<nav class="navbar-default navbar-side" role="navigation">
			<div class="sidebar-collapse">
				<ul class="nav" id="main-menu">

					<li>
						<a class="active-menu waves-effect waves-dark" href="${pageContext.request.contextPath }/login/toLogin.do">
							<i class="fa fa-dashboard"></i>
							飞行动态记录
						</a>
					</li>
					<li>
						<a href="${pageContext.request.contextPath}/flyinfo/toFlyInfoCheck.do " class="waves-effect waves-dark">
							<i class="fa fa-desktop"></i>
							增量信息审核
						</a>
					</li>
				</ul>
			</div>

		</nav>
		<!-- /. NAV SIDE  -->

		<div id="page-wrapper">
			<!-- 数据展示 -->
			<div class="panel-body" style="padding-bottom: 0px;">
				<div id="toolbar" class="btn-group">
					<button id="btn_add" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
						新增
					</button>
					<button id="btn_delete" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
						删除
					</button>
					<button id="btn_publish_single" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-check" aria-hidden="true"></span>
						发布单条
					</button>
					<button id="btn_publish_all" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-check" aria-hidden="true"></span>
						发布整表
					</button>
				</div>
				<div style="float: right; margin: 10px 0px 10px 25px">
					<!--  
					<input class="btn btn-default" type="text" id="select_time" >
					-->
					<button id="select_time" class="btn btn-default"></button>
					<button type="button" class="btn btn-default">
						<span style="color: #008000; background-color: #efefef; font-weight: bold;"></span>
						<span class="glyphicon glyphicon-time"></span>
						&nbsp;日期
					</button>
				</div>
				<table id="tb_departments"></table>
			</div>
			<!-- 数据展示 -->
		</div>
		<!-- /. PAGE WRAPPER  -->
	</div>
	<script type="text/javascript">
	$(function() {
	    $("#select_time").html(getNowFormatDate());
	    $('#select_time').datetimepicker({
	        minView: "month",
	        // 选择日期后，不会再跳转去选择时分秒
	        language: 'zh-CN',
	        format: 'yyyy-mm-dd',
	        todayBtn: 1,
	        autoclose: 1
	    }).on('changeDate',
	    function(ev) {
	        var time = ev.date.myDateFormat('yyyy-MM-dd');
	        $("#select_time").html(time);
	        // 重新发送请求获取数据
	        $("#tb_departments").bootstrapTable('refresh', {
	            url: '${pageContext.request.contextPath}/flyDynamic/getFlyDynamicList.do?time=' + time
	        });
	        $('#tb_departments').bootstrapTable('selectPage', 1); // Jump
	        // to
	        // the
	        // first
	        // page
	    });
	    // 1.初始化Table
	    var oTable = new TableInit();
	    oTable.Init();

	    // 2.初始化Button的点击事件
	    var oButtonInit = new ButtonInit();
	    oButtonInit.Init();
	});

	var TableInit = function() {
	    var oTableInit = new Object();
	    oTableInit.queryParams = {
	        "time": getNowFormatDate()
	    };
	    // 初始化Table
	    oTableInit.Init = function() {
	        $('#tb_departments').bootstrapTable({
	            url: '${pageContext.request.contextPath}/flyDynamic/getFlyDynamicList.do',
	            // 请求后台的URL（*）
	            method: 'get',
	            // 请求方式（*）
	            toolbar: '#toolbar',
	            // 工具按钮用哪个容器
	            striped: true,
	            // 是否显示行间隔色
	            cache: false,
	            // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
	            pagination: true,
	            // 是否显示分页（*）
	            sortable: false,
	            // 是否启用排序
	            sortOrder: "asc",
	            // 排序方式
	            // queryParams : oTableInit.queryParams,//传递参数（*）
	            sidePagination: "client",
	            // 分页方式：client客户端分页，server服务端分页（*）
	            pageNumber: 1,
	            // 初始化加载第一页，默认第一页
	            pageSize: 15,
	            // 每页的记录行数（*）
	            pageList: [15, 25, 50, 100],
	            // 可供选择的每页的行数（*）
	            search: true,
	            // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
	            strictSearch: false,
	            // 严格搜索模式
	            showColumns: true,
	            // 是否显示所有的列
	            showRefresh: true,
	            // 是否显示刷新按钮
	            minimumCountColumns: 2,
	            // 最少允许的列数
	            clickToSelect: true,
	            // 是否启用点击选中行
	            // height : 500,
	            // //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
	            uniqueId: "index",
	            // 每一行的唯一标识，一般为主键列
	            showToggle: true,
	            // 是否显示详细视图和列表视图的切换按钮
	            cardView: false,
	            // 是否显示详细视图
	            detailView: false,
	            // 是否显示父子表
	            rowStyle: function(row, index) {
	                var strclass = "";
	                if (row.state == 1) {
	                    strclass = 'success'; // 还有一个active
	                } else if (row.state == 0) {
	                    strclass = 'danger';
	                } else {
	                    return {};
	                }
	                return {
	                    classes: strclass
	                }
	            },
	            onEditableSave: function(field, row, oldValue, $el) {
	                // 格式化几个时间字段
	                if ((typeof row.estimatedFly) == 'string' && row.estimatedFly != '') {
	                    console.log(row.estimatedFly);
	                    var ymd = $("#select_time").html();
	                    ymd = ymd.replace(new RegExp(/(-)/g), '');
	                    var mh = row.estimatedFly.replace(':', '');
	                    var msg = ymd + mh + '00';
	                    if (/^\d+$/.test(msg)) {
	                        // 输入的全是数字
	                    } else {
	                        alert('输入数据有误');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    if (mh.length != 4) {
	                        alert('输入数据有误');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    if (mh.substring(0, 2) > 24 || mh.substring(0, 2) < 0) {
	                        alert('小时0~23');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    if (mh.substring(2, 4) > 60 || mh.substring(2, 4) < 0) {
	                        alert('分钟0~59');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    row.estimatedFly = msg;
	                }
	                if ((typeof row.preEstimatedFly) == 'string' && row.preEstimatedFly != '') {
	                    console.log(row.preEstimatedFly);
	                    var ymd = $("#select_time").html();
	                    ymd = ymd.replace(new RegExp(/(-)/g), '');
	                    var mh = row.preEstimatedFly.replace(':', '');
	                    var msg = ymd + mh + '00';
	                    if (/^\d+$/.test(msg)) {
	                        // 输入的全是数字
	                    } else {
	                        alert('输入数据有误');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    if (mh.length != 4) {
	                        alert('输入数据有误');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    if (mh.substring(0, 2) > 24 || mh.substring(0, 2) < 0) {
	                        alert('小时0~23');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    if (mh.substring(2, 4) > 60 || mh.substring(2, 4) < 0) {
	                        alert('分钟0~59');
	                        $("#tb_departments").bootstrapTable('refresh');
	                        return;
	                    }
	                    row.preEstimatedFly = msg;
	                }
	                var str = JSON.stringify(row);
	                $.ajax({
	                    type: "post",
	                    url: '${pageContext.request.contextPath}/flyDynamic/updateFlyDynamic.do',
	                    data: {
	                        "flyInfoStr": str
	                    },
	                    success: function(data) {
	                        $("#tb_departments").bootstrapTable('refresh');
	                        console.log(data);
	                    },
	                    error: function() {
	                        alert("输入数据错误");
	                        $("#tb_departments").bootstrapTable('refresh');
	                    },
	                    complete: function() {

	}

	                });
	            },
	            columns: [{
	                checkbox: true
	            },
	            {
	                field: 'index',
	                title: '次序',
	                align: 'center',
	                formatter: function(value, row, index) {
	                    return index + 1; // 返回行号
	                }
	            },
	            {
	                field: 'props',
	                title: '属性',
	                align: 'center',
	                editable: {
	                    type: 'select',
	                    source: [{
	                        value: '国际',
	                        text: '国际'
	                    },
	                    {
	                        value: '国内',
	                        text: '国内'
	                    },
	                    ]
	                }
	            },
	            {
	                field: 'task',
	                title: '性质',
	                align: 'center',
	                editable: true
	            },
	            {
	                field: 'planeType',
	                title: '机型',
	                align: 'center',
	                editable: true
	            },
	            {
	                field: 'planeNo',
	                title: '机号',
	                align: 'center',
	                editable: true
	            },
	            {
	                field: 'incomingFlyNo',
	                title: '进港航班号',
	                align: 'center',
	                editable: true
	            },
	            {
	                field: 'departureFlyNo',
	                title: '出港航班号',
	                align: 'center',
	                editable: true
	            },
	            {
	                field: 'estimatedFly',
	                title: '本站预计起飞',
	                align: 'center',
	                editable: true,
	                /*
									 * editable: { type: 'combodate', viewformat:
									 * 'HH:mm', template: 'HH:mm', format: 'HH:mm',
									 * combodate: { minuteStep: 1, secondStep: 1,
									 * maxYear: 5000, minYear: 2016, } },
									 */
	                formatter: function(value, row, index) {
	                    if (value == null || value == '') {
	                        return "";
	                    }
	                    value = value + '';
	                    value = value.substring(8, 12);
	                    return value;
	                }

	            },
	            {
	                field: 'preEstimatedFly',
	                title: '前站预计起飞',
	                align: 'center',
	                editable: true,
	                formatter: function(value, row, index) {
	                    if (value == null || value == '') {
	                        return "";
	                    }
	                    value = value + '';
	                    value = value.substring(8, 12);
	                    return value;
	                }
	            },
	            {
	                field: 'flightLine',
	                title: '动态',
	                align: 'center',
	                editable: true
	            },
	            {
	                field: 'remark',
	                title: '备注',
	                align: 'center',
	                editable: true
	            },
	            {
	                field: 'time',
	                title: '时间',
	                align: 'center',
	            },
	            {
	                field: 'state',
	                title: '状态',
	                align: 'center',
	                formatter: function(value, row, index) {
	                    var str = '';
	                    if (value == 0) {
	                        str = '未发布';
	                    } else {
	                        str = '已发布';
	                    }
	                    return str;
	                }
	            }]
	        });
	    };

	    return oTableInit;
	};

	var ButtonInit = function() {
	    var oInit = new Object();
	    var postdata = {};

	    oInit.Init = function() {
	        // 初始化页面上面的按钮事件
	        $('#btn_publish_all').click(function() {
	            var arrselections = $("#tb_departments").bootstrapTable('getSelections');
	            if (arrselections.length <= 0) {
	                swal({
	                    title: "操作提示",
	                    text: "请选择要发布的记录",
	                    type: "warning",
	                    confirmButtonColor: "#DD6B55",
	                    confirmButtonText: "确定"
	                });
	                return;
	            }
	            swal({
	                title: "操作提示",
	                text: "确认发布最新整表?",
	                type: "warning",
	                showCancelButton: true,
	                cancelButtonText: '取消',
	                confirmButtonColor: "#DD6B55",
	                confirmButtonText: "确定"
	            }).then(function() {
	                // 发布选中的记录
	                $.ajax({
	                    type: "post",
	                    url: '${pageContext.request.contextPath}/flyDynamic/publishAll.do',
	                    data: {
	                        "msg": JSON.stringify(arrselections)
	                    },
	                    success: function(data) {
	                        if (data.state == "ok") {
	                            // 发布成功
	                            swal({
	                                title: "操作提示",
	                                text: "发布整表成功("+arrselections.length+"条记录)",
	                                type: "success",
	                                confirmButtonText: "确定"
	                            }).then(function() {
	                                $("#tb_departments").bootstrapTable('refresh');
	                            });
	                        } else {
	                            alert("发布失败");
	                        }
	                    },
	                    error: function() {
	                        alert("发布失败");
	                    }
	                });
	            },
	            function(dismiss) {});

	        });
	        
	        
	        $('#btn_publish_single').click(function() {
	            var arrselections = $("#tb_departments").bootstrapTable('getSelections');
	            if (arrselections.length <= 0) {
	                swal({
	                    title: "操作提示",
	                    text: "请选择要发布的记录",
	                    type: "warning",
	                    confirmButtonColor: "#DD6B55",
	                    confirmButtonText: "确定"
	                });
	                return;
	            }
	            $.ajax({
	                type: "post",
	                url: '${pageContext.request.contextPath}/flyDynamic/checkIsPublished.do',
	                data: {
	                    "msg": JSON.stringify(arrselections)
	                },
	                success: function(data) {
	                    if(data.retList.length==0) {
	                    	//新增记录无重复
	                    	swal({
                                title: "操作提示",
                                text: data.retList2.length + "条记录发布成功",
                                type: "success",
                                confirmButtonText: "确定"
                            }).then(function() {
                                $("#tb_departments").bootstrapTable('refresh');
                            });
	                    }else {
	                    	var html = "<hr/><table>";
	                    	for(var i=0;i<data.retList.length;++i) {
	                    		html += "<tr><td>进港航班号：" + data.retList[i].incomingFlyNo + "&nbsp&nbsp&nbsp出港航班号：" + data.retList[i].departureFlyNo + "</td></tr>";
	                    	}
	                    	html += "<tr><td><br/><br/></td></tr><tr><td>是：覆盖原表相同航班号的航班信息</td></tr>";
	                    	html += "<tr><td>否：在原表的基础上，新增选中的航班信息</td></tr></table>";
	                    	//存在重复记录
	                    	swal({
                                title: "您选择发布的航班号和原表有重复，是否覆盖?",
                                width: "700",
                                html: html,
                                type: "info",
                                showCancelButton: true,
                                confirmButtonText: '是',
                                cancelButtonText: '否'
                            }).then(function() {
                            	$.post("${pageContext.request.contextPath}/flyDynamic/publishSingleUpdate.do",{"msg1":JSON.stringify(data.retList),"msg2":JSON.stringify(data.retList2)},function(data){
									swal({
		                                title: data.retMsg,
		                                type: "success",
		                                confirmButtonText: "确定"
		                            }).then(function() {
		                                $("#tb_departments").bootstrapTable('refresh');
		                            });
								});
                            }, function(dismiss) {
                            	if(dismiss=='cancel') {
									$.post("${pageContext.request.contextPath}/flyDynamic/publishSingleAdd.do",{"msg":JSON.stringify(data.retList3)},function(data){
										swal({
			                                title: data.retMsg,
			                                type: "success",
			                                confirmButtonText: "确定"
			                            }).then(function() {
			                                $("#tb_departments").bootstrapTable('refresh');
			                            });
									});
                            	}else {
                            		console.log('退出，不做任何操作');
                            	}
                            });
	                    }
	                },
	                error: function() {
	                    alert("Error");
	                }
	            });
	            
	            /*
	            var hasPublishCount = 0;
	            for (var i = 0; i < arrselections.length; ++i) {
	                if (arrselections[i].state == 1) {
	                    hasPublishCount++;
	                }
	            }
	            swal({
	                title: "操作提示",
	                text: hasPublishCount == 0 ? "确认发布选中的" + arrselections.length + "条记录?": "确认发布选中的" + arrselections.length + "条记录?其中有" + hasPublishCount + "条记录已发布，再次发布会覆盖之前的记录",
	                type: "warning",
	                showCancelButton: true,
	                cancelButtonText: '取消',
	                confirmButtonColor: "#DD6B55",
	                confirmButtonText: "确定"
	            }).then(function() {
	                // 发布选中的记录
	                $.ajax({
	                    type: "post",
	                    url: '${pageContext.request.contextPath}/flyDynamic/publish.do',
	                    data: {
	                        "msg": JSON.stringify(arrselections)
	                    },
	                    success: function(data) {
	                        if (data.state == "ok") {
	                            // 发布成功
	                            swal({
	                                title: "操作提示",
	                                text: hasPublishCount == 0 ? arrselections.length + "条记录发布成功": arrselections.length + "条记录发布成功,其中" + hasPublishCount + "条记录为再次发布",
	                                type: "success",
	                                confirmButtonText: "确定"
	                            }).then(function() {
	                                $("#tb_departments").bootstrapTable('refresh');
	                            });
	                        } else {
	                            alert("删除失败");
	                        }
	                    },
	                    error: function() {
	                        alert("删除失败");
	                    }
	                });
	            },
	            function(dismiss) {});
	            */

	        });
	        $('#btn_add').click(function() {
	            $('#tb_departments').bootstrapTable('selectPage', 1); // Jump
	            // to
	            // the
	            // first
	            // page
	            var data = {
	                id: uuid(),
	                task: '',
	                planeType: '',
	                planeNo: '',
	                incomingFlyNo: '',
	                departureFlyNo: '',
	                estimatedFly: '',
	                preEstimatedFly: '',
	                flightLine: '',
	                remark: '',
	                props: '国内',
	                time: $("#select_time").html(),
	                addTime: new Date().getTime(),
	                userId: '${user.id}',
	                state: 0
	            }; // define a new row data，certainly it's empty
	            $('#tb_departments').bootstrapTable('append', data); // the
	            $.ajax({
	                type: "post",
	                url: '${pageContext.request.contextPath}/flyDynamic/updateFlyDynamic.do',
	                data: {
	                    "flyInfoStr": JSON.stringify(data)
	                },
	                success: function(data) {
	                    console.log(data);
	                },
	                error: function() {
	                    alert("Error");
	                }
	            });
	            $("#dataTable tr:eq(1) td:eq(0)").trigger("dblclick");
	        });
	        $("#btn_delete").click(function() {
	            var arrselections = $("#tb_departments").bootstrapTable('getSelections');
	            if (arrselections.length <= 0) {
	                swal({
	                    title: "操作提示",
	                    text: "请选择删除信息",
	                    type: "warning",
	                    confirmButtonColor: "#DD6B55",
	                    confirmButtonText: "确定"
	                });
	                return;
	            }
	            var hasPublishCount = 0;
	            for (var i = 0; i < arrselections.length; ++i) {
	                if (arrselections[i].state == 1) {
	                    hasPublishCount++;
	                }
	            }
	            if (hasPublishCount != 0) {
	                swal({
	                    title: "操作提示",
	                    text: "选择的记录中有" + hasPublishCount + "条记录为已发布记录，不允许删除",
	                    type: "warning",
	                    confirmButtonColor: "#DD6B55",
	                    confirmButtonText: "确定"
	                });
	                return;
	            }
	            swal({
	                title: "操作提示",
	                text: "确认删除选中的" + arrselections.length + "条记录?",
	                type: "warning",
	                showCancelButton: true,
	                cancelButtonText: '取消',
	                confirmButtonColor: "#DD6B55",
	                confirmButtonText: "确定"
	            }).then(function() {
	                var ids = '';
	                for (var i = 0; i < arrselections.length; ++i) {
	                    ids += arrselections[i].id + ",";
	                }
	                $.ajax({
	                    type: "post",
	                    url: '${pageContext.request.contextPath}/flyDynamic/delete.do',
	                    data: {
	                        "idStr": ids
	                    },
	                    success: function(data) {
	                        if (data.state == "ok") {
	                            $("#tb_departments").bootstrapTable('refresh');
	                            console.log(data);
	                        } else {
	                            alert("删除失败");
	                        }
	                    }
	                });
	            });
	        });

	    };
	    return oInit;
	};
	</script>
</body>
</html>