$(function() {

	// 1.初始化Table
	var oTable = new TableInit();
	oTable.Init();

	// 2.初始化Button的点击事件
	var oButtonInit = new ButtonInit();
	oButtonInit.Init();

});

var TableInit = function() {
	var oTableInit = new Object();
	// 初始化Table
	oTableInit.Init = function() {
		$('#tb_departments')
				.bootstrapTable(
						{
							url : '${pageContext.request.contextPath}/flyinfo/getFlyInfoList.do', // 请求后台的URL（*）
							method : 'post', // 请求方式（*）
							toolbar : '#toolbar', // 工具按钮用哪个容器
							striped : true, // 是否显示行间隔色
							cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
							pagination : true, // 是否显示分页（*）
							sortable : false, // 是否启用排序
							sortOrder : "asc", // 排序方式
							// queryParams : oTableInit.queryParams,//传递参数（*）
							sidePagination : "client", // 分页方式：client客户端分页，server服务端分页（*）
							pageNumber : 1, // 初始化加载第一页，默认第一页
							pageSize : 10, // 每页的记录行数（*）
							pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
							search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
							strictSearch : false,// 严格搜索模式
							showColumns : true, // 是否显示所有的列
							showRefresh : true, // 是否显示刷新按钮
							minimumCountColumns : 2, // 最少允许的列数
							clickToSelect : true, // 是否启用点击选中行
							// height : 500,
							// //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
							uniqueId : "index", // 每一行的唯一标识，一般为主键列
							showToggle : true, // 是否显示详细视图和列表视图的切换按钮
							cardView : false, // 是否显示详细视图
							detailView : false, // 是否显示父子表
							rowStyle : function(row, index) {
								var strclass = "";
								if (row.state == 1) {
									strclass = 'success';// 还有一个active
								} else if (row.state == 0) {
									strclass = 'danger';
								} else {
									return {};
								}
								return {
									classes : strclass
								}
							},
							onEditableSave : function(field, row, oldValue, $el) {

								var str = JSON.stringify(row);
								$
										.ajax({
											type : "post",
											url : '${pageContext.request.contextPath}/flyinfo/updateFlyInfo.do',
											data : {
												"flyInfoStr" : str
											},
											success : function(data) {
												// alert("编辑成功");
												console.log(data);
											},
											error : function() {
												alert("Error");
											},
											complete : function() {

											}

										});
							},
							columns : [ {
								checkbox : true
							}, {
								field : 'index',
								title : '次序',
								align : 'center',
								formatter : function(value, row, index) {
									return index + 1; // 返回行号
								}
							}, {
								field : 'task',
								title : '性质',
								align : 'center',
								editable : true
							}, {
								field : 'planeType',
								title : '机型',
								align : 'center',
								editable : true
							}, {
								field : 'planeNo',
								title : '机号',
								align : 'center',
								editable : true
							}, {
								field : 'incomingFlyNo',
								title : '进港航班号',
								align : 'center',
								editable : true
							}, {
								field : 'departureFlyNo',
								title : '出港航班号',
								align : 'center',
								editable : true
							}, {
								field : 'planedFly',
								title : '预计起飞',
								align : 'center',
								editable : true
							}, {
								field : 'flightLine',
								title : '动态',
								align : 'center',
								editable : true
							}, {
								field : 'remark',
								title : '备注',
								align : 'center',
								editable : true
							}, {
								field : 'state',
								title : '状态',
								align : 'center',
								formatter : function(value, row, index) {
									var str = '';
									if (value == 0) {
										str = '未审核';
									} else if (value == 1) {
										str = '已审核';
									} else {
										str = '未知状态';
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
		// 初始化页面上面的按钮事件
	};

	return oInit;
};