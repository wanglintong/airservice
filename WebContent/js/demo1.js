(function(window) {
	
	$.ajax({
		url : '/sssp/user/findUserById.do',
		type : 'post',
		contentType:'application/json;charset=utf-8',
		data : null,
		success : function(data){
			var msg = JSON.stringify(data);
			alert(msg);
		},
		fail:function(){
			alert("加载车牌号失败...");
		}
	});
	
})(window);