/**
 * Created by fish on 2018/3/29.
 */
var nav = new Vue({
    el: '#nav',
    data: {
       list:[
           {title:'店铺',url:'../html/func/shopMng.html',activite:false},
           {title:'微淘',url:'../html/func/weTaoMng.html',activite:false},
           {title:'分类',url:'../html/func/categoryMng.html',activite:false},
           {title:'商品',url:'../html/func/productMng.html',activite:false},
           {title:'订单',url:'../html/func/orderMng.html',activite:false},
           {title:'发货',url:'../html/func/deliverMng.html',activite:false},
           {title:'优惠券',url:'../html/func/couponMng.html',activite:false},
       ],
        userName:''
    },
    methods:{
        navigation:function(event){
            var idx = event.target.dataset.idx
            for(var i in this.list){
                this.list[i].activite = false
            }
            var currentItem = this.list[idx]
            currentItem.activite=true
            $("#contentFrame",parent.document).attr('src',currentItem.url)
        },
        quitLogin:function(){
            console.log('quitLogin clicked')
            $.ajax(
                {
                    url:quitLoginURL,
                    type : "POST",
                    dataType:"json",
                    data : {},
                    success:function(result){
                        console.log('quitLogin clicked ',result)
                        if(result && result.code==1){
                            console.log('退出登录成功.')
                            window.parent.location.href='../../html/login.html'
                        }else{
                            alert('退出失败')
                        }
                    }
                });
        }
    },
    created:function(){
        console.log('created')
        //检测登录态
        var that = this
        checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
                    that.userName = result.data.userName
            }else{
                window.parent.location.href='../../html/login.html'
            }
        })
    },
})