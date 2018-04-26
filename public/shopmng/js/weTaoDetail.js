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
        shopId:'',
        mask:false,
        maskText:'',
    },
    computed: {

    },
    methods:{
        viewComments:function(e){
            var wetaoid = e.currentTarget.dataset.wetaoid
            var url = '/pages/wetao/comment?wetaoid='+wetaoid
            if(window.__wxjs_environment === 'miniprogram'){
                wx.miniProgram.reLaunch({
                    url: url
                })
            }else{
                console.log(url)
                toast(this,'请前往小程序操作')
            }
        },
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
            var url = '/pages/shop/webview?url='+encodeURIComponent(domain+'shopmng/app/weTao.html')
            if(window.__wxjs_environment === 'miniprogram'){
                wx.miniProgram.reLaunch({
                    url: url
                })
            }else{
                window.history.back()
            }
        }
    },
    created:function(){
        var that = this
        console.log('created')
        if(window.__wxjs_environment === 'miniprogram'){

        }else{

        }

    },
    beforeMount:function(){
        console.log('beforeMount')
    },
    mounted:function(){
        console.log('mounted')
        window.addEventListener('scroll', this.handleScroll)

        var that = this
        var minapp = window.__wxjs_environment === 'miniprogram'

        $("a").off('click').on('click',function(e){

            var href =$(this).attr('href');
            //带上商铺ID
            if(href && href.indexOf('?')>0){
                href = href+'&shopId='+that.shopId
            }else{
                href = href+'?shopId='+that.shopId
            }
            console.log('1111href',href)
            if(!minapp &&  href.startsWith('minapp:')){
                toast(that,'请使用小程序访问')
                return false
            }
            if(minapp && href.startsWith('minapp:')){
                var minAppUrl =  href.substr(7)
                wx.miniProgram.reLaunch({
                    url: minAppUrl
                })
                return false
            }else if(!minapp &&  href.startsWith('http')){
                window.location.href=href
                return false
            }
            return false;
        });
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
