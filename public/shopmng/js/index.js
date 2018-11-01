/**
 * Created by fish on 2018/3/26.
 */
var frame = new Vue({
    el: '#frame',
    data: {
        contentSrc: '../html/wellcome.html',
        userName: ''
    },
    computed: {
        innerHeight: function () {
            return window.innerHeight
        },
        innerWidth: function () {
            return window.innerWidth
        }
    },
    methods: {},
    created: function () {
        console.log('created')
        //检测登录态
        var that = this
        checkLogin(function (result) {
            if (result && result.code == 1) {
                console.log('登录态校验成功.')
                that.userName = result.data.userName
            } else {
                window.parent.location.href = './login.html'
            }
        })
    },
    beforeMount: function () {
        console.log('beforeMount')
    },
    mounted: function () {
        console.log('mounted')

    },
    beforeUpdate: function () {
        console.log('beforeUpdate')
    },
    updated: function () {
        console.log('updated')

    }
    ,
    beforeDestroy: function () {
        console.log('beforeDestroy')
    },
    destroyed: function () {
        console.log('destroyed')
    }
})
