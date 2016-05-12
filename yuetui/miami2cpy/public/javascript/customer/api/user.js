/**
 * Created by zsh on 2016/3/16.
 */
import fetch from 'isomorphic-fetch'
export default {
    getOrderList(cb){
        $.ajax({
            url: '/miami/customer/order/getByCustomer'
        }).success(function(res){
            if(res.errCode=='1000601'){
                alert('用户未登录，将跳转到登陆页面！')
                location.href = res.url
            }else if(res.errCode==0){
                cb(res.data)
            }else{
                alert(JSON.stringify(res))
            }
        }).fail(res => alert(JSON.stringify(res)))
    },

    cancelOrder(orderId,cb) {
        $.ajax({
            url: '/miami/customer/order/cancel?orderId='+orderId
        }).success(function(res){
            if(res.errCode=='1000601'){
                alert('用户未登录，将跳转到登陆页面！')
                location.href = res.url
            }else if(res.errCode==0){
                cb(res.data)
            }else{
                alert(JSON.stringify(res))
            }
        }).fail(res => alert(JSON.stringify(res)))
    },

    confirmReceipt(orderId,storeId, cb) {
        $.ajax({
            url:'/miami/customer/order/confirmReceipt?orderId='+orderId+"&storeId="+storeId
        }).done(function(res){
            if(res.errCode==0) cb(res.data)
            else alert(JSON.stringify(res))
        }).fail(function(res){
            alert(JSON.stringify(res))
        })
    },

    reqRefund(data, cb) {
        $.ajax({
            url:'/miami/customer/refund/create',
            type:'POST',
            data: JSON.stringify(data),
            contentType:'application/json'
        }).done(function(res){
            if(res.errCode==0) cb(res.data)
            else alert(JSON.stringify(res))
        }).fail(function(res){
            alert(JSON.stringify(res))
        })
    },
    getOrderDetail(orderId,cb) {
        fetch('/miami/customer/order/getDetail?orderId='+orderId,{credentials: 'include'})
            .then(response => response.json())
            .then(json => {
                if(json.errCode=='1000601'){
                    alert('用户未登录，将跳转到登陆页面！')
                    location.href = json.url
                }else if(json.errCode==0){
                    cb(json.data)
                }else{
                    alert(JSON.stringify(json))
                }
            })
    },

    getAddress(storeId,cb){
        $.ajax({
            url: '/miami/customer/order/getAddress?storeId='+storeId
        }).success(function(res){
            if(res.errCode=='1000601'){
                alert('用户未登录，将跳转到登陆页面！')
                location.href = res.url
            }else if(res.errCode==0){
                cb(res.address)
            }else{
                alert(JSON.stringify(res))
            }
        }).fail(res => alert(JSON.stringify(res)))
    },

    addAddress(data,cb) {
        $.ajax({
            url:'/miami/customer/order/setAddress',
            type:'POST',
            data:JSON.stringify(data),
            contentType:'application/json'
        }).done(function(res){
            if(res.errCode==0) cb(res.data)
            else alert(JSON.stringify(res))
        }).fail(function(res){
            alert(JSON.stringify(res))
        })
    },
    editAddress(data,cb) {
        $.ajax({
            url:'/miami/customer/order/modifyAddress',
            type:'POST',
            data:JSON.stringify(data),
            contentType:'application/json'
        }).done(function(res){
            if(res.errCode==0) cb(res.data)
            else alert(JSON.stringify(res))
        }).fail(function(res){
            alert(JSON.stringify(res))
        })
    },
    deleteAddress(addressid,cb) {
        $.ajax({
            url:'/miami/customer/order/deleteAddress?addressId='+addressid
        }).done(function(res){
            if(res.errCode==0) cb(res.data)
            else alert(JSON.stringify(res))
        }).fail(function(res){
            alert(JSON.stringify(res))
        })
    },
    createComment(data,cb) {
        $.ajax({
            url:'/miami/customer/comment/create',
            type:'POST',
            data: JSON.stringify(data),
            contentType:'application/json'
        }).done(function(res){
            if(res.errCode==0) cb(res.data)
            else alert(JSON.stringify(res))
        }).fail(function(res){
            alert(JSON.stringify(res))
        })
    }
}