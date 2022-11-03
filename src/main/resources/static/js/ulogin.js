
const btn = document.getElementById("lo");
btn.onclick = function (){
    $.ajax({
        url: "http://localhost:8080/login",
        type: "post",
        data: $('#login_form').serialize(),
        dataType: "text",
/*        success: function (token){
            localStorage.setItem("token",token);
            location.href = "http://localhost:8080/index"/!* + localStorage.getItem('token')*!/;
/!*                var token1 = "Bearer " + localStorage.getItem('token');
                $.ajax({
                    url: "http://localhost:8080/index.html",
                    type: "get",
                    dataType: "json",
                    async: true,
                    //headers:{"Authorization":token},
                    beforeSend: function (request){
                        request.setRequestHeader('Authorization',token1);
                        location.href = "http://localhost:8080/index.html";
                    },
                });*!/
        }*/
    });
}


function show(){
    var password = document.getElementById("password");
    var icon = document.querySelector(".fas")
    // ========== Checking type of password ===========
    if(password.type === "password"){
    password.type = "text";
    }
    else {
    password.type = "password";
    }
};
