import fetch from 'isomorphic-fetch'
import shop from '../api/shop'
import {
    RECEIVE_STORE_LIST,
    RECEIVE_ORDER_LIST,
    RECEIVE_CONF_MAP,
    RECEIVE_GOOD_LIST,
    RECEIVE_CATE_LIST,
    RECEIVE_ORDER_SPANS,
    RECEIVE_STORE_INFO,
    RECEIVE_COMMENT_LIST
    } from '../constants/ActionTypes'


function receiveStoreInfo(info) {
    return {
        type: RECEIVE_STORE_INFO,
        info: info
    }
}

export function getStoreInfo() {
    return dispatch => {
        shop.getStoreInfo(info => {
            dispatch(receiveStoreInfo(info))
        })
    }
}

function receiveOrderList(data) {
  return {
      type: RECEIVE_ORDER_LIST,
      orders: data.list,
      num: data.num
  }
}

export function getOrderList(state, pageNum) {
    return dispatch => {
        shop.getOrderList(state, pageNum, data => {
            dispatch(receiveOrderList(data))
        })
    }
}

export function getOrderSpans() {
    console.log('actions getOrderSpans')

    return dispatch => {
        shop.getOrderSpans(spans => {
            dispatch(receiveOrderSpans(spans))
        })
    }
}

function receiveOrderSpans(spans) {
    return {
        type: RECEIVE_ORDER_SPANS,
        spans: spans
    }
}

function receiveStoreList(list) {
    return {
        type: RECEIVE_STORE_LIST,
        stores: list
    }
}

export function getStoreList() {
    console.log('actions getStoreList')
    return dispatch => {
        shop.getStoreList(list => {
            dispatch(receiveStoreList(list))
        })
    }
}


function receiveCommentList(data) {

    return {
        type: RECEIVE_COMMENT_LIST,
        comments: data.list,
        num: data.num
    }
}

export function getCommentList(storeId, pageNum) {
    console.log('actions getCommentList')
    return dispatch => {
        shop.getCommentList(storeId, pageNum, list => {
            dispatch(receiveCommentList(list))
        })
    }
}

function receiveConfMap(user) {
    return {
        type: RECEIVE_CONF_MAP,
        user: user
    }
}

export function getConfMap() {
    return dispatch => {
        shop.getConfMap(user => {
            dispatch(receiveConfMap(user))
        })
    }
}

function receiveGoodList(list) {
    return {
        type: RECEIVE_GOOD_LIST,
        goods: list
    }
}

export function getGoodList() {
    console.log('actions getGoodList')
    return dispatch => {
        shop.getGoodList(list => {
            dispatch(receiveGoodList(list))
        })
    }
}

function receiveCateList(list) {
    return {
        type: RECEIVE_CATE_LIST,
        categories: list
    }
}

export function getCateList() {
    console.log('actions getCateList')
    return dispatch => {
        shop.getCateList(list => {
            dispatch(receiveCateList(list))
        })
    }
}