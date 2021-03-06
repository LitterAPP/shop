/**
 * Created by fish on 2018/3/29.
 */
/**
 * Created by fish on 2018/3/29.
 */
var pageSize = 15
var pCategoryId = '', subCategoryId = ''
var orderMng = new Vue({
    el: '#orderMng',
    data: {
        list: [],
        condition: {
            orderId: '',
            keyword: '',

            status: {
                selected: '-1',
                options: [
                    {text: '全部', value: '-1'},
                    {text: '支付中', value: '0'},
                    {text: '支付成功:待发货', value: '1'},
                    {text: '支付取消', value: '2'},
                    {text: '支付失败', value: '3'},
                    {text: '支付完成:拼团中', value: '4'},
                    {text: '退款成功', value: '5'},
                    {text: '退款处理失败', value: '55555'},
                    {text: '退款未审核通过', value: '5555'},
                    {text: '退款审核中', value: '555'},
                    {text: '退款中', value: '55'},
                    {text: '拼团成功:待发货', value: '6'},
                    {text: '已投递:已揽件', value: '7'},
                    {text: '已签收', value: '8'},
                    {text: '已确认收货', value: '9'}
                ]
            },
            referScene: '',
            appid: '',
            channel: ''
        },
        currentMemo: '',
        showMemo: false,
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
        getCondition: function () {
            var params = {}
            params.orderId = this.condition.orderId || ''
            params.keyword = this.condition.keyword || ''
            params.status = this.condition.status.selected || ''
            params.referScene = this.condition.referScene || ''
            params.appid = this.condition.appid || ''
            params.channel = this.condition.channel || ''
            return params
        },
        search: function () {
            var that = this
            var params = this.getCondition()
            that.page = 1
            that.listOrder(params.orderId, params.keyword, params.status, params.referScene, params.appid, params.channel, that.page, pageSize, false)
        },
        exportData: function () {
            var that = this
            var params = this.getCondition()
            window.location.href = exportOrderURL + '?orderId=' + params.orderId + '&keyword=' + params.keyword + '&startTime=' + $("#startTimePicker").val() + '&endTime=' + $("#endTimePicker").val() + '&status=' + params.status + '&referScene=' + params.referScene + '&appid=' + params.appid + '&channel=' + params.channel
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
            this.listOrder(params.orderId, params.keyword, params.status, params.referScene, params.appid, params.channel, that.page, pageSize, false)
        },
        listOrder: function (orderId, keyword, status, referScene, appid, channel, page, pageSize, append) {
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
                    referScene: referScene,
                    appid: appid,
                    channel: channel,
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
        that.listOrder('', '', that.condition.status.selected, '', '', '', 1, pageSize, false)

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