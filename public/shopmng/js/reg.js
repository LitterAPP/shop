/**
 * Created by fish on 2018/3/29.
 */
var timeHandler
var regMng = new Vue({
    el: '#regMng',
    data: {
        mobile: '',
        code: '',
        userName: '',
        password: '',
        repassword: '',
        errorMsg: '',
        mask: false,
        maskText: '',
        disable: false,
        regButtonText: '60秒后重发'
    },
    methods: {
        login: function () {
            window.location.href = '../html/login.html'
        },
        sendSms: function () {
            var that = this
            if (!checkMobile(that.mobile)) {
                toast(that, '请输入正确的手机号码')
                return
            }
            that.disable = true
            toast(that, '短信验证码发送中...')
            $.ajax(
                {
                    url: smsSendUrl,
                    type: "POST",
                    dataType: "json",
                    data: {
                        "mobile": that.mobile
                    },
                    success: function (result) {
                        if (result && result.code == 1) {
                            toast(that, '验证码已下发，请留意手机短信')
                            that.time = 60
                            timeHandler = setInterval(function () {
                                that.time--
                                if (that.time > 0) {
                                    that.regButtonText = that.time + '秒后重发'
                                } else {
                                    that.regButtonText = '发送验证码'
                                    that.disable = false
                                    clearInterval(timeHandler)
                                }
                            }, 1000)
                        } else {
                            that.errorMsg = result.msg
                            toast(that, '发送短信失败:' + result.msg)
                        }
                    }
                });
        },
        validteCode: function (event) {
            var that = this
            showLoading(that, '处理中，请稍后...')
            if (stringEmpty(that.userName)) {
                toast(that, '用户名不能为空')
                return
            }

            if (stringEmpty(that.userName)) {
                toast(that, '登录用户名不能为空')
                return
            }

            if (stringEmpty(that.password)) {
                toast(that, '登录密码不能为空')
                return
            }

            if (stringEmpty(that.repassword)) {
                toast(that, '登录密码不能为空')
                return
            }

            if (that.repassword != that.password) {
                toast(that, '两次密码输入不一致')
                return
            }

            if (stringEmpty(that.mobile)) {
                toast(that, '手机号码不能为空')
                return
            }

            if (stringEmpty(that.code)) {
                toast(that, '短信验证码不能为空')
                return
            }


            $.ajax(
                {
                    url: regUrl,
                    type: "POST",
                    dataType: "json",
                    data: {
                        "userName": that.userName,
                        "password": that.password,
                        "repassword": that.repassword,
                        "mobile": that.mobile,
                        "code": that.code
                    },
                    success: function (result) {
                        if (result && result.code == 1) {
                            toast(that, '注册成功:登录即可获取小程序访问二维码')
                            setTimeout(function () {
                                window.location.href = '../html/login.html'
                            }, 3000)
                        } else {
                            that.errorMsg = result.msg
                            toast(that, '' + result.msg)
                        }
                    }
                });
        }
    }
    ,
    created: function () {
        console.log('created')
    },
    beforeDestroy: function () {
        console.log('destoryed')
        clearInterval(timeHandler)
    }
})
