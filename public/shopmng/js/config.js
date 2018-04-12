/**
 * Created by fish on 2018/4/3.
 */
var uploadImgURL='/upload/uploadImageOfBase64'
var loginURL='/shopmng/loginMng'
var checkSessionURL='/shopmng/checkSession'
var quitLoginURL='/shopmng/quitLogin'
var getShopIndexConfigURL='/shop/getShopIndexConfig'
var saveShopConfigURL='/shopmng/saveShopIndex'
var listCategroyURL='/shopmng/categoryALL'
var saveProductURL='/shopmng/saveProductInfo'
var getOneProductURL='/shopmng/getOneProduct'
var listProductURL='/shop/listProduct'
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
                data : {},
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