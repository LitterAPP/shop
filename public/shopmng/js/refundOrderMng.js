/**
 * Created by fish on 2018/3/29.
 */
/**
 * Created by fish on 2018/3/29.
 */
var pageSize = 15
var pCategoryId = '', subCategoryId = ''
var refundOrderMng = new Vue({
    el: '#refundOrderMng',
    data: {
        list: [],
        condition: {
            orderId: '',
            productName: '',
            status: {
                selected: '-5',
                options: [
                    {text: '全部退款订单', value: '-5'},
                    {text: '退款成功', value: '5'},
                    {text: '退款处理失败', value: '55555'},
                    {text: '退款未审核通过', value: '5555'},
                    {text: '退款审核中', value: '555'},
                    {text: '退款中', value: '55'},
                ]
            }
        },
        auditStatus: [
            {text: '审核通过', value: '55'},
            {text: '拒绝退款', value: '5555'}
        ],
        auditStatusSelected: '55',
        refundFee: '0.00',
        auditMemo: '',
        refundOrder: {},
        currentMemo: '',
        showMemo: false,
        refundPrompt: false,
        mask: false,
        mastText: '',
        total: 0,
        pageTotal: 0,
        page: 1
    },
    watch: {},
    computed: {},
    methods: {
        closeMemo: function () {
            var that = this
            that.showMemo = false
        },
        refundCancel: function () {
            var that = this
            that.refundPrompt = false
            that.refundOrder = {}
        },
        refundOK: function () {
            var that = this

            if (stringEmpty(that.refundFee)) {
                toast(that, '请输入退款金额')
                return;
            }

            if (stringEmpty(that.auditMemo)) {
                toast(that, '请输入审核备注信息')
                return;
            }


            if (parseFloat(that.refundFee) > parseFloat(that.refundOrder.totapPay)) {
                toast(that, '退款额度不能大于订单总金额')
                return;
            }
            showLoading(that, '审核提交中,请稍后...')
            $.ajax({
                url: refundAuditURL,
                type: 'POST',
                dataType: 'json',
                data: {
                    orderId: that.refundOrder.orderId,
                    refundFee: that.refundFee,
                    memo: that.auditMemo,
                    auditStatus: that.auditStatusSelected
                },
                success: function (result) {
                    console.log('refund', result)
                    if (result && result.code == 1) {
                        that.refundPrompt = false
                        that.search()
                        hideLoading(that)
                    } else {
                        toast(that, '审核操作失败')
                    }
                }
            })

        },
        refund: function (e) {
            var that = this
            that.refundOrder = that.list[e.target.dataset.idx]
            that.refundFee = that.refundOrder.totalPay
            console.log('selected order', that.refundOrder)
            that.refundPrompt = true
        },
        getCondition: function () {
            var params = {}
            params.orderId = this.condition.orderId || ''
            params.keyword = this.condition.keyword || ''
            params.status = this.condition.status.selected || ''
            return params
        },
        search: function () {
            var that = this
            var params = this.getCondition()
            that.page = 1
            that.listOrder(params.orderId, params.keyword, params.status, that.page, pageSize, false)
        },
        exportData: function () {
            var that = this
            var params = this.getCondition()
            window.location.href = exportOrderURL + '?orderId=' + params.orderId + '&keyword=' + params.keyword + '&startTime=' + $("#startTimePicker").val() + '&endTime=' + $("#endTimePicker").val() + '&status=' + params.status

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
            this.listOrder(params.orderId, params.keyword, params.status, that.page, pageSize, false)
        },
        listOrder: function (orderId, keyword, status, page, pageSize, append) {
            var that = this
            showLoading(that, '请稍后,正在加载订单列表...')
            $.ajax({
                url: listOrderURL,
                type: 'POST',
                dataType: 'json',
                data: {
                    orderId: orderId,
                    keyword: keyword,
                    status: status,
                    startTime: $("#startTimePicker").val(),
                    endTime: $("#endTimePicker").val(),
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
                        toast(that, '加载订单列表失败')
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
        that.listOrder('', '', that.condition.status.selected, 1, pageSize, false)
    },
    updated: function () {
        var that = this
        $(".memo").bind('click', function () {
            if (that.showMemo) {
                that.showMemo = false
                return
            }
            that.showMemo = true
            that.currentMemo = $(this).data('memo')
        })
    }
})