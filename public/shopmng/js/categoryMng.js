/**
 * Created by fish on 2018/4/9.
 */
/**
 * Created by fish on 2018/3/29.
 */
var categoryMng = new Vue({
    el: '#categoryMng',
    data: {
        pCategory:{},
        addPcatText:'',
        addSubcatText:'',
        pidx:-1,
        mask:false,
        maskText:''
    },
    methods:{
        up:function(e){
            var that = this
            var i = e.target.dataset.idx
            var option = that.pCategory.options[i]
            if(!option) return
            var theDestOption = that.pCategory.options[parseInt(i)-1]
            if(!theDestOption) return
            console.log('up',option.value,theDestOption.value)
            $.ajax({
                url:changeCategoryOrderURL,
                type:'POST',
                dataType:'json',
                data:{
                    pid1:option.value,
                    pid2:theDestOption.value
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1){
                        listCategroy(that,null)
                        hideLoading(that)
                        that.addPcatText = ''
                    }else{
                        toast(that,'上移失败')
                    }
                }
            })
        },
        down:function(e){
            var that = this
            var i = e.target.dataset.idx
            var option = that.pCategory.options[i]
            if(!option) return
            var theDestOption = that.pCategory.options[parseInt(i)+1]
            if(!theDestOption) return
            console.log('down',option.value,theDestOption.value)
            $.ajax({
                url:changeCategoryOrderURL,
                type:'POST',
                dataType:'json',
                data:{
                    pid1:option.value,
                    pid2:theDestOption.value
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1){
                        listCategroy(that,null)
                        hideLoading(that)
                        that.addPcatText = ''
                    }else{
                        toast(that,'下移失败')
                    }
                }
            })
        },
        subCatInputFocus:function(e){
            var that = this
            that.addSubcatText=''
            var pidx = e.target.dataset.pidx
            that.pidx = pidx
        },
        addPCategory:function(e){
            var that = this
            if(!that.addPcatText && that.addPcatText.length==0){
                toast(that,'请输入分类名称')
                return
            }
            for(var i in that.pCategory.options){
                if(that.pCategory.options[i].text ==trimAll(that.addPcatText) ){
                    toast(that,'该分类已经存在')
                    return
                }
            }
            showLoading(that,'请稍后...')
            $.ajax({
                url:addPCategroyURL,
                type:'POST',
                dataType:'json',
                data:{
                    text:that.addPcatText
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1){
                        listCategroy(that,null)
                        hideLoading(that)
                        that.addPcatText = ''
                    }else{
                        toast(that,'新增分类失败')
                    }
                }
            })
        },
        addSubCategory:function(e){
            var that = this
            if(!that.addSubcatText && that.addSubcatText.length==0){
                toast(that,'请输入分类名称')
                return
            }
            var pidx = e.target.dataset.pidx
            var pCat = that.pCategory.options[pidx]

            if(pCat.subCategory && pCat.subCategory.options &&   pCat.subCategory.options.length > 0 ){
                for(var i in pCat.subCategory.options){
                    if(pCat.subCategory.options[i].text ==trimAll(that.addSubcatText) ){
                        toast(that,'该分类已经存在')
                        return
                    }
                }
            }

            showLoading(that,'请稍后...')
            $.ajax({
                url:addSubCategroyURL,
                type:'POST',
                dataType:'json',
                data:{
                    text:that.addSubcatText,
                    pCategoryId:pCat.value
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1){
                        listCategroy(that,null)
                        hideLoading(that)
                        that.addSubcatText = ''
                    }else{
                        toast(that,'新增分类失败')
                    }
                }
            })
        },
        deletePCategory:function(e){
            var that = this
            $.confirm({
                columnClass: 'col-md-4 col-md-offset-4',
                title: '删除确认',
                content: '确认删除分类吗？商品关联关系也将被删除（快捷键：enter=立即伤处，esc=取消不删除）',
                buttons: {
                    ok: {
                        text: "立即删除",
                        keys: ['enter'],
                        action: function(){
                            showLoading(that,'请稍后...')
                            $.ajax({
                                url:delPCategroyURL,
                                type:'POST',
                                dataType:'json',
                                data:{
                                    pid:e.target.dataset.id
                                },
                                success:function(result){
                                    console.log(result)
                                    if(result && result.code==1){
                                        listCategroy(that,null)
                                        hideLoading(that)
                                    }else{
                                        toast(that,'删除分类失败')
                                    }
                                }
                            })
                        }
                    },
                    cancel: {
                        text: "取消不删除",
                        keys: ['esc'],
                        action:function () {

                        }
                    }
                }
            });

        },
        deleteSubCategory:function(e){
            var that = this
            $.confirm({
                title: '删除确认',
                content: '确认删除分类吗？商品关联关系也将被删除（快捷键：enter=立即伤处，esc=取消不删除）',
                buttons: {
                    ok: {
                        text: "立即删除",
                        keys: ['enter'],
                        action: function(){
                            showLoading(that,'请稍后...')
                            $.ajax({
                                url:delSubCategoryURL,
                                type:'POST',
                                dataType:'json',
                                data:{
                                    subId:e.target.dataset.id
                                },
                                success:function(result){
                                    console.log(result)
                                    if(result && result.code==1){
                                        listCategroy(that,null)
                                        hideLoading(that)
                                    }else{
                                        toast(that,'删除分类失败')
                                    }
                                }
                            })
                        }
                    },
                    cancel: {
                        text: "取消不删除",
                        keys: ['esc'],
                        action:function () {

                        }
                    }
                }
            });


        },
        savePCategory:function(e){
            var that = this
            var text = e.target.dataset.text
            var id = e.target.dataset.id
            if(stringEmpty(text) || stringEmpty(id)){
                toast(that,'分类名称不能为空')
                return ;
            }

            showLoading(that,'请稍后...')
            $.ajax({
                url:savePCategoryURL,
                type:'POST',
                dataType:'json',
                data:{
                    pid:id,
                    text:text
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1){
                        listCategroy(that,null)
                        hideLoading(that)
                    }else{
                        toast(that,'修改一级分类失败')
                    }
                }
            })
        },
        saveSubCategory:function(e){
            var that = this
            var text = e.target.dataset.text
            var id = e.target.dataset.id
            if(stringEmpty(text) || stringEmpty(id)){
                toast(that,'分类名称不能为空')
                return ;
            }

            showLoading(that,'请稍后...')
            $.ajax({
                url:saveSubCategoryURL,
                type:'POST',
                dataType:'json',
                data:{
                    subId:id,
                    text:text
                },
                success:function(result){
                    console.log(result)
                    if(result && result.code==1){
                        listCategroy(that,null)
                        hideLoading(that)
                    }else{
                        toast(that,'修改二级分类失败')
                    }
                }
            })
        },
    },
    created:function(){
        console.log('created')
        //检测登录态
        checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
            }else{
                window.parent.location.href='../../html/login.html'
            }
        })
       var that = this
       listCategroy(this,null)
    },
})