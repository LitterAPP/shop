/**
 * Created by fish on 2018/4/2.
 */
var couponId
var addCoupon = new Vue({
    el: '#addCoupon',
    data: {
        couponId: '',
        couponName: '',
        amount: '',
        limitProductChecked: false,
        limitProductId: '',
        limitPriceChecked: false,
        limitPrice: '',
        limitTimes: '',
        startTime: '',
        endTime: '',
        expireTime: '',
        mask: false
    },
    methods: {
        getOneCoupon: function () {
            var that = this
            showLoading(that, '请稍后,正在加载代金券...')
            $.ajax({
                url: listCouponURL,
                type: 'POST',
                dataType: 'json',
                data: {
                    couponId: couponId,
                    keyword: '',
                    page: 1,
                    pageSize: 1
                },
                success: function (result) {
                    if (result && result.code == 1 && result.data && result.data.list && result.data.list.length == 1) {
                        hideLoading(that)
                        var coupon = result.data.list[0]
                        that.couponId = coupon.couponId
                        that.couponName = coupon.couponName
                        that.amount = coupon.amount
                        that.limitProductId = coupon.limitProductId
                        that.limitProductChecked = coupon.limitProductId ? true : false
                        that.limitPrice = coupon.limitPrice
                        that.limitPriceChecked = coupon.limitPrice ? true : false
                        that.limitTimes = coupon.limitTimes
                        $("#startTimePicker").val(coupon.startTime)
                        $("#endTimePicker").val(coupon.endTime)
                        $("#expireTimePicker").val(coupon.expireTime)
                    } else {
                        toast(that, '加载代金券失败')
                    }
                }
            })
        },
        startTimeChanged: function (e) {
            console.log('startTime changed.')
        },
        goBack: function () {
            window.history.back()
        },
        saveCoupon: function () {
            if (stringEmpty(this.couponName)) {
                toast(this, '代金券名称不能为空')
                return
            }
            if (stringEmpty(this.amount)) {
                toast(this, '代金券面额不能为空')
                return
            }
            if (stringEmpty(this.limitTimes)) {
                toast(this, '领取次数限制不能为空')
                return
            }
            if (!this.limitProductChecked) {
                this.limitProductId = ''
            } else if (stringEmpty(this.limitProductId)) {
                toast(this, '商品ID不能为空')
                return
            }
            if (!this.limitPriceChecked) {
                this.limitPrice = ''
            } else if (stringEmpty(this.limitPrice)) {
                toast(this, '限制额度不能为空')
                return
            }
            this.startTime = $("#startTimePicker").val()
            if (stringEmpty(this.startTime)) {
                toast(this, '开始时间不能为空')
                return
            }
            this.endTime = $("#endTimePicker").val()
            if (stringEmpty(this.endTime)) {
                toast(this, '结束时间不能为空')
                return
            }
            this.expireTime = $("#expireTimePicker").val()
            if (stringEmpty(this.expireTime)) {
                toast(this, '过期时间不能为空')
                return
            }
            var that = this
            $.ajax({
                url: addCouponURL,
                type: 'POST',
                dataType: 'json',
                data: {
                    couponId: that.couponId,
                    couponName: that.couponName,
                    amount: that.amount,
                    limitProductId: that.limitProductId,
                    limitPrice: that.limitPrice,
                    limitTimes: that.limitTimes,
                    startTime: that.startTime,
                    endTime: that.endTime,
                    expireTime: that.expireTime
                },
                success: function (result) {
                    if (result && result.code == 1) {
                        that.goBack()
                    } else {
                        toast(that, '保存失败,' + result.msg)
                    }
                }
            })
        },
    },
    created: function () {
        console.log('created')
        //检测登录态
        couponId = urlTools.getUrlParam("couponId")
        checkLogin(function (result) {
            if (result && result.code == 1) {
                console.log('登录态校验成功.')
            } else {
                window.parent.location.href = '../../html/login.html'
            }
        })
        if (couponId) {
            this.getOneCoupon()
        }
    },
})