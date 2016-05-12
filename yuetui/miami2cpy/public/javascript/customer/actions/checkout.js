/**
 * Created by zsh on 2016/3/30.
 */
import fetch from 'isomorphic-fetch'
import shop from '../api/shop'
import user from '../api/user'
import{
    CHECKOUT_SUCCESS,
    RECEIVE_ORDER_LIST,
    RECEIVE_ADDRESS_LIST
} from './ActionTypes'

/*******************order check***************************/

export function checkout(storeId) {
    return (dispatch, getState) => {
        const cart = getState().cart

        shop.clearLS(storeId, () => {
            //dispatch({
            //  type: CHECKOUT_REQUEST
            //})
            //dispatch({
            //  type: CHECKOUT_SUCCESS,
            //  cart
            //})
            // Replace the line above with line below to rollback on failure:
            // dispatch({ type: types.CHECKOUT_FAILURE, cart })
        })
    }
}

export function createOrder(data,storeId) {
    return dispatch => {
        shop.createOrder(data, res => {
            if(res.errCode==0){
                dispatch(checkout(storeId))
                dispatch({
                    type: CHECKOUT_SUCCESS,
                    null
                })
                //console.log(res)
                location.href=res.data
            }else{
                alert('下单失败，原因：'+res.msg)
            }
        })
    }
}

function receiveAddress(addressList) {
    return {
        type: RECEIVE_ADDRESS_LIST,
        addressList
    }
}

export function getAddress(storeId) {
    return dispatch => {
        user.getAddress(storeId,data => {
            dispatch(receiveAddress(data))
        })
    }
}

export function addAddress(data) {
    return dispatch => {
        user.addAddress(data, res => {
            dispatch(getAddress(0))
        })
    }
}

export function editAddress(data) {
    return dispatch => {
        user.editAddress(data, res=>{
            dispatch(getAddress(0))
        })
    }
}

export function deleteAddress(addressid) {
    return dispatch => {
        user.deleteAddress(addressid, res=>{
            dispatch(getAddress(0))
        })
    }
}