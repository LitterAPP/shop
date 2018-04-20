/**
 * Created by fish on 2018/4/10.
 */
/**
 * Created by fish on 2018/3/29.
 */
/**
 * Created by fish on 2018/3/29.
 */
var pageSize=15
var weTaoMng = new Vue({
    el: '#weTaoMng',
    data: {
        list:[

        ],
        condition:{
            couponId:'',
            keyword:'',
        },
        mask:false,
        mastText:'',
        total:0,
        pageTotal:0,
        page:1
    },
    watch:{

    },
    computed:{

    },
    methods:{
        goToEdit:function(e){
            var id = e.target.dataset.id
            console.log('get the id is ' , id)
            window.location.href='../func/addWeTao.html?id='+id
        },
        add:function(){
            window.location.href='../func/addWeTao.html'
        },
        getCondition:function(){
            var params = {}
            params.couponId = this.condition.couponId || ''
            params.keyword = this.condition.keyword || ''
            return params
        },
        search:function() {
            var that = this
            var params = this.getCondition()
            that.page=1
            that.listWeTao(params.keyword,that.page,pageSize,false)
        },
        more:function(event){
            var that = this
            var flag = parseInt(event.target.dataset.flag)
            if(flag==1){
                that.page += parseInt(event.target.dataset.flag)
                if(that.page > that.pageTotal){
                    that.page = that.pageTotal
                    return
                }
            }else if(flag==-1  ){
                that.page += parseInt(event.target.dataset.flag)
                if(that.page<=0){
                    that.page=1
                    return ;
                }
            }
            var params = this.getCondition()
            this.listWeTao(params.keyword,that.page,pageSize,false)
        },
        listWeTao: function (keyword,page,pageSize,append) {
            var that = this
            showLoading(that,'请稍后,正在加载微淘列表...')
            $.ajax({
                url:listWeTaoURL,
                type:'POST',
                dataType:'json',
                data:{
                    keyword:keyword,
                    page:page,
                    pageSize:pageSize
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1 && result.data){
                        hideLoading(that)
                        that.total = result.data.total
                        that.pageTotal = result.data.pageTotal
                        if(append){
                            var index = that.list.length-1
                            for(var i in result.data.list){
                                index++
                                that.list.push(result.data.list[i])
                                Vue.set(that.list,index,result.data.list[i])
                            }
                        }else{
                            that.list  = result.data.list
                        }
                    }else{
                        toast(that,'加载列表失败')
                    }


                }
            })
        }
    },
    created:function(){
        console.log('created')
        //检测登录态
        var that = this
        checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
            }else{
                window.parent.location.href='../../html/login.html'
            }
        })
        that.listWeTao('',1,pageSize,false)
    },
})