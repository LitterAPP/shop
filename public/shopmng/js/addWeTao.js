/**
 * Created by fish on 2018/4/2.
 */
var id, E,editor
var currentImagesIdx=-1
var addWeTao = new Vue({
    el: '#addWeTao',
    data: {
        id:0,
        content:'',
        images:[{
            remoteUrl: '',
            osskey: ''
        }],
        seoTitle:'',
        seoKey:'',
        seoDesc:'',
        mask:false
    },
    methods:{
        setHtml:function(html){
            editor.txt.html(html)
        },
        getHtml:function(){
           return editor.txt.html()
        },
        addImages:function(){
            var obj = {}
            this.images.push(obj)
        },
        delImages:function(event){
            var index = event.target.dataset.idx
            this.images.splice(index,1)
        },
        imagesClick:function(event){
            currentImagesIdx = event.target.dataset.idx
            $("#imagesIpnut").click()
        },
        imagesChanged:function(event){
            var that = this
            var reader = new FileReader();
            var AllowImgFileSize = 5*1024*1024; //上传图片最大值(单位字节)（ 2 M = 2097152 B ）超过2M上传失败
            var file = $("#imagesIpnut")[0].files[0];
            var imgUrlBase64;
            if (file) {
                imgUrlBase64 = reader.readAsDataURL(file);
                reader.onload = function (e) {
                    if (AllowImgFileSize != 0 && AllowImgFileSize < reader.result.length) {
                        alert('上传失败，请上传不大于5M的图片！');
                        return;
                    } else {
                        that.mask=true
                        that.maskText='请稍后,上传图片中...'
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
                                        that.images[currentImagesIdx].remoteUrl = reader.result
                                        that.images[currentImagesIdx].osskey = result.data
                                        Vue.set(that.images,currentImagesIdx,that.images[currentImagesIdx])
                                    }else{
                                        toast(that,'上传失败')
                                    }
                                }
                            });
                    }
                }
            }
        },
        getOneWeTao:function(){
                var that = this
                showLoading(that,'请稍后,正在加载代金券...')
                $.ajax({
                    url:getOneWeTaoUrl,
                    type:'POST',
                    dataType:'json',
                    data:{
                        id:id
                    },
                    success:function(result){
                        if(result && result.code==1 && result.data){
                            hideLoading(that)
                            that.id = result.data.id
                            that.setHtml(result.data.content)
                            that.seoDesc = result.data.seoDesc
                            that.seoTitle = result.data.seoTitle
                            that.seoKey = result.data.seoKey
                            that.images = result.data.images
                        }else{
                            toast(that,'加载微淘失败')
                        }
                    }
                })
        },
        goBack: function () {
            window.history.back()
        },
        saveWeTao:function(){
          //  console.log('--->',this.getHtml())

            if(stringEmpty(this.getHtml())){
                toast(this,'微淘文本为空')
                return
            }

            var imageStr='';
            if(this.images && this.images.length>0){
                var play_picsChecked = true
                for(var i in this.images){
                    if(!this.images[i] ||  !this.images[i].osskey || this.images[i].osskey.length==0){
                        play_picsChecked = false
                        break;
                    }
                    if(i!=this.images.length-1){
                        imageStr += this.images[i].osskey+","
                    }else{
                        imageStr += this.images[i].osskey
                    }
                }
                if(!play_picsChecked){
                    toast(this,'微淘图片不完整')
                    return
                }

            }


            var that = this
            $.ajax({
                url:addWeTaoUrl,
                type:'POST',
                dataType:'json',
                data:{
                    id:that.id,
                    content:that.getHtml(),
                    images:imageStr,
                    seoTitle:that.seoTitle,
                    seoKey:that.seoKey,
                    seoDesc:that.seoDesc
                },
                success:function(result){
                   if(result && result.code == 1){
                        that.goBack()
                   }else{
                       toast(that,'保存失败,'+result.msg)
                   }
                }
            })
        },
    },
    created:function(){
        console.log('created')
        //检测登录态
        id =  urlTools.getUrlParam("id")
        checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
            }else{
                window.parent.location.href='../../html/login.html'
            }
        })
        if(id){
            this.getOneWeTao()
        }

        E = window.wangEditor
        editor = new E('#content')
        editor.create()
        this.setHtml('<p>用 JS 设置的内容111</p>')
    },

})