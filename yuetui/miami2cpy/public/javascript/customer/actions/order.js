/**
 * Created by zsh on 2016/3/30.
 */
import fetch from 'isomorphic-fetch'
import user from '../api/user'
import{
    RECEIVE_ORDER_LIST,
    RECEIVE_ORDER_DETAIL

} from './ActionTypes'
function receiveOrderList(orderList){
    return{
        type: RECEIVE_ORDER_LIST,
        orderList
    }
}

export function getOrderList() {
    return dispatch => {
        user.getOrderList(data =>{
            dispatch(receiveOrderList(data))
        })
    }
}

export function cancelOrder(orderId) {
    return dispatch => {
        user.cancelOrder(orderId,data => {
            dispatch(getOrderList())
        })
    }
}

export function confirmReceipt(orderId,storeId) {
    return dispatch => {
        user.confirmReceipt(orderId,storeId,data =>{
            dispatch(getOrderList())
        })
    }
}

export function reqRefund(data) {
    return dispatch => {
        user.reqRefund(data, res => {
            dispatch(getOrderList())
        })
    }
}
function receiveOrderDetail(orderDetail){
    return{
        type: RECEIVE_ORDER_DETAIL,
        orderDetail
    }
}

export function getOrderDetail(orderId) {
    return dispatch => {
        user.getOrderDetail(orderId,data => {
            dispatch(receiveOrderDetail(data))
        })
    }
}

export function createComment(json) {
    return dispatch => {
        user.createComment(json,data => {
            dispatch(getOrderList())
        })
    }
}