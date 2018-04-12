/**
 * Created by fish on 2018/4/2.
 */
var currentPalyImgIdx=-1,currentPicDetailIdx=-1,currentGroupImgIdx=-1,productId
var addProduct = new Vue({
    el: '#addProduct',
    data: {
        title:'',
        productType:0,
        store:0,
        contact_mobile:'',
        contact_wx:'',
        price:[],
        banner_pic:{
            remoteUrl:'',
            osskey:''
        },
        play_pics:[{
            remoteUrl: '',
            osskey: ''
        }],
        selectedCategoryParams:[],
        selectedAttrs:[],
        together_info:{},
        text_details:[{value:''}],
        pic_details:[{
            remoteUrl:'',
            osskey:''
        }],
        groups:[],


        category:[

        ],
        /**首次加载分类列表*/
        pCategory:{
            /*options:[
                {text:'One',value:'A',
                    subCategory:{
                        options:[
                            {text:'One',value:'A'},  {text:'Tow',value:'B'},  {text:'Three',value:'C'}
                        ]
                    }
                },
                {text:'Tow',value:'B',
                    subCategory:{
                        options:[
                            {text:'One',value:'A'},  {text:'Tow',value:'B'},  {text:'Three',value:'C'}
                        ]
                    }
                },
                {text:'Three',value:'C'}
            ]*/
        },
        join_together:false,
        isSale:false,
        isHot:false,
        mask:false
    },
    methods:{
        listCategroy: function () {
            var that = this
            that.mask=true
            that.maskText='请稍后,商品分类加载中...'
            $.ajax(
                {
                    // url:"http://localhost:9020/Upload/uploadImageOfBase64",
                    url:listCategroyURL,
                    type : "POST",
                    dataType:"json",
                    data : {},
                    success:function(result){
                        that.mask = false
                        if(result && result.code==1){
                            that.pCategory = result.data
                            console.log(that.pCategory)
                        }else{
                            that.mask = true;
                            that.maskText='加载商品失败:'+result.msg
                        }
                        setTimeout(function(){
                            that.mask = false
                        },2000)
                    }
                });
        },
        saveProduct:function(event){
            var that = this
            this.selectedCategoryParams=[]
            for(var i in this.category){
                var obj={}
                var oneCat = this.category[i]

                var oneSelected = oneCat.selected
                var oneSelectedIndex = oneCat.selectedIndex
                //没有选择一级分类
                if(!oneSelectedIndex || oneSelectedIndex==0) {
                   continue
                }
                obj.pCategoryId = oneSelected
                if(oneCat.options[oneSelectedIndex].subCategory && oneCat.options[oneSelectedIndex].subCategory.selected && oneCat.options[oneSelectedIndex].subCategory.selected!='0'){
                    obj.subCategoryId = oneCat.options[oneSelectedIndex].subCategory.selected
                }
                this.selectedCategoryParams.push(obj)
            }

            var postData={
                productId:productId,
                isHot:this.isHot,
                isSale:this.isSale,
                title:this.title,
                productType:0,
                store:this.store,
                contact_wx:this.contact_wx,
                contact_mobile:this.contact_mobile,
                price:this.price,
                banner_pic:this.banner_pic,
                play_pics:this.play_pics,
                selectedCategoryParams:this.selectedCategoryParams,
                selectedAttrs:this.selectedAttrs,
                together_info:this.together_info,
                text_details:this.text_details,
                pic_details:this.pic_details,
                groups:this.groups,
                join_together:this.join_together
            }
            //检查POST的数据是否完整
            if(!postData.title || postData.title.length==0){
                that.mask = true
                that.maskText='必填：商品标题'
                setTimeout(function(){that.mask=false},1000)
                return
            }
            if(!postData.store){
                that.mask = true
                that.maskText='必填：库存必填'
                setTimeout(function(){that.mask=false},1000)
                return
            }
            if(!postData.price || postData.price.length!=2){
                that.mask = true
                that.maskText='必填：商品原价和现价均必填'
                setTimeout(function(){that.mask=false},1000)
                return
            }
            if(!postData.banner_pic || !postData.banner_pic.osskey || postData.banner_pic.osskey.length==0){
                that.mask = true
                that.maskText='必填：商品的ICON必填'
                setTimeout(function(){that.mask=false},1000)
                return
            }

            if(!postData.play_pics || postData.play_pics.length==0 || postData.play_pics[0].osskey.length==0){
                that.mask = true
                that.maskText='必填：商品的轮播图片至少1张'
                setTimeout(function(){that.mask=false},1000)
                return
            }
            var play_picsChecked = true
            for(var i in postData.play_pics){
                if(!postData.play_pics[i] ||  !postData.play_pics[i].osskey || postData.play_pics[i].osskey.length==0){
                    play_picsChecked = false
                    break;
                }
            }
            if(!play_picsChecked){
                that.mask = true
                that.maskText='必填：商品的轮播图片不完整'
                setTimeout(function(){that.mask=false},1000)
                return
            }

            if(that.join_together ){
                if(!postData.together_info.price || postData.together_info.price.length==0){
                    that.mask = true
                    that.maskText='必填：拼团价格必填'
                    setTimeout(function(){that.mask=false},1000)
                    return
                }
                if( !postData.together_info.num || postData.together_info.num.length==0){
                    that.mask = true
                    that.maskText='必填：拼团人数必填'
                    setTimeout(function(){that.mask=false},1000)
                    return
                }
                if( !postData.together_info.hour|| postData.together_info.hour.length==0){
                    that.mask = true
                    that.maskText='必填：拼团限时必填'
                    setTimeout(function(){that.mask=false},1000)
                    return
                }
            }


            if(!postData.text_details || postData.text_details.length==0 || postData.text_details[0].value.length==0){
                that.mask = true
                that.maskText='必填：商品的描述至少1条'
                setTimeout(function(){that.mask=false},1000)
                return
            }
            var textDetailsChecked = true
            for(var i in postData.text_details){
                if(!postData.text_details[i] ||  !postData.text_details[i].value || postData.text_details[i].value.length==0){
                    textDetailsChecked = false
                    break;
                }
            }
            if(!textDetailsChecked){
                that.mask = true
                that.maskText='必填：商品的描述不完整'
                setTimeout(function(){that.mask=false},1000)
                return
            }


            if(!postData.pic_details || postData.pic_details.length==0 || postData.pic_details[0].osskey.length==0){
                that.mask = true
                that.maskText='必填：商品的详情图片至少1张'
                setTimeout(function(){that.mask=false},1000)
                return
            }
            var pic_detailsChecked = true
            for(var i in postData.pic_details){
                if(!postData.pic_details[i] ||  !postData.pic_details[i].osskey || postData.pic_details[i].osskey.length==0){
                    pic_detailsChecked = false
                    break;
                }
            }
            if(!pic_detailsChecked){
                that.mask = true
                that.maskText='必填：商品的详情图片不完整'
                setTimeout(function(){that.mask=false},1000)
                return
            }

            var gruopsChecked = true
            for(var i in postData.groups){
                if(!postData.groups[i] ||  !postData.groups[i]
                        || !postData.groups[i].title  || postData.groups[i].title.length==0
                    || !postData.groups[i].price1 || postData.groups[i].price1.length==0
                    || ! postData.groups[i].price2 || postData.groups[i].price2.length==0
                    || !postData.groups[i].osskey || postData.groups[i].osskey.length==0
                ){
                    gruopsChecked = false
                    break;
                }
            }
            if(!gruopsChecked){
                that.mask = true
                that.maskText='必填：商品的分组数据不完整'
                setTimeout(function(){that.mask=false},1000)
                return
            }


            //去除图片base64信息提交
            postData.banner_pic.remoteUrl=''
            for(var i in postData.play_pics){
                postData.play_pics[i].remoteUrl=''
            }
            for(var i in postData.pic_details){
                postData.pic_details[i].remoteUrl=''
            }
            for(var i in postData.groups){
                postData.groups[i].remoteUrl=''
            }
            console.log('保存商品->',JSON.stringify(postData))
            that.mask = true
            that.maskText="请稍后,商品信息保存中..."
            $.ajax({
                url:saveProductURL,
                type:'POST',
                dataType:'json',
                data:encodeURIComponent(JSON.stringify(postData)),
                success:function(result){
                    if(result && result.code==1 && result.data && result.data.productId){
                        that.mask=true
                        productId=result.data.productId
                        that.maskText='商品信息保存成功！'
                        console.log('商品信息保存成功！')
                        setTimeout(function () {
                            that.getOneProductInfo()
                        },3000)
                    }else{
                        that.mask=true
                        that.maskText='商品信息保存失败！'+result.msg
                        console.log('商品信息保存失败！')
                    }
                    setTimeout(function () {
                        that.mask = false
                    },3000)
                }
            });
        },
        productBannerClick:function(event){
            $("#productBannerInput").click()
        },
        delPCategoryItem:function(event){
            var index = event.target.dataset.idx
            this.category.splice(index,1)
        },
        addPCategoryItem:function(e){//deep copy
            var obj = {}
            obj = JSON.parse(JSON.stringify(this.pCategory))
            obj.selected='0'
            this.category.push(obj)
            console.log(this.category)
        },
        delAttr:function(event){
            var index = event.target.dataset.idx
            this.selectedAttrs.splice(index,1)
            console.log(this.selectedAttrs)
        },
        addAttr:function(event){
            this.selectedAttrs.push('')
        },
        delPlayPic:function(event){
            var index = event.target.dataset.idx
            this.play_pics.splice(index,1)
        },
        playImgClick:function(event){
            currentPalyImgIdx = event.target.dataset.idx
            $("#playImgInput").click()
        },
        addPlayPic:function(){
            var obj = {}
            this.play_pics.push(obj)
        },
        delPicDetail:function(event){
            var index = event.target.dataset.idx
            this.pic_details.splice(index,1)
        },
        picDetailClick:function(event){
            currentPicDetailIdx = event.target.dataset.idx
            $("#picDetailInput").click()
        },
        addPicDetail:function(){
            var obj = {}
            this.pic_details.push(obj)
        },
        delTextDetail:function(event){
            var index = event.target.dataset.idx
            this.text_details.splice(index,1)
        },
        addTextDetail:function(){
            this.text_details.push({value:''})
        },
        groupImgClick:function(event){
            currentGroupImgIdx = event.target.dataset.idx
            $("#groupImageInput").click()
        },
        delGroupItem:function(event){
            var index = event.target.dataset.idx
            this.groups.splice(index,1)
        },
        addGroupItem:function(){
            this.groups.push({})
        },
        groupImgChanged: function (event) {
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#groupImageInput")[0].files[0];
            var imgUrlBase64;
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        that.mask=true
                        that.maskText='请稍后,上传商品分组截图中...'
                        $.ajax(
                            {
                                // url:"http://localhost:9020/Upload/uploadImageOfBase64",
                                url:uploadImgURL,
                                type : "POST",
                                dataType:"json",
                                data : {
                                    "base64Str" : reader.result,
                                    "cos":1,
                                    "session":""
                                },
                                success:function(result){
                                    that.mask=false
                                    if(result && result.code==1){
                                        that.groups[currentGroupImgIdx].remoteUrl = reader.result
                                        that.groups[currentGroupImgIdx].osskey = result.data
                                        Vue.set(that.groups,currentGroupImgIdx,that.groups[currentGroupImgIdx])
                                    }else{
                                        alert(result.msg)
                                    }

                                }
                            });
                    }
                }
            }
        },
        picDetailChanged:function(event){
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#picDetailInput")[0].files[0];
            var imgUrlBase64;
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        that.mask=true
                        that.maskText='请稍后,上传商品详情截图中...'
                        $.ajax(
                            {
                                // url:"http://localhost:9020/Upload/uploadImageOfBase64",
                                url:uploadImgURL,
                                type : "POST",
                                dataType:"json",
                                data : {
                                    "base64Str" : reader.result,
                                    "cos":1,
                                    "session":""
                                },
                                success:function(result){
                                    that.mask=false
                                    if(result && result.code==1){
                                        that.pic_details[currentPicDetailIdx].remoteUrl = reader.result
                                        that.pic_details[currentPicDetailIdx].osskey = result.data
                                        Vue.set(that.pic_details,currentPicDetailIdx,that.pic_details[currentPicDetailIdx])
                                    }else{
                                        alert(result.msg)
                                    }
                                }
                            });
                    }
                }
            }
        },
        playImgChanged:function(event){
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#playImgInput")[0].files[0];
            var imgUrlBase64;
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        that.mask=true
                        that.maskText='请稍后,上传商品轮播截图中...'
                        $.ajax(
                            {
                                // url:"http://localhost:9020/Upload/uploadImageOfBase64",
                                url:uploadImgURL,
                                type : "POST",
                                dataType:"json",
                                data : {
                                    "base64Str" : reader.result,
                                    "cos":1,
                                    "session":""
                                },
                                success:function(result){
                                    that.mask=false
                                    if(result && result.code==1){
                                        that.play_pics[currentPalyImgIdx].remoteUrl = reader.result
                                        that.play_pics[currentPalyImgIdx].osskey = result.data
                                        Vue.set(that.play_pics,currentPalyImgIdx,that.play_pics[currentPalyImgIdx])
                                    }else{
                                        alert(result.msg)
                                    }
                                }
                            });
                    }
                }
            }
        },
        pCategoryChanged:function(event){
            var idx = event.target.dataset.idx
            console.log('selected',idx,this.category[idx])

            var options = this.category[idx].options
            for(var i in options){
                if(options[i].value == this.category[idx].selected){
                    this.category[idx].selectedIndex = i
                    break
                }
            }

           // this.category[idx].subCategory = this.category.options[idx].subCategory
        },
        subCategoryChanged:function(event){

        },
        productBannerChange:function(event){
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#productBannerInput")[0].files[0];
            var imgUrlBase64;
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        that.mask=true
                        that.maskText='请稍后,上传商品ICON中...'
                        $.ajax(
                            {
                                // url:"http://localhost:9020/Upload/uploadImageOfBase64",
                                url:uploadImgURL,
                                type : "POST",
                                dataType:"json",
                                data : {
                                    "base64Str" : reader.result,
                                    "cos":1,
                                    "session":""
                                },
                                success:function(result){
                                    that.mask=false
                                    if(result && result.code==1){
                                        that.banner_pic.remoteUrl = reader.result
                                        that.banner_pic.osskey = result.data
                                    }else{
                                        alert(result.msg)
                                    }
                                }
                            });
                    }
                }
            }
        },
        goBack: function () {
            window.history.back()
        },
        getOneProductInfo:function(){
            var that = this

           if(!productId || productId.indexOf("PRO-")==-1) {
               console.log('productId not exist', productId)
               return
           }
           that.mask = true
           that.maskText = '请稍后,商品数据加载中...'
            $.ajax({
                url:getOneProductURL,
                dataType:'json',
                type:'POST',
                data:{productId:productId},
                success:function(result){
                    that.mask = false
                    if(result && result.code==1 && result.data){
                        productId=productId
                        that.isHot=result.data.isHot
                        that.isSale=result.data.isSale
                        that.title=result.data.title
                        that.productType=0
                        that.store=result.data.store
                        that.price=result.data.price
                        that.banner_pic=result.data.banner_pic
                        that.play_pics=result.data.play_pics
                        that.selectedCategoryParams=result.data.selectedCategoryParams
                        //分类处理
                        that.category = []
                        for(var i in that.selectedCategoryParams){
                            var obj = {}
                            obj = JSON.parse(JSON.stringify(that.pCategory))
                            obj.selected=that.selectedCategoryParams[i].pCategoryId

                            for(var j in obj.options){
                                if(obj.options[j].value==obj.selected){
                                    obj.selectedIndex=j
                                    if(that.selectedCategoryParams[i].subCategoryId){
                                        obj.options[j].subCategory.selected = that.selectedCategoryParams[i].subCategoryId
                                    }
                                    break;
                                }
                            }
                            //console.log(JSON.stringify(obj))
                            that.category.push(obj)
                        }
                        that.selectedAttrs=result.data.selectedAttrs
                        that.join_together = result.data.join_together
                        that.together_info=result.data.together_info||{}
                        that.text_details=result.data.text_details
                        that.pic_details=result.data.pic_details
                        that.groups=result.data.groups
                        console.log('加载商品成功',productId,result.data,that.together_info)
                    }else{
                        console.log('加载商品失败',productId)
                        alert(result.msg)
                    }
                }
            });
           //获取商品详情
        }
    },
    created:function(){
        console.log('created')
        //检测登录态
        productId =  urlTools.getUrlParam("productId")
        common.listCategroy(this)
        common.checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
            }else{
                window.parent.location.href='../../html/login.html'
            }
        })
        this.getOneProductInfo()

    },
})