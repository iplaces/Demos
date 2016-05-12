import { combineReducers } from 'redux'
import {
    RECEIVE_ORDER_LIST,
    RECEIVE_ORDER_SPANS,
    DEFAULT_SPAN,
    DEFAULT_ORDER_LIST
} from '../constants/ActionTypes'

export const orders = (state = DEFAULT_ORDER_LIST, action) => {
    switch (action.type) {
        case RECEIVE_ORDER_LIST:
            return  {list: action.orders, num: action.num}
        default:
            return state
    }
}


export const spans = (state = DEFAULT_SPAN, action) => {
    switch (action.type) {
        case RECEIVE_ORDER_SPANS:
            return  action.spans

        default:
            return state
    }
}

