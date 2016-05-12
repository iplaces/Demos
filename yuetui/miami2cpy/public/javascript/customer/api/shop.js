/**
 * Created by zsh on 2016/3/11.
 */
import fetch from 'isomorphic-fetch'
export default {
    getStoreCategories(cb) {
        //获取店铺分类
        fetch(`/miami/customer/store/listCat`)
            .then(response => response.json())
            .then(json => cb(json.data))
    },

    searchStore(keyword,cb) {
        //按关键词搜索店铺
        fetch(`/miami/customer/store/search?searchKey=`+keyword)
            .then(response => response.json())
            .then(json => cb(json.data))
    },

    searchFood(keyword,cb) {
        //按关键词搜索美食
        fetch(`/miami/customer/good/search?searchKey=`+keyword)
            .then(response => response.json())
            .then(json => cb(json.data))
    },

    searchAll(keyword,cb) {
        //按关键词搜索美食和店铺
        fetch(`/miami/customer/good/search?searchKey=`+keyword)
            .then(response => response.json())
            .then(json => cb(json.data))
    },

    getStoresByCatId(catId,sort,curPage,cb) {
        //按分类搜索店铺
        fetch(`/miami/customer/store/getByCat?catId=`+catId+"&sort="+sort+"&page="+curPage)
            .then(response => response.json())
            .then(json =>{cb(json.data);})
    },

    getFoodList(storeId,sort,cb) {
        fetch('/miami/customer/good/list?storeId='+storeId+'&sort='+sort)
            .then(response =>response.json())
            .then(json => cb(json.data))
    },
    getFoodListWithCate(storeId,sort,cb) {
        fetch('/miami/customer/good/listWithCate?storeId='+storeId+'&sort='+sort)
            .then(response =>response.json())
            .then(json => cb(json.data))
    },

    getCartFromLocal(storeId,cb) {
        console.log("get local from storeId==="+storeId)
        let cartJson = localStorage.getItem("miami_store_id_"+storeId)
        console.log("get str from storeId==="+cartJson)
        cb(JSON.parse(cartJson))
    },

    setCartToLocal(cart,storeId){
        localStorage.setItem("miami_store_id_"+storeId,JSON.stringify(cart))
    },

    clearLS(storeId,cb) {
        localStorage.removeItem('miami_store_id_'+storeId)
        cb()
    },

    createOrder(data,cb) {
        console.log("ajax data====="+JSON.stringify(data))
        $.ajax({
            url:'/miami/customer/order/create',
            type:'POST',
            data: JSON.stringify(data),
            contentType:'application/json'
        }).done(function(res){
            //console.log("shop====="+res)
            cb(res)
        })
    },

    getStoreInfo(storeId,cb) {
        //获得商户信息
        fetch(`/miami/customer/store/getInfo?storeId=`+storeId, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>  {console.log('shop getStoreInfo');console.log(json);cb(json.data)})
    },
    getRemarkList(storeId,level,page,cb) {
        fetch('/miami/customer/comment/getByStore?storeId='+storeId+'&leval='+level+'&page='+page, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>  cb(json.data))
    }
}