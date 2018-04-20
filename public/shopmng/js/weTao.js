/**
 * Created by fish on 2018/4/18.
 */
/**
 * Created by fish on 2018/3/26.
 */
var pageSize=10
var weTao = new Vue({
    el: '#weTao',
    data: {
        list:[

        ],
        mask:false,
        maskText:'',
        total:0,
        pageTotal:0,
        page:1,
        top:false,
        bottom:false
    },
    computed: {

    },
    methods:{
        getDetail:function(e){
            var id = e.currentTarget.dataset.id
            var url = weTaoDetailUrl+"?id="+id
            window.location.href=url
        },
        zan:function(e){
            var id = e.currentTarget.dataset.id
            this.listWeTao(1,pageSize,false,id);
        },
        handleScroll:function() {
            var that = this
            var scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
            if(scrollTop==0 && !this.top){
                console.log('到顶部了')
                this.top = true
                this.listWeTao(1,pageSize,false)
            }else if(scrollTop>=$(document).height()-$(window).height()){
                console.log('到底部了')
                that.page += parseInt(1)
                if(that.page > that.pageTotal){
                    that.page = that.pageTotal
                    return
                }
                that.bottom=true
                that.listWeTao(that.page,pageSize,true)
            }
        },
        goBackMinapp:function(){
            if(window.__wxjs_environment === 'miniprogram'){
                wx.miniProgram.navigateBack()
            }else{
                window.history.back()
            }
        },
        listWeTao: function (page,pageSize,append,id) {
            var that = this
            //showLoading(that,'请稍后,正在加载微淘列表...')
            $.ajax({
                url:listWeTaoForUserURL,
                type:'POST',
                dataType:'json',
                data:{
                    page:page,
                    pageSize:pageSize,
                    id:id||0
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1 && result.data){
                       //hideLoading(that)
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
                        setTimeout(function () {
                            that.top=false
                            that.bottom=false
                        },2000)

                    }else{
                        toast(that,'加载列表失败')
                    }
                }
            })
        }
    },
    created:function(){
        var that = this
        console.log(window.__wxjs_environment)
        if(window.__wxjs_environment === 'miniprogram'){

        }else{
            toast(this,"请使用小程序访问")
        }
        that.listWeTao(1,pageSize,false)
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
