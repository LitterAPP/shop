/**
 * Created by fish on 2018/4/18.
 */
/**
 * Created by fish on 2018/3/26.
 */
var pageSize = 10, shopId = ''
var weTao = new Vue({
    el: '#weTao',
    data: {
        list: [],
        mask: false,
        maskText: '',
        total: 0,
        pageTotal: 0,
        page: 1,
        top: false,
        bottom: false,
        nomore: false
    },
    computed: {},
    methods: {
        getDetail: function (e) {
            var id = e.currentTarget.dataset.id
            var url = weTaoDetailUrl + "?id=" + id
            window.location.href = url
        },
        zan: function (e) {
            var id = e.currentTarget.dataset.id
            this.listWeTao(1, pageSize, false, id);
        },
        handleScroll: function () {
            var that = this
            var scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
            if (scrollTop == 0 && !this.top) {
                console.log('到顶部了')
                this.top = true
                that.nomore = false
                that.bottom = false
                that.page = 1
                this.listWeTao(1, pageSize, false)
            } else if (scrollTop >= $(document).height() - $(window).height()) {

                console.log('到底部了-bottom', that.bottom)
                if (that.bottom) {
                    return
                }
                that.page += parseInt(1)
                console.log('到底部了-->page', that.page)
                if (that.page <= that.pageTotal) {
                    that.nomore = false
                    that.bottom = true
                    that.listWeTao(that.page, pageSize, true)
                } else {
                    that.page = that.pageTotal
                    that.nomore = true
                    that.bottom = false
                }

            }
        },
        goBackMinapp: function () {
            if (window.__wxjs_environment === 'miniprogram') {
                wx.miniProgram.reLaunch({
                    url: '/pages/shop/shopIndex'
                })
            } else {
                window.history.back()
            }
        },
        listWeTao: function (page, pageSize, append, id) {
            var that = this
            //showLoading(that,'请稍后,正在加载微淘列表...')
            $.ajax({
                url: listWeTaoForUserURL,
                type: 'POST',
                dataType: 'json',
                data: {
                    page: page,
                    pageSize: pageSize,
                    id: id || 0,
                    shopId: shopId
                },
                success: function (result) {
                    console.log(result)
                    if (result && result.code == 1 && result.data) {
                        //hideLoading(that)
                        that.total = result.data.total
                        that.pageTotal = result.data.pageTotal
                        if (append) {
                            var index = that.list.length - 1
                            for (var i in result.data.list) {
                                index++
                                that.list.push(result.data.list[i])
                                Vue.set(that.list, index, result.data.list[i])
                            }
                        } else {
                            that.list = result.data.list
                        }
                        setTimeout(function () {
                            that.top = false
                            that.bottom = false
                        }, 2000)

                    } else {
                        toast(that, '加载列表失败')
                    }
                }
            })
        }
    },
    created: function () {
        var that = this
        shopId = urlTools.getUrlParam("shopId")
        console.log(window.__wxjs_environment)
        if (window.__wxjs_environment === 'miniprogram') {

        } else {
            toast(this, "请使用小程序访问")
        }
        that.listWeTao(1, pageSize, false)
    },
    beforeMount: function () {
        console.log('beforeMount')
    },
    mounted: function () {
        window.addEventListener('scroll', this.handleScroll)
        var that = this


    },
    beforeUpdate: function () {
        console.log('beforeUpdate')


    },
    updated: function () {
        $("a").off('click').on('click', function (e) {
            return false;
        });
    }
    ,
    beforeDestroy: function () {
        console.log('beforeDestroy')
    },
    destroyed: function () {
        window.removeEventListener('scroll', this.handleScroll)
    }
})
