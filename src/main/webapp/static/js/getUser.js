//获取用户的数据、设置到页面的hidden上
$(function () {
    $.ajax({
        url: path + "/getUser.do",
        type: "get",
        async: false,
        data: {},
        success: function (responseText) {

            if (responseText.user != null) {
                $("#loginDiv div h1").html("<font color='red'>" + responseText.user.username + "</font>");
                $("#loginDiv").attr("href", "");

                $("#registerDiv").attr("href", path + "/logout.do");
                $("#registerDiv div h1").html("注销");

                //页面获取用户的数据
                $("#userNickname").val(responseText.user.username);
                $("#userId").val(responseText.user.userId);


                $("#userEmail").val(responseText.user.email);
            }
        },
        error: function () {
            alert("系统错误");
        }
    })
});
