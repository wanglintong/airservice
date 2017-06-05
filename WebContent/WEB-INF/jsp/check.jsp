<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>增量信息审核</title>
<%@include file="base.jsp"%>
<script type="text/javascript" src="../js/base.js"></script>
<style type="text/css">
fieldset {
    padding: .35em .625em .75em;
    margin: 0 2px;
    border: 1px solid silver;
}
[type="radio"]:not(:checked), [type="radio"]:checked {
    /* position: absolute; */
    /* left: -9999px; */
    position:inherit;
    opacity: 10;
}
</style>
</head>
<body id="body">
	<div id="wrapper">
		<nav class="navbar navbar-default top-navbar" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle waves-effect waves-dark"
					data-toggle="collapse" data-target=".sidebar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand waves-effect waves-dark" href="${pageContext.request.contextPath }/login/toLogin.do"><i
					class="large material-icons">insert_chart</i> <strong>机场调度系统</strong></a>

				<div id="sideNav" href="">
					<i class="material-icons dp48">toc</i>
				</div>
			</div>

			<ul class="nav navbar-top-links navbar-right">
				<li><a class="dropdown-button waves-effect waves-dark"
					href="#!" data-activates="dropdown1"><i
						class="fa fa-user fa-fw"></i> <b>${user.username}</b> <i
						class="material-icons right">arrow_drop_down</i></a></li>
			</ul>
		</nav>
		<!-- Dropdown Structure -->
		<ul id="dropdown1" class="dropdown-content">
			<li><a href="javascript:alert('暂未开放')"><i
					class="fa fa-user fa-fw"></i> 个人主页</a></li>
			<li><a href="javascript:alert('暂未开放')"><i
					class="fa fa-gear fa-fw"></i> 设置</a></li>
			<li><a
				href="${pageContext.request.contextPath }/login/logout.do"><i
					class="fa fa-sign-out fa-fw"></i> 退出系统</a></li>
		</ul>

		<!--/. NAV TOP  -->
		<nav class="navbar-default navbar-side" role="navigation">
			<div class="sidebar-collapse">
				<ul class="nav" id="main-menu">

					<li><a class="waves-effect waves-dark"
						href="${pageContext.request.contextPath }/login/toLogin.do"><i
							class="fa fa-dashboard"></i> 飞行动态记录</a></li>
					<li><a
						href="${pageContext.request.contextPath}/flyinfo/toFlyInfoCheck.do "
						class="active-menu waves-effect waves-dark"><i
							class="fa fa-desktop"></i> 增量信息审核</a></li>
				</ul>
			</div>

		</nav>
		<!-- /. NAV SIDE  -->

		<div id="page-wrapper">
			<!-- 数据展示 -->
			<div class="panel-body" style="padding-bottom: 0px;">
				<div id="toolbar" class="btn-group">
					<button id="btn_check" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-check" aria-hidden="true"></span>审核
					</button>
					<button id="btn_ignore" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>忽略
					</button>
				</div>
				<div style="float: right; margin: 10px 0px 10px 25px">
					<button id="select_time" class="btn btn-default"></button>

					<button type="button" class="btn btn-default">
						<span
							style="color: #008000; background-color: #efefef; font-weight: bold;"></span>
						<span class="glyphicon glyphicon-time"></span>&nbsp;日期
					</button>
				</div>
				<table id="tb_departments"></table>
			</div>
			<!-- 数据展示 -->
		</div>
		<!-- /. PAGE WRAPPER  -->
	</div>
	<script type="text/javascript">
		var ws = null;
		var currentMessageId = '';
		var currentMessageStatus = '';
		$(function() {	
			/**websocket**/
			connect();
			
			var newMessage = '';
			$("#body").change(function(e){
				if($(e.target).is("#ta")){
					newMessage = e.target.value;
				};
			});
			$("#body").click(function(e){
				if($(e.target).is("#changeMessage")){
					//如果报文状态已忽略和已审核 不允许修改该报文
					if(currentMessageStatus==2) {
						alert('报文已审核，禁止修改');
						return;
					}
					if(currentMessageStatus==1) {
						alert("报文已忽略，禁止修改");
						return;
					}
					if(newMessage=='') {
						alert('报文未修改');
						return;
					}
					$.post('${pageContext.request.contextPath}/message/parseMessage.do',{'message':newMessage,'messageId':currentMessageId},function(data){
						if(data.state=='ok') {
							//修改报文内容
							console.log("id = " + currentMessageId);
							var msg = data.msg;
							var html = createTableMessage(JSON.parse(msg));
							$("#messageInfoTable").html(html);
							$("#tb_departments").bootstrapTable('refresh');
						}else {
							//新报文存在问题解析失败
							alert('新报文解析失败');
						}
					});
					newMessage = '';
				};
			});
			
			$("#select_time").html(getNowFormatDate());
			$('#select_time').datetimepicker({
			    minView: "month", //选择日期后，不会再跳转去选择时分秒 
			    language:  'zh-CN',
			    format: 'yyyy-mm-dd',
			    todayBtn:  1,
			    autoclose: 1
			}).on('changeDate',function(ev){
				var time = ev.date.myDateFormat('yyyy-MM-dd');
				$("#select_time").html(time);
				//重新发送请求获取数据
				$("#tb_departments")
						.bootstrapTable(
								'refresh',
								{
									url : '${pageContext.request.contextPath}/message/getMessageList.do?time='
											+ time
								});
				$('#tb_departments').bootstrapTable('selectPage', 1); //Jump to the first page  
			});
			//1.初始化Table
			var oTable = new TableInit();
			oTable.Init();

			//2.初始化Button的点击事件
			var oButtonInit = new ButtonInit();
			oButtonInit.Init();
		});

		var TableInit = function() {
			var oTableInit = new Object();
			oTableInit.queryParams = {
				"time" : getNowFormatDate()
			};
			//初始化Table
			oTableInit.Init = function() {
				$('#tb_departments')
						.bootstrapTable(
								{
									url : '${pageContext.request.contextPath}/message/getMessageList.do', //请求后台的URL（*）
									method : 'get', //请求方式（*）
									toolbar : '#toolbar', //工具按钮用哪个容器
									striped : true, //是否显示行间隔色
									cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
									pagination : true, //是否显示分页（*）
									sortable : false, //是否启用排序
									sortOrder : "asc", //排序方式
									//queryParams : oTableInit.queryParams,//传递参数（*）
									sidePagination : "client", //分页方式：client客户端分页，server服务端分页（*）
									pageNumber : 1, //初始化加载第一页，默认第一页
									pageSize : 15, //每页的记录行数（*）
									pageList : [ 15, 25, 50, 100 ], //可供选择的每页的行数（*）
									search : true, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
									strictSearch : false,//严格搜索模式
									showColumns : true, //是否显示所有的列
									showRefresh : true, //是否显示刷新按钮
									minimumCountColumns : 2, //最少允许的列数
									clickToSelect : true, //是否启用点击选中行
									//height : 500, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
									uniqueId : "index", //每一行的唯一标识，一般为主键列
									showToggle : true, //是否显示详细视图和列表视图的切换按钮
									cardView : false, //是否显示详细视图
									detailView : false, //是否显示父子表
									onClickRow : function(row, $element, field) {
										var jsonMessage = JSON.parse(row.jsonMessage);
										var html = createTableMessage(jsonMessage);
										
										if (field == 'message') {
											currentMessageId = row.id;
											currentMessageStatus = row.status;
											//显示报文详细信息
											swal(
													{
														title : "报文详情",
														width:700,
														html : "<div id='mainDiv' style='height:360px;'>"
																+ "<div id='leftDiv' style='float:left;width:70%;height:350px;'>"
																+ "<textarea id='ta' style='height:350px;font-size:14px;'>"
																+ row.message
																+ "</textarea>"
																+ "<button id='changeMessage'>修改报文</button>"
																+ "</div>"

																+ "<div id='rightDiv' style='float:left ;  width:30%; height:350px;''>"
																+ "<table id='messageInfoTable' style='font-size:14px'>"
																+ html
																+ "</table>"
																+ "</div>"
																+ "</div>",
														showCancelButton : true,
														cancelButtonText : '忽略',
														confirmButtonColor : "#DD6B55",
														confirmButtonText : "审核"
													}).then(function(){
														console.log('执行审核操作');
														//先检查报文是否已审核
														if(row.status==2) {
															swal({
																title:'消息提示',
																text:'报文已审核,请不要重复审核!',
																type:'warning'
															});
															return;
														}
														//检查报文是否已忽略
														if(row.status==1) {
															swal({
																title:'消息提示',
																text:'报文已忽略,无法审核!',
																type:'warning'
															});
															return;
														}
														var checkMessageId = row.id;
														
														$.post("${pageContext.request.contextPath}/message/checkFlyNo.do",{'ids':checkMessageId},function(data){
															//console.log(JSON.stringify(data));
															var noRepeat = true;
															for(var key in data) {
																var len = data[key].length;
																if(len>1) {
																	noRepeat = false;
																	break;
																}
															}
															if(noRepeat) {
																$.post("${pageContext.request.contextPath}/message/check.do",{'ids':checkMessageId},function(data){
																	if(data.state=='ok') {
																		swal({
																			title:'消息提示',
																			text: '报文审核成功',
																			type:'success',
																			confirmButtonText : "确定"
																		});
																		$("#tb_departments").bootstrapTable('refresh');
																	}else {
																		swal({
																			title:'消息提示',
																			text: '审核失败',
																			type:'error',
																			confirmButtonText : "确定"
																		});
																	}
																});
															}else {
																var msg = [];//记录messageid:flyInfoId
																var html = '';
																for(var key in data) {
																	var len = data[key].length;
																	if(len==1) {
																		//console.log(key);
																		var content = {};
																		content[key] = data[key][0].id;
																		msg.push(content);
																	}else if(len>1) {
																		html += '<fieldset>';
																		for(var j=0 ; j<len ; ++j) {
																			var obj = data[key][j];
																			html += '<input type="radio" name="' + key + '" data-id="' + obj.id + '">&nbsp;&nbsp;进港航班号:' + obj.incomingFlyNo + " 出港航班号:" + obj.departureFlyNo + " 机型:" + obj.planeType + "<br/>";
																		}
																		html += '</fieldset><br/>';
																	}
																}
																swal({
													                title: "报文中的航班号存在多个，请选择更新",
													                width: "700",
													                html: html,
													                type: "warning",
													                confirmButtonColor: "#DD6B55",
													                confirmButtonText: "确定"
													            }).then(function() {
													               var radios = $("input:radio:checked");
													               for(var i=0 ; i<radios.length ; ++i) {
													            	   var content = {};
													            	   content[$(radios[i]).attr('name')] = $(radios[i]).attr('data-id');
													            	   msg.push(content);
													               }
													               /**审核**/
													               console.log(JSON.stringify(msg));
													               $.post("${pageContext.request.contextPath}/message/checkRepeat.do",{'msg':JSON.stringify(msg)},function(data){
													            	  if(data.state=='ok') {
													            		  alert('审核成功');
													            		  $("#tb_departments").bootstrapTable('refresh');
													            	  }else {
													            		  alert('审核失败');
													            	  }
													               });
													            },function(dismiss){});
															}
														});
													},function(dismiss){
														if(dismiss=='cancel') {
															if(row.status==2) {
																swal({
																	title:'消息提示',
																	text:'报文已审核，无法忽略!',
																	type:'warning'
																});
																return;
															}
															if(row.status==1) {
																swal({
																	title:'消息提示',
																	text:'报文已忽略，请不要重复忽略!',
																	type:'warning'
																});
																return;
															}
															$.post("${pageContext.request.contextPath}/message/ignore.do",{'ids':row.id},function(data) {
																if(data.state=='ok') {
																	swal({
																		title:'消息提示',
																		text:'操作成功',
																		type:'success'
																	});
																	$("#tb_departments").bootstrapTable('refresh');
																}else {
																	swal({
																		title:'消息提示',
																		text:'忽略失败',
																		type:'error'
																	});
																}
															});
														}
													});
										}
									},
									rowStyle : function(row, index) {
										var strclass = "";
										if (row.status == 0) {
											strclass = 'danger';//还有一个active
										} else if (row.status == 2) {
											strclass = 'success';
										} else {
											strclass = 'warning';
										}
										return {
											classes : strclass
										}
									},
									columns : [
											{
												checkbox : true
											},
											{
												field : 'index',
												title : '次序',
												align : 'center',
												formatter : function(value,
														row, index) {
													return index + 1; //返回行号  
												}
											},
											{
												field : 'jsonMessage',
												title : '报文类型',
												align : 'center',
												formatter : function(value,
														row, index) {
													var json = JSON.parse(value
															+ '');
													return json.messageType;
												}
											},
											{
												field : 'jsonMessage',
												title : '航班号',
												align : 'center',
												formatter : function(value,
														row, index) {
													var str = '';
													var json = JSON.parse(value
															+ '');
													if (json.incomingFlyNo != null) {
														str = json.incomingFlyNo
																+ '   (进港)';
													} else if (json.departureFlyNo != null) {
														str = json.departureFlyNo
																+ '   (出港)';
													} else if (json.flyNo != null) {
														str = json.flyNo;
													}
													return str;
												}
											},
											{
												field : 'jsonMessage',
												title : '航线',
												align : 'center',
												formatter : function(value,
														row, index) {
													var str = '';
													var json = JSON.parse(value
															+ '');
													return json.flightLine + '';
												}
											},
											{
												field : 'message',
												title : '报文',
												align : 'center',
												formatter : function(value,
														row, index) {
													var str = '' + value;
													if (str.length > 60) {
														str = str.substring(0,
																60)
																+ "...";
													}
													return str;
												}
											},
											{
												field : 'addTime',
												title : '时间',
												align : 'center',

												formatter : function(value,
														row, index) {
													return new Date(value)
															.myDateFormat("hh:mm:ss");
												}

											},
											{
												field : 'status',
												title : '状态',
												align : 'center',
												formatter : function(value,
														row, index) {
													var str = '';
													if (value == 0) {
														str = '未审核';
													} else if (value == 1) {
														str = '已忽略';
													} else {
														str = '已审核';
													}
													return str;
												}
											} ]
								});
			};

			return oTableInit;
		};

		var ButtonInit = function() {
			var oInit = new Object();
			var postdata = {};

			oInit.Init = function() {
				//初始化页面上面的按钮事件
				$('#btn_check').click(function() {
					var arrselections = $("#tb_departments").bootstrapTable('getSelections');
					if (arrselections.length <= 0) {
						swal({
							title : "操作提示",
							text : "请选择审核的报文",
							type : "warning",
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确定"
						});
						return;
					}
					var hasIgnoreCount = 0;
					var hasCheckCount = 0;
					var ids = '';
					for (var i = 0; i < arrselections.length; ++i) {
						ids = ids + arrselections[i].id + ',';
						if (arrselections[i].status == 1) {
							hasIgnoreCount++;
						}
						if (arrselections[i].status == 2) {
							hasCheckCount++;
						}
					}
					if (hasIgnoreCount != 0 || hasCheckCount !=0 ) {
						var text = '';
						if(hasIgnoreCount!=0) {
							text = hasIgnoreCount+'条已忽略报文,';
						}
						if(hasCheckCount!=0) {
							text = text + hasCheckCount + '条已审核报文,';
						}
						swal({
							title : "操作提示",
							text :  "所选报文中存在"+text+"请重新选择!",
							type : "warning",
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确定"
						});
						return;
					}
					//确定报文审批的航班号是否存在重复
					$.post("${pageContext.request.contextPath}/message/checkFlyNo.do",{'ids':ids},function(data){
						//console.log(JSON.stringify(data));
						var noRepeat = true;
						for(var key in data) {
							var len = data[key].length;
							if(len>1) {
								noRepeat = false;
								break;
							}
						}
						if(noRepeat) {
							$.post("${pageContext.request.contextPath}/message/check.do",{'ids':ids},function(data){
								if(data.state=='ok') {
									swal({
										title:'消息提示',
										text: (ids.split(',').length-1)+'条报文审核成功',
										type:'success',
										confirmButtonText : "确定"
									});
									$("#tb_departments").bootstrapTable('refresh');
								}else {
									swal({
										title:'消息提示',
										text: '审核失败',
										type:'error',
										confirmButtonText : "确定"
									});
								}
							});
						}else {
							var msg = [];//记录messageid:flyInfoId
							var html = '';
							for(var key in data) {
								var len = data[key].length;
								if(len==1) {
									//console.log(key);
									var content = {};
									content[key] = data[key][0].id;
									msg.push(content);
								}else if(len>1) {
									html += '<fieldset>';
									for(var j=0 ; j<len ; ++j) {
										var obj = data[key][j];
										html += '<input type="radio" name="' + key + '" data-id="' + obj.id + '">&nbsp;&nbsp;进港航班号:' + obj.incomingFlyNo + " 出港航班号:" + obj.departureFlyNo + " 机型:" + obj.planeType + "<br/>";
									}
									html += '</fieldset><br/>';
								}
							}
							swal({
				                title: "报文中的航班号存在多个，请选择更新",
				                width: "700",
				                html: html,
				                type: "warning",
				                confirmButtonColor: "#DD6B55",
				                confirmButtonText: "确定"
				            }).then(function() {
				               var radios = $("input:radio:checked");
				               for(var i=0 ; i<radios.length ; ++i) {
				            	   var content = {};
				            	   content[$(radios[i]).attr('name')] = $(radios[i]).attr('data-id');
				            	   msg.push(content);
				               }
				               /**审核**/
				               console.log(JSON.stringify(msg));
				               $.post("${pageContext.request.contextPath}/message/checkRepeat.do",{'msg':JSON.stringify(msg)},function(data){
				            	  if(data.state=='ok') {
				            		  alert('审核成功');
				            		  $("#tb_departments").bootstrapTable('refresh');
				            	  }else {
				            		  alert('审核失败');
				            	  }
				               });
				            },function(dismiss){});
						}
					});
				});

				$("#btn_ignore").click(function() {
					var arrselections = $("#tb_departments").bootstrapTable('getSelections');
					if (arrselections.length <= 0) {
						swal({
							title : "操作提示",
							text : "请选择要忽略的报文",
							type : "warning",
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确定"
						});
						return;
					}
					var hasIgnoreCount = 0;
					var hasCheckCount = 0;
					var ids = '';
					for (var i = 0; i < arrselections.length; ++i) {
						ids = ids + arrselections[i].id + ',';
						if (arrselections[i].status == 1) {
							hasIgnoreCount++;
						}
						if (arrselections[i].status == 2) {
							hasCheckCount++;
						}
					}
					if (hasIgnoreCount != 0 || hasCheckCount !=0 ) {
						var text = '';
						if(hasIgnoreCount!=0) {
							text = hasIgnoreCount+'条已忽略报文,';
						}
						if(hasCheckCount!=0) {
							text = text + hasCheckCount + '条已审核报文,';
						}
						swal({
							title : "操作提示",
							text :  "所选报文中存在"+text+"请重新选择!",
							type : "warning",
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确定"
						});
						return;
					}
					//批量忽略报文
					console.log(ids);
					$.post("${pageContext.request.contextPath}/message/ignore.do",{'ids':ids},function(data){
						if(data.state=='ok') {
							swal({
								title:'消息提示',
								text: (ids.split(',').length-1)+'条报文忽略成功',
								type:'success'
							});
							$("#tb_departments").bootstrapTable('refresh');
						}else {
							swal({
								title:'消息提示',
								text: '忽略失败',
								type:'error'
							});
						}
					});
				});
			}
			return oInit;
		};
		function createTableMessage(jsonMessage) {
			var html = ''
			for ( var msg in jsonMessage) {
				var value = '';
				switch (msg) {
				case 'planeType':
					value = jsonMessage.planeType;
					msg = '机型';
					break;
				case 'flyNo':
					value = jsonMessage.flyNo;
					msg = '航班号';
					break;
				case 'preRealFly':
					value = jsonMessage.preRealFly
							+ '';
					value = value.substring(8, 12);
					value = value.substring(0, 2)
							+ ':'
							+ value.substring(2, 4);
					msg = '前站实际起飞';
					break;
				case 'realFly':
					value = jsonMessage.realFly
							+ '';
					value = value.substring(8, 12);
					value = value.substring(0, 2)
							+ ':'
							+ value.substring(2, 4);
					msg = '本站实际起飞';
					break;
				case 'task':
					value = jsonMessage.task;
					msg = '任务';
					break;
				case 'estimatedArrival':
					value = jsonMessage.estimatedArrival
							+ '';
					value = value.substring(8, 12);
					value = value.substring(0, 2)
							+ ':'
							+ value.substring(2, 4);
					msg = '预计到达';
					break;
				case 'incomingProg':
					value = jsonMessage.incomingProg
							+ '';
					msg = '进港状态';
					break;
				case 'departureProg':
					value = jsonMessage.departureProg
							+ '';
					msg = '出港状态';
					break;
				case 'prePlanedFly':
					value = jsonMessage.prePlanedFly
							+ '';
					value = value.substring(8, 12);
					value = value.substring(0, 2)
							+ ':'
							+ value.substring(2, 4);
					msg = '前站计划起飞';
					break;
				case 'planedFly':
					value = jsonMessage.planedFly
							+ '';
					value = value.substring(8, 12);
					value = value.substring(0, 2)
							+ ':'
							+ value.substring(2, 4);
					msg = '本站计划起飞';
					break;
				case 'realArrival':
					value = jsonMessage.realArrival
							+ '';
					value = value.substring(8, 12);
					value = value.substring(0, 2)
							+ ':'
							+ value.substring(2, 4);
					msg = '实际到达时间';
					break;
				case 'planFlightLine':
					value = jsonMessage.planFlightLine;
					msg = '计划航线';
					break;
				case 'messageType':
					value = jsonMessage.messageType;
					msg = '报文类型';
					break;
				case 'incomingFlyNo':
					value = jsonMessage.incomingFlyNo;
					msg = '航班号(进港)';
					break;
				case 'departureFlyNo':
					value = jsonMessage.departureFlyNo;
					msg = '航班号(出港)';
					break;
				case 'alternate':
					value = jsonMessage.alternate;
					msg = '备降站';
					break;
				case 'flightLine':
					value = jsonMessage.flightLine;
					msg = '航线';
					break;
				case 'departureProg':
					value = jsonMessage.departureProg;
					msg = '出港状态';
					break;
				default:
					value = 'value';
					break;
				}
				html += '<tr><td style="text-align: center;">'
						+ msg
						+ '</td><td style="text-align: center;">'
						+ value + '</td></tr>';
			}	
			return html;
		}
		function connect() {
            //var target = 'ws://192.168.21.29:80${pageContext.request.contextPath}/messageReminder';
            var target = 'ws://localhost:8080${pageContext.request.contextPath}/messageReminder';
            if (target == '') {
                alert('Please select server side connection implementation.');
                return;
            }
            if ('WebSocket' in window) {
                ws = new WebSocket(target);
            } else if ('MozWebSocket' in window) {
                ws = new MozWebSocket(target);
            } else {
                alert('WebSocket is not supported by this browser.');
                return;
            }
            ws.onopen = function () {
            };
            ws.onmessage = function (event) {
            	console.log(event.data);
            	if(event.data=='has new message') {
            		$("#tb_departments").bootstrapTable('refresh');
            	}
            };
            ws.onclose = function (event) {
            };
        }
	</script>
</body>
</html>