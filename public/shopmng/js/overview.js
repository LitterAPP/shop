/**
 * Created by fish on 2018/4/27.
 */
var overview = new Vue({
    el: '#overview',
    data: {
        today:{
            orderCount:0,
            orderTogetherCount:0,
            productCount:0,
            deliverCount:0,
            orderAmountSum:0
        },
        yestoday:{
            orderCount:0,
            orderTogetherCount:0,
            productCount:0,
            deliverCount:0,
            orderAmountSum:0
        }
    },
    computed: {

    },
    methods:{

    },
    created:function(){
        var that = this
        console.log('created')
        $.ajax({
            url:dataOverViewUrl,
            type:'POST',
            dataType:'json',
            data:{
                flag:1,
                beginTime:'',
                endTime:''
            },
            success:function(result){
                var tmp={
                    orderCount:0,
                    orderTogetherCount:0,
                    productCount:0,
                    deliverCount:0,
                    orderAmountSum:0
                }
                tmp.deliverCount = result.data.deliverCount[0]
                tmp.orderCount = result.data.orderCount[0]
                tmp.orderTogetherCount = result.data.orderTogetherCount[0]
                tmp.productCount = result.data.productCount[0]
                tmp.orderAmountSum = result.data.orderAmountSum[0]
                that.today = tmp

            }
        })
        $.ajax({
            url:dataOverViewUrl,
            type:'POST',
            dataType:'json',
            data:{
                flag:2,
                beginTime:'',
                endTime:''
            },
            success:function(result){
                var tmp={
                    orderCount:0,
                    orderTogetherCount:0,
                    productCount:0,
                    deliverCount:0,
                    orderAmountSum:0
                }
                tmp.deliverCount = result.data.deliverCount[0]
                tmp.orderCount = result.data.orderCount[0]
                tmp.orderTogetherCount = result.data.orderTogetherCount[0]
                tmp.productCount = result.data.productCount[0]
                tmp.orderAmountSum = result.data.orderAmountSum[0]
                that.yestoday = tmp
            }
        })
    },
    beforeMount:function(){
        console.log('beforeMount')
    },
    mounted:function(){
        console.log('mounted')

    },
    beforeUpdate:function(){
        console.log('beforeUpdate')
    },
    updated:function(){
        console.log('updated')
    },
    beforeDestroy:function(){
        console.log('beforeDestroy')
    },
    destroyed:function(){
        console.log('destroyed')
    }
})