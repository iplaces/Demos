import fetch from 'isomorphic-fetch'
export default {
    getOrderList(state, pageNum, cb) {
        //获得订单列表
        let url;
        //url = `/miami/admin/order/list?state=` + state + `&pageNum=` + pageNum
        if(state != '') {
            url = `/miami/admin/order/list?state=` + state + `&pageNum=` + pageNum
        } else {
            url =  `/miami/admin/order/list?`+ `pageNum=` + pageNum
        }
        console.log('getOrderList')
        console.log(url)
        fetch(url, {credentials: 'include'})
            .then(response => response.json())
            .then(json => {
                console.log('shop.js getOrderList')
                console.log(json)
                cb({list: json.list, num: json.num})
            })
    },

    getStoreInfo(cb) {
        //获得商户信息
        fetch(`/miami/admin/store/info`, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>  {console.log('shop getStoreInfo');console.log(json);cb(json.data)}
            )
    },

    getOrderSpans(cb) {
        fetch(`/miami/admin/order/spans`, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>
                {console.log('shop getOrderSpans');console.log(json);cb(json.data)}
            )
    },

    getStoreList(cb) {
        //获得商户列表
        console.log('getStoreList started')
        fetch(`/miami/admin/store/list`, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>
            {console.log('shop getStoreList');console.log(json);cb(json.list)})
    },

    getCommentList(storeId, pageNum, cb) {

        let url
        if(storeId != 0) {
            url = `/miami/admin/comment/list?storeId=` + storeId + `&pageNum=` + pageNum
        } else {
            url =  `/miami/admin/comment/list?pageNum=` + pageNum
        }
        fetch(url, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>
            {console.log('shop getCommentList'); console.log(json); cb({list: json.result, num: json.num})}
            )
    },

    getConfMap(cb) {
        //读取初始confMap中的用户基本信息
       cb($CONF$)
    },

    getGoodList(cb) {
        //获得商品列表
        console.log('getGoodList started')
        fetch(`/miami/admin/good/list`, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>
            {console.log('shop getGoodList'); console.log(json);cb(json.list)})
    },

    getCateList(cb) {
        //获得分类列表
        console.log('getCateList started')
        fetch(`/miami/admin/category/list`, {credentials: 'include'})
            .then(response => response.json())
            .then(json =>
            {console.log('shop getCategoryList'); console.log(json);cb(json.list)})
    },

}