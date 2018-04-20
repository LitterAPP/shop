/**
 * Created by fish on 2018/4/18.
 */
/**
 * Created by fish on 2018/3/26.
 */
var pageSize=10
var weTaoDetail = new Vue({
    el: '#weTaoDetail',
    data: {
        zan:0,
        isZan:false,
        mask:false,
        maskText:'',
    },
    computed: {

    },
    methods:{
        zanDetail:function(e){
            var id = e.currentTarget.dataset.id
            var that = this
            //showLoading(that,'请稍后,正在加载微淘列表...')
            $.ajax({
                url:zanOnDetailPageURL,
                type:'POST',
                dataType:'json',
                data:{
                    id:id||0
                },
                success:function(result){
                    console.log('--->',result)
                    if(result && result.code==1 && result.data){
                        that.zan = result.data.zan
                        that.isZan = result.data.isZan
                    }else{
                        toast(that,'点赞失败')
                    }
                }
            })
        },
        goBackMinapp:function(){
            window.history.back()
        }
    },
    created:function(){
        var that = this
        console.log(window.__wxjs_environment)
        if(window.__wxjs_environment === 'miniprogram'){

        }else{

        }

    },
    beforeMount:function(){
        console.log('beforeMount')
    },
    mounted:function(){
        window.addEventListener('scroll', this.handleScroll)
    },
    beforeUpdate:function(){
        console.log('beforeUpdate')
    },
    updated:function(){
        console.log('updated')

    }
    ,
    beforeDestroy:function(){
        console.log('beforeDestroy')
    },
    destroyed:function(){
        window.removeEventListener('scroll', this.handleScroll)
    }
})
