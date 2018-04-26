/**
 * Created by fish on 2018/3/29.
 */
var loginMg = new Vue({
    el: '#loginMng',
    data: {
        userName: '',
        password:'',
        errorMsg:'',
        mask:false,
        maskText:''
    },
    methods:{
        reg:function(event){
            window.location.href='../html/reg.html'
        },
        login:function(event){
            var that = this
            if(stringEmpty(that.userName)){
                toast(that,'请输入登录用户名')
                return
            }
            if(stringEmpty(that.password)){
                toast(that,'请输入登录密码')
                return
            }
            showLoading(that,'登录中...')
            console.log('username',that.userName,'password',that.password)
            $.ajax(
                {
                    url:loginURL,
                    type : "POST",
                    dataType:"json",
                    data : {
                        "userName" : that.userName,
                        "password": that.password
                    },
                    success:function(result){
                        if(result && result.code==1){
                            window.location.href='../html/index.html'
                        }else{
                            that.errorMsg = result.msg
                            toast(that,'登录失败:'+ result.msg)
                        }
                    }
                });

        }
    }
    ,
    created:function(){
        console.log('created')
        //检测登录态
        checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
                window.parent.location.href='../html/index.html'
            }else{

            }
        })
    },
})
