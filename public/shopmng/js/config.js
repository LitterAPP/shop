/**
 * Created by fish on 2018/4/3.
 */
var uploadImgURL='/upload/uploadImageOfBase64'
var loginURL='/shopmng/loginMng'
var checkSessionURL='/shopmng/checkSession'
var quitLoginURL='/shopmng/quitLogin'
var getShopIndexConfigURL='/shopmng/getShopIndexConfig'
var saveShopConfigURL='/shopmng/saveShopIndex'
var listCategroyURL='/shopmng/categoryALL'
var saveProductURL='/shopmng/saveProductInfo'
var getOneProductURL='/shopmng/getOneProduct'
var listProductURL='/shopmng/listProduct'
var operatedProductURL='/shopmng/operatedProduct'
var addPCategroyURL='/shopmng/addPCategory'
var delPCategroyURL='/shopmng/delPCategory'
var addSubCategroyURL='/shopmng/addSubCategory'
var delSubCategoryURL='/shopmng/delSubCategory'
var savePCategoryURL='/shopmng/savePCategory'
var saveSubCategoryURL='/shopmng/saveSubCategory'
var changeCategoryOrderURL='/shopmng/changeCategoryOrder'
var listOrderURL='/shopmng/listOrder'
var listSourceURL='/shopmng/listSource'
var deliverURL='/shopmng/deliverMng'
var listCouponURL = '/shopmng/listCoupon'
var addCouponURL = '/shopmng/addCoupon'
var listWeTaoURL = '/shopmng/listWeTao'
var listWeTaoForUserURL = '/shop/listWeTao'
var addWeTaoUrl = '/shopmng/addWeTao'
var offLineWeTaoUrl = '/shopmng/offLineWeTao'
var onLineWeTaoUrl = '/shopmng/onLineWeTao'
var getOneWeTaoUrl = '/shopmng/getOneWeTao'
var weTaoDetailUrl='/shop/weTaoDetail'
var zanOnDetailPageURL='/shop/zanOnDetailPage'
var domain = 'http://192.168.0.184:9020/'
var smsSendUrl='/Sms/sendCode'
var regUrl='/shopmng/reg'
var urlTools = {
    //获取RUL参数值
    getUrlParam: function(name) {               /*?videoId=identification  */
        var params = decodeURI(window.location.search);        /* 截取？号后面的部分    index.html?act=doctor,截取后的字符串就是?act=doctor  */
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = params.substr(1).match(reg);
        if (r!=null) return unescape(r[2]); return null;
    }
}
var common = {
    listSource:function(type,func){
        $.ajax(
            {
                url:listSourceURL,
                type : "POST",
                dataType:"json",
                data : {type:type},
                success:function(result){
                    if(func && typeof(func)=='function' ){
                    func(result)
                    }
                 }
        })
    },
    listCategroy: function (handler,func) {
        console.log('调用listCategroy开始')
        showLoading(handler,'请稍后,商品分类加载中...')
        $.ajax(
            {
                url:listCategroyURL,
                type : "POST",
                dataType:"json",
                data : {
                    force:true,
                },
                success:function(result){
                    console.log('调用listCategroy结束')
                    if(result && result.code==1){
                        hideLoading(handler)
                        handler.pCategory = result.data
                    }else{
                        toast(handler,'商品分类加载中失败:'+result.msg)
                    }
                    //需要回调做特殊处理的情况
                    if(func && typeof(func)=='function' ){
                        func(result)
                    }
                }
            });
    }
    ,
    checkLogin:function(func){
        $.ajax(
            {
                url:checkSessionURL,
                type : "POST",
                dataType:"json",
                data : {},
                success:function(result){
                    func(result)
                }
            });
    },
    toast:function(handler,text){
        handler.mask = true
        handler.maskText=text
        setTimeout(function(){
            handler.mask = false
        },2000)
    },
    showLoading:function(handler,text){
        handler.mask = true
        handler.maskText=text
    },
    hideLoading:function(handler){
        setTimeout(function(){
            handler.mask = false
        },300)
    },
    trimAll:function(text){
       return  text.replace(/\s+/g, '')
    },
    trim:function(text){
        return  text.replace(/^\s+|\s+$/g, '')
    },
    stringEmpty:function(str){
        str = str+''
        return !str || str.replace(/\s+/g, '').length==0
    },
    checkMobile:function(mobile){
        if(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/.test(mobile)){
            return true
        }
        return false
    }
}
//alias
var toast = common.toast
var checkLogin = common.checkLogin
var listCategroy = common.listCategroy
var trimAll = common.trimAll
var trim = common.trim
var showLoading = common.showLoading
var hideLoading = common.hideLoading
var stringEmpty = common.stringEmpty
var listSource =common.listSource
var checkMobile=common.checkMobile