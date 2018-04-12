/**
 * Created by fish on 2018/3/29.
 */
/**
 *   导航JSON ： {
                "text": "全部宝贝",
                "url": "/pages/shop/list",
                "img":"",
                "imgkey":"",
                "sort": 1,
                "type": 1,
                "linkType": 1
            }
 * @type {Vue}
 */
var shopId = 1
var flag,index
var shopMng = new Vue({
    el: '#shopMng',
    data: {
        shopName:'',
        shopAvatar:"",
        shopAvatarKey:'',
        wellcomeText:'',
        shopBanner:"",
        shopBannerKey:'',
        activityText:'',
        activityBg:'',
        activityBgKey:'',
        firstNavList:[

        ],
        secondNavList:[

        ],
        swiperList:[

        ],
        thirdNavList:[],
        fourthNavList:[],
        fiveNavList:[],
        mask:true,
        maskText:'请稍后...'
    },

    computed: {
        innerHeight: function () {
            return window.innerHeight
        }
    },

    created:function(){
        var that = this
        console.log('created')
        //检测登录态
        common.checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
            }else{
                window.parent.location.href='../../html/login.html'
            }
        })
        that.getShopData()
    },

    methods:{
        getShopData:function(){
            var that = this
            //获取店铺数据
            that.mask=true
            $.ajax(
                {
                    url:getShopIndexConfigURL,
                    type : "POST",
                    dataType:"json",
                    data : {shopId : shopId},
                    success:function(result){
                        if(result && result.code==1 && result.data){
                            console.log('获取店铺配置成功.',result.data)
                            that.shopName = result.data.config.shopName
                            that.shopAvatar = result.data.config.shopAvatar
                            that.shopAvatarKey = result.data.config.shopAvatarKey
                            that.shopBanner = result.data.config.shopBanner
                            that.shopBannerKey = result.data.config.shopBannerKey
                            that.wellcomeText = result.data.config.wellcomeText
                            that.activityBg = result.data.config.activityBg
                            that.activityBgKey = result.data.config.activityBgKey
                            that.activityText = result.data.config.activityText
                            that.firstNavList = result.data.config.firstNavList
                            that.secondNavList = result.data.config.secondNavList
                            that.swiperList = result.data.config.swiperList
                            that.thirdNavList = result.data.config.thirdNavList
                            that.fourthNavList = result.data.config.fourthNavList
                            that.fiveNavList = result.data.config.fiveNavList
                        }else{

                        }
                        that.mask=false
                    }
                }
            );
        },
        delNavItem:function(event){
            var flag = event.target.dataset.flag
            var idx = event.target.dataset.idx
            if(flag == 1 && idx>=0 && idx < this.firstNavList.length){
                this.firstNavList.splice(idx,1)
                return
            }
            if(flag == 2 && idx>=0 && idx < this.secondNavList.length){
                this.secondNavList.splice(idx,1)
                return
            }
            if(flag == 3 && idx>=0 && idx < this.swiperList.length){
                this.swiperList.splice(idx,1)
                return
            }

            if(flag == 4 && idx>=0 && idx < this.thirdNavList.length){
                this.thirdNavList.splice(idx,1)
                return
            }

            if(flag == 5 && idx>=0 && idx < this.fourthNavList.length){
                this.fourthNavList.splice(idx,1)
                return
            }

            if(flag == 6 && idx>=0 && idx < this.fiveNavList.length){
                this.fiveNavList.splice(idx,1)
                return
            }
        },
        addNavItem:function(event){
           var flag = event.target.dataset.flag
            if(flag == 1){
                this.firstNavList.push({ linkType:0,type:0})
              //  Vue.set(this.firstNavList,this.firstNavList.length-1 , this.firstNavList[this.firstNavList.length-1]);
                return
            }
            if(flag == 2){
                this.secondNavList.push({ linkType:0,type:0})
            //    Vue.set(this.secondNavList,this.secondNavList.length-1 , this.secondNavList[this.secondNavList.length-1]);
                return
            }
            if(flag == 3){
                this.swiperList.push({ linkType:0,type:0})
                //    Vue.set(this.swiperList,this.swiperList.length-1 , this.swiperList[this.swiperList.length-1]);
                return
            }

            if(flag == 4){
                this.thirdNavList.push({ linkType:0,type:0})
                //    Vue.set(this.swiperList,this.swiperList.length-1 , this.swiperList[this.swiperList.length-1]);
                return
            }

            if(flag == 5){
                this.fourthNavList.push({ linkType:0,type:0})
                //    Vue.set(this.swiperList,this.swiperList.length-1 , this.swiperList[this.swiperList.length-1]);
                return
            }

            if(flag == 6){
                this.fiveNavList.push({ linkType:0,type:0})
                //    Vue.set(this.swiperList,this.swiperList.length-1 , this.swiperList[this.swiperList.length-1]);
                return
            }


        },
        avatarClick:function(){
            $("#shopAvatarInput").click()
        },
        shopBannerClick:function(){
            $("#shopBannerInput").click()
        },
        activityBgClick:function(){
            $("#activityBgInput").click()
        },
        navImgClick:function(event){
            index = event.target.dataset.idx
            flag = event.target.dataset.flag
            console.log('navImgClick','flag',flag,'index',index)
            if(flag==1){
                $("#firstNavImageInput").click()
            }
            if(flag==2){
                $("#secondNavImageInput").click()
            }

            if(flag==3){
                $("#swiperImageInput").click()
            }

            if(flag==4){
                $("#thirdImageInput").click()
            }

            if(flag==5){
                $("#fourthImageInput").click()
            }

            if(flag==6){
                $("#fiveImageInput").click()
            }
        },
        navImgChanged:function(){
            console.log('navImgChanged','flag',flag,'index',index)
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败

            var file
            if(flag==1){
                file = $("#firstNavImageInput")[0].files[0];
            }
            if(flag==2){
                file = $("#secondNavImageInput")[0].files[0];
            }
            if(flag==3){
                file = $("#swiperImageInput")[0].files[0];
            }

            if(flag==4){
                file = $("#thirdImageInput")[0].files[0];
            }

            if(flag==5){
                file = $("#fourthImageInput")[0].files[0];
            }

            if(flag==6){
                file = $("#fiveImageInput")[0].files[0];
            }
            var imgUrlBase64
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        that.mask = true
                        that.maskText='请稍后,图片上传中...'
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
                                    that.mask = false
                                    if(result && result.code==1){
                                        if(flag==1){
                                            that.firstNavList[index].img= reader.result
                                            that.firstNavList[index].imgkey = result.data
                                            Vue.set(that.firstNavList,index , that.firstNavList[index]);
                                        }
                                        if(flag==2){
                                            that.secondNavList[index].img = reader.result
                                            that.secondNavList[index].imgkey = result.data
                                            Vue.set(that.secondNavList,index , that.secondNavList[index]);
                                        }

                                        if(flag==3){
                                            that.swiperList[index].img = reader.result
                                            that.swiperList[index].imgkey = result.data
                                            Vue.set(that.swiperList,index , that.swiperList[index]);
                                        }

                                        if(flag==4){
                                            that.thirdNavList[index].img = reader.result
                                            that.thirdNavList[index].imgkey = result.data
                                            Vue.set(that.thirdNavList,index , that.thirdNavList[index]);
                                        }

                                        if(flag==5){
                                            that.fourthNavList[index].img = reader.result
                                            that.fourthNavList[index].imgkey = result.data
                                            Vue.set(that.fourthNavList,index , that.fourthNavList[index]);
                                        }

                                        if(flag==6){
                                            that.fiveNavList[index].img = reader.result
                                            that.fiveNavList[index].imgkey = result.data
                                            Vue.set(that.fiveNavList,index , that.fiveNavList[index]);
                                        }
                                    }else{
                                        alert(result.msg)
                                    }
                                }
                            });
                    }
                }
            }
        },
        shopBannerChange:function(){
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#shopBannerInput")[0].files[0];
            var imgUrlBase64;
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        that.mask = true
                        that.maskText='请稍后,图片上传中...'
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
                                    that.mask = false
                                    if(result && result.code==1){
                                        that.shopBanner = reader.result
                                        that.shopBannerKey = result.data
                                    }else{
                                        alert(result.msg)
                                    }
                                }
                            });
                    }
                }
            }
        },
        activityBgChange:function(){
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#activityBgInput")[0].files[0];
            var imgUrlBase64;
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        showLoading(that,'请稍后,图片上传中...')
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
                                    hideLoading(that)
                                    if(result && result.code==1){
                                        that.activityBg = reader.result
                                        that.activityBgKey = result.data
                                    }else{
                                        toast(that,result.msg)
                                    }
                                }
                            });
                    }
                }
            }
        },
        shopAvatarChange:function(){
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#shopAvatarInput")[0].files[0];
            var imgUrlBase64;
            if (file) {
                //将文件以Data URL形式读入页面
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    //var ImgFileSize = reader.result.substring(reader.result.indexOf(",") + 1).length;//截取base64码部分（可选可不选，需要与后台沟通）
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        //调用服务器上传Base64位的图片
                        that.mask = true
                        that.maskText='请稍后,图片上传中...'
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
                                    that.mask = false
                                    if(result && result.code==1){
                                        that.shopAvatar = reader.result
                                        that.shopAvatarKey = result.data
                                    }else{
                                        alert(result.msg)
                                    }
                                }
                            });
                    }
                }
            }
        },
        saveShopConfig:function(){
            var that = this
            var postData = {
                shopId:shopId,
                firstNavList:this.firstNavList || [],
                fiveNavList:this.fiveNavList || [],
                fourthNavList:this.fourthNavList || [],
                secondNavList:this.secondNavList || [],
              //  shopAvatar:this.shopAvatar,
                shopAvatarKey:this.shopAvatarKey,
              //  shopBanner:this.shopBanner,
                shopBannerKey:this.shopBannerKey,
                activityBgKey:this.activityBgKey,
                activityText:this.activityText,
                shopName:this.shopName,
                swiperList:this.swiperList || [],
                thirdNavList:this.thirdNavList || [],
                wellcomeText:this.wellcomeText
            }

            for(var i in postData.firstNavList){
                postData.firstNavList[i].img=''
            }
            for(var i in postData.fiveNavList){
                postData.fiveNavList[i].img=''
            }
            for(var i in postData.fourthNavList){
                postData.fourthNavList[i].img=''
            }
            for(var i in postData.secondNavList){
                postData.secondNavList[i].img=''
            }
            for(var i in postData.thirdNavList){
                postData.thirdNavList[i].img=''
            }
            for(var i in postData.swiperList){
                postData.swiperList[i].img=''
            }
           showLoading(that,'请稍候,商品配置保存中...')
            $.ajax(
                {
                    // url:"http://localhost:9020/Upload/uploadImageOfBase64",
                    url:saveShopConfigURL,
                    type : "POST",
                    dataType:"json",
                    data : encodeURIComponent(JSON.stringify(postData)),
                    success:function(result){
                        hideLoading(that)
                        console.log(result && result.code==1,result)
                        if(result && result.code==1){
                            toast(that,'商铺配置成功!')
                            setTimeout(function () {
                                that.getShopData()
                            },3000)
                        }else{
                            toast(that,'商铺配置失败!'+result.msg)
                        }
                    }
                });
        },
    }
})