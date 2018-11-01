/**
 * Created by fish on 2018/3/29.
 */
/**
 * Created by fish on 2018/3/29.
 */
var pageSize = 15
var wxRefundorderMng = new Vue({
    el: '#wxRefundorderMng',
    data: {
        list: [],
        condition: {
            transaction_id: '',
            refund_id: '',
            out_refund_no: '',
            out_trade_no: ''
        },
        mask: false,
        mastText: '',
        total: 0,
        pageTotal: 0,
        page: 1
    },
    watch: {},
    computed: {},
    methods: {
        getCondition: function () {
            var params = {}
            params.transaction_id = this.condition.transaction_id || ''
            params.refund_id = this.condition.refund_id || ''
            params.out_refund_no = this.condition.out_refund_no || ''
            params.out_trade_no = this.condition.out_trade_no || ''
            return params
        },
        search: function () {
            var that = this
            var params = this.getCondition()
            that.page = 1
            that.listOrder(params, that.page, pageSize, false)
        },
        more: function (event) {
            var that = this
            var flag = parseInt(event.target.dataset.flag)
            if (flag == 1) {
                that.page += parseInt(event.target.dataset.flag)
                if (that.page > that.pageTotal) {
                    that.page = that.pageTotal
                    return
                }
            } else if (flag == -1) {
                that.page += parseInt(event.target.dataset.flag)
                if (that.page <= 0) {
                    that.page = 1
                    return;
                }
            }
            var params = this.getCondition()
            this.listOrder(params, that.page, pageSize, false)
        },
        listOrder: function (params, page, pageSize, append) {
            var that = this
            showLoading(that, '请稍后,正在加载订单列表...')
            $.ajax({
                url: listRefundOrderURL,
                type: 'POST',
                dataType: 'json',
                data: {
                    transactionId: params.transaction_id || '',
                    outTradeNo: params.out_trade_no || '',
                    refundId: params.refund_id || '',
                    outRefundNo: params.outRefundNo || '',
                    page: page,
                    pageSize: pageSize
                },
                success: function (result) {
                    console.log(result)
                    if (result && result.code == 1 && result.data) {
                        hideLoading(that)
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
                    } else {
                        toast(that, '加载退款订单列表失败')
                    }


                }
            })
        }
    },
    created: function () {
        console.log('created')
        //检测登录态
        var that = this
        checkLogin(function (result) {
            if (result && result.code == 1) {
                console.log('登录态校验成功.')
            } else {
                window.parent.location.href = '../../html/login.html'
            }
        })
        that.listOrder({}, that.page, pageSize, false)
    },
})