$(function(){
    // 进入页面自动加载首页
    $(".mb_nav").css('display', 'none');
    $("#wrapper").load("../page/welcome.html");

    // 点击首页标题出现首页页面
    $(".title").on("click",function(){
        $(".mb_nav").css('display', 'none');
        var url = $(this).attr("url");
        $(".left_nav>li").removeClass("current");
        $("#wrapper").load("../page/welcome.html");
    })

    // 点击列表时跳转页面
    $("dl.subNav").on("click","dd",function(){
        $(".mb_nav").css('display', 'block');
        // 获取a标签的值进行判断
        switch($(this).find("a").text()){
            case "小区列表":
                $("span#span_c").text("小区列表");
                break;
            case "员工管理":
                $("span#span_c").text("员工管理");
                break;
            case "分类管理":
                $("span#span_c").text("分类管理");
                break;
            case "产品管理":
                $("span#span_c").text("产品管理");
                break;
            case "订单管理":
                $("span#span_c").text("订单管理");
                break;
            case "评论管理":
                $("span#span_c").text("评论管理");
                break;
            case "地址管理":
                $("span#span_c").text("地址管理");
                break;
        }
        var url = $(this).attr("url");
        $(".left_nav>li").removeClass("current");
        $(this).addClass("current");
        $("#wrapper").load(url);
    })
});

//左侧菜单的一二级菜单切换
window.onload = function () {
    var ul = document.getElementById('zome').getElementsByTagName('dt');
    for (var i = 0; i < ul.length; i++) {
        ul[i].onclick = function () {
            var nex = this.parentNode.getElementsByTagName('dl')[0]
            if (nex.style.display != 'block') {
                nex.style.display = 'block'
            } else {
                nex.style.display = 'none'
            }
        }
    }
}
