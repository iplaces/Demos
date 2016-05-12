/**
 * Created by zsh on 2016/3/16.
 */
import {
    RECEIVE_ORDER_LIST,
    RECEIVE_ORDER_DETAIL,
    RECEIVE_ADDRESS_LIST
    }from '../actions/ActionTypes'

export function orderList(state =[], action){
    switch (action.type) {
        case RECEIVE_ORDER_LIST:
            return action.orderList || []
        default:
            return state

    }
}
export function addressList(state =[], action){
    switch (action.type) {
        case RECEIVE_ADDRESS_LIST:
            return action.addressList || []
        default:
            return state

    }
}
export function orderDetail(state ={}, action){
    switch (action.type) {
        case RECEIVE_ORDER_DETAIL:
            return action.orderDetail || {}
        default:
            return state

    }
}
