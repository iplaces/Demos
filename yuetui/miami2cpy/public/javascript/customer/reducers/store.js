/**
 * Created by zsh on 2016/3/14.
 */
import {
    RECEIVE_STORE_CATEGORIES,
    RECEIVE_STORE_LIST,
    APPEND_TO_STORE_LIST,
    RECEIVE_STORE_INFO,
    RECEIVE_REMARK_LIST
    }from '../actions/ActionTypes'

export function storeCategories(state =[], action){
    switch (action.type) {
        case RECEIVE_STORE_CATEGORIES:
            return action.storeCategories
        default:
            return state
    }
}

export function storeList(state = [], action){
    switch (action.type) {
        case RECEIVE_STORE_LIST:
            return action.storeList
        case APPEND_TO_STORE_LIST:
            return state.concat(action.storeList)
        default:
            return state

    }
}

export function storeInfo(state = {}, action) {
    switch (action.type) {
        case RECEIVE_STORE_INFO:
            return action.storeInfo
        default:
            return state
    }
}

export function remarkList(state = [],action) {
    switch (action.type) {
        case RECEIVE_REMARK_LIST:
            return action.remarkList
        default:
            return state
    }
}