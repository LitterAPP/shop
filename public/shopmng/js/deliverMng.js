/**
 * Created by fish on 2018/3/29.
 */
/**
 * Created by fish on 2018/3/29.
 */
var pageSize = 15
var pCategoryId = '', subCategoryId = ''
var deliverMng = new Vue({
    el: '#deliverMng',
    data: {
        list: [],
        condition: {
            orderId: '',
            productName: '',
            status: {
                selected: '-2',
                options: [
                    {text: '全部待发货', value: '-2'},
                    {text: '支付成功:待发货', value: '1'},
                    {text: '拼团成功:待发货', value: '6'}
                ]
            }
        },
        expressList: [],
        expressOrderCode: '',
        expressSelected: 'DBL',
        deliverOrderId: '',
        deliverOrder: {},
        currentMemo: '',
        showMemo: false,
        deliverPrompt: false,
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
        deliverCancel: function () {
            var that = this
            that.deliverPrompt = false
            that.deliverOrderId = ''

        },
        deliverOK: function () {
            var that = this
            console.log('发货订单id', that.deliverOrderId, that.expressSelected)
            if (stringEmpty(that.expressSelected)) {
                toast(that, '请选择物流公司')
                return;
            }
            if (stringEmpty(that.expressOrderCode)) {
                toast(that, '请填写物流订单号')
                return;
            }
            showLoading(that, '发货登记中,请稍后...')
            $.ajax({
                url: deliverURL,
                type: 'POST',
                dataType: 'json',
                data: {
                    orderId: that.deliverOrderId,
                    expressCode: that.expressSelected,
                    expressOrderCode: that.expressOrderCode
                },
                success: function (result) {
                    console.log('deliver', result)

                    if (result && result.code == 1) {
                        that.deliverPrompt = false
                        hideLoading(that)
                    } else {
                        toast(that, '发货登记失败')
                    }
                }
            })

        },
        deliver: function (e) {
            var that = this
            that.deliverOrderId = e.target.dataset.orderid
            that.deliverOrder = that.list[e.target.dataset.idx]
            console.log('selected order', that.deliverOrder)
            that.deliverPrompt = true
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
        listSource(3, function (result) {
            that.expressList = result
            that.expressSelected = 'DBL'
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