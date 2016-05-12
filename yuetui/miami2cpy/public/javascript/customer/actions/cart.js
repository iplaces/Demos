/**
 * Created by zsh on 2016/3/30.
 */
import fetch from 'isomorphic-fetch'
import shop from '../api/shop'
import {
    RECEIVE_CART_LIST,
    ADD_TO_CART,
    MINUS_FOOD,
} from './ActionTypes'

/**
 * 获取cartList
 */
function receiveCartList(cart){
    return{
        type: RECEIVE_CART_LIST,
        cart
    }
}

export function getCartList(storeId) {
    return dispatch => {
        shop.getCartFromLocal(storeId, data => {
            dispatch(receiveCartList(data || {}))
        })
    }
}

/**
 * 添加到cart
 */
function addToCartUnsafe(foodId) {
    return {
        type: ADD_TO_CART,
        foodId
    }
}
function minusFoodUnsafe(foodId) {
    return {
        type: MINUS_FOOD,
        foodId
    }
}

export function setCartToLocal(cart,storeId) {
    console.log("save to local in action ====")
    shop.setCartToLocal(cart,storeId)
}

export function addToCart(foodId) {
    return (dispatch, getState) => {
        dispatch(addToCartUnsafe(foodId))
    }
}

export function minusFood(foodId) {
    return (dispatch, getState) => {
        dispatch(minusFoodUnsafe(foodId))
    }
}
//清空购物车
export function clearCart(storeId) {
    return dispatch =>{
        shop.clearLS(storeId,()=>{
            dispatch(getCartList(storeId))
        })
    }
}