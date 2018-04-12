/**
 * Created by fish on 2018/3/29.
 */
/**
 * Created by fish on 2018/3/29.
 */
var pageSize=15
var pCategoryId='',subCategoryId=''
var orderMng = new Vue({
    el: '#orderMng',
    data: {
        list:[

        ],
        condition:{
            orderId:'',
            keyword:'',
            status:{
                selected:'-1',
                options:[
                    {text:'支付中',value:'0'},
                    {text:'支付成功:待发货',value:'1'},
                    {text:'支付取消',value:'2'},
                    {text:'支付失败',value:'3'},
                    {text:'支付完成:拼团中',value:'4'},
                    {text:'退款成功',value:'5'},
                    {text:'拼团成功:待发货',value:'6'},
                    {text:'已投递:已揽件',value:'7'},
                    {text:'已签收',value:'8'},
                    {text:'已确认收货',value:'9'}
                ]
            }
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
        getCondition:function(){
            var params = {}
            params.orderId = this.condition.orderId || ''
            params.keyword = this.condition.keyword || ''
            params.status = this.condition.status.selected||''
            return params
        },
        search:function() {
            var that = this
            var params = this.getCondition()
            that.page=1
            that.listOrder(params.orderId,params.keyword,params.status,that.page,pageSize,false)
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
            this.listOrder(params.orderId,params.keyword,params.status,that.page,pageSize,false)
},
        listOrder: function (orderId,keyword,status,page,pageSize,append) {
            var that = this
            showLoading(that,'请稍后,正在加载订单列表...')
            $.ajax({
                url:listOrderURL,
                type:'POST',
                dataType:'json',
                data:{
                    orderId:orderId,
                    keyword:keyword,
                    status:status,
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
                        toast(that,'加载订单列表失败')
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
        that.listOrder('','',-1,1,pageSize,false)
    },
})