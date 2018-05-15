/**
 * Created by fish on 2018/3/29.
 */
var pageSize=15
var pCategoryId='',subCategoryId=''
var productMng = new Vue({
    el: '#productMng',
    data: {
        list:[
            /*{
                productId:"PRO-20171225154938-173050",
                productName:"【正版粉红豹少女心抱枕！】【全网最便宜】【80cm~130cm】全身的浪漫粉色给人温暖，陪你度过即将到来的漫长冬天的人，就是它，粉红顽皮豹！",
                productOriginPrice:"￥18.00",
                productNowPrice:"￥10.00",
                joinTogether:1,
                productTogetherPrice:"￥8.00",
                sotre:10000,
                status:1,
                createTime:"2017-12-25 15:49:38",
                pv:1000,
                deal:999,
                isHot:true,
                isSale:true
            },
            {
                productId:"PRO-20171225154938-173050",
                productName:"【正版粉红豹少女心抱枕！】【全网最便宜】【80cm~130cm】全身的浪漫粉色给人温暖，陪你度过即将到来的漫长冬天的人，就是它，粉红顽皮豹！",
                productOriginAmount:"￥18.00",
                productNowAmount:"￥10.00",
                joinTogether:true,
                productTogetherAmount:"￥8.00",
                sotre:10000,
                status:1,
                createTime:"2017-12-25 15:49:38",
                pv:1000,
                deal:999,
                isHot:true,
                isSale:true
            }*/
        ],
        condition:{
            productId:'',
            productName:'',
            status:{
                selected:'1',
                options:[
                    {text:'待上架',value:'0'},{text:'上架中',value:'1'},  {text:'下架中',value:'2'},  {text:'禁止',value:'3'}
                ]
            },
            pCategory:{
                /*
                selected:'0',
                options:[
                    {text:'One',value:'A',
                        subCategory:{
                        selected:'0',
                        options:[
                            {text:'One',value:'A'},  {text:'Tow',value:'B'},  {text:'Three',value:'C'}
                        ]
                        }
                    },
                    {text:'Tow',value:'B',
                        subCategory:{
                            selected:'0',
                            options:[
                                {text:'One',value:'A'},  {text:'Tow',value:'B'},  {text:'Three',value:'C'}
                            ]
                        }
                    },
                    {text:'Three',value:'C'}
                ]*/
            }
        },
        mask:false,
        mastText:'',
        total:0,
        pageTotal:0,
        page:1,
        productIdChecked:[]
    },
    watch:{

    },
    computed:{

    },
    methods:{
        operator:function(event){
            var that = this
            var flag = parseInt(event.target.dataset.flag)
            if(!that.productIdChecked || that.productIdChecked.length==0){
                that.mask = true;
                that.maskText='请选中要操作的商品'
                setTimeout(function(){
                    that.mask = false
                },2000)
                return
            }
            that.mask = true;
            that.maskText='请稍后,数据更新中...'
            $.ajax({
                url:operatedProductURL,
                dataType:'json',
                type:'POST',
                data:{flag:flag,productIds:that.productIdChecked.join(',')},
                success:function(result){
                    console.log(result)
                    if(result && result.code == 1){
                        that.mask = true;
                        that.maskText='操作成功!'
                    }else{
                        that.mask = true;
                        that.maskText='操作失败:'+result.msg
                    }

                    setTimeout(function(){
                        that.mask = false
                        that.search()
                    },3000)

                }
            })

            console.log(flag,that.productIdChecked)
        },
        edit:function(event){
            var that = this
            var productId = event.target.dataset.productid
            console.log(productId)
           window.location.href="../func/addProduct.html?productId="+productId
        },
        getCondition:function(){
            var params = {}
            params.productId = this.condition.productId || ''
            params.keyword = this.condition.productName || ''
            params.pCategoryId = pCategoryId||''
            params.subCategoryId = subCategoryId||''
            params.status = this.condition.status.selected||''
            return params
        },
        search:function() {
            var that = this
            var params = this.getCondition()
           // console.log(this.condition,params)
            that.page=1
            this.listProduct(params.productId,params.keyword,params.pCategoryId,params.subCategoryId,0,0,params.status,0,that.page,pageSize,false)
        },
        more:function(event){
            var that = this
            var flag = parseInt(event.target.dataset.flag)
            if(flag==1){
                that.page += parseInt(event.target.dataset.flag)
                if(that.page > that.pageTotal){
                    that.page = that.pageTotal
                    return
                }
            }else if(flag==-1  ){
                that.page += parseInt(event.target.dataset.flag)
                if(that.page<=0){
                    that.page=1
                    return ;
                }
            }


            var params = this.getCondition()
            this.listProduct(params.productId,params.keyword,params.pCategory,params.subCategory,0,0,params.status,0,that.page,pageSize,false)
        },
        listProduct: function (productId,keyword,pCategoryId,subCategoryId,isSale,isHot,status,orderBy,page,pageSize,append) {
            var that = this
            that.mask = true
            that.maskText='请稍后,商品列表加载中...'
            $.ajax({
                url:listProductURL,
                type:'POST',
                dataType:'json',
                data:{
                    productId:productId,
                    keyword:keyword,
                    pCategoryId:pCategoryId,
                    subCategoryId:subCategoryId,
                    isSale:isSale,
                    isHot:isHot,
                    status:status,
                    orderBy:1,
                    page:page,
                    pageSize:pageSize
                },
                success:function(result){
                    if(result && result.code==1 && result.data){
                        that.total = result.data.total
                        that.pageTotal = result.data.pageTotal
                        if(append){
                            var index = that.list.length-1
                            for(var i in result.data.list){
                                index++
                                that.list.push(result.data.list[i])
                                Vue.set(that.list,index,result.data.list[i])
                            }
                        }else{
                            that.list  = result.data.list
                        }
                    }
                    that.mask = false
                }
            })
        }
        ,
        pCategoryChanged:function(event){

            if(event.target.selectedIndex == -1){
                return
            }
            //console.log('pCategoryChanged-seleceted value',event.target[event.target.selectedIndex].value)
            pCategoryId = event.target[event.target.selectedIndex].value
            for(var i in this.condition.pCategory.options ){
               // console.log('this.condition.pCategory.options[i].value',this.condition.pCategory.options[i].value,this.condition.pCategory.options[i].value === this.condition.pCategory.selected)
                if(this.condition.pCategory.options[i].value === this.condition.pCategory.selected){
                    this.condition.subCategory = this.condition.pCategory.options[i].subCategory
                    console.log(this.condition.subCategory)
                    break
                }
            }
        },
        subCategoryChanged:function(event){
            if(event.target.selectedIndex == -1){
                return
            }
            //console.log('subCategoryChanged-seleceted value',event.target[event.target.selectedIndex].value)
            subCategoryId = event.target[event.target.selectedIndex].value
        }
        ,
        addProduct:function(){
            window.location.href="../func/addProduct.html"
        }
    },
    created:function(){
        console.log('created')
        //检测登录态
        var that = this
        that.listProduct('','','','',false,false,1,0,1,pageSize,false)
        common.listCategroy(this,function(result){
            that.condition.pCategory = result.data
        })
        common.checkLogin(function(result){
            if(result && result.code==1){
                console.log('登录态校验成功.')
            }else{
                window.parent.location.href='../../html/login.html'
            }
        })
    },
})