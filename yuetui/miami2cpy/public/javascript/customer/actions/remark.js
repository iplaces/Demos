/**
 * Created by zsh on 2016/4/23.
 */

import shop from '../api/shop'
import {
    RECEIVE_REMARK_LIST
} from './ActionTypes'

function receiveRemarkList(remarkList){
    return{
        type: RECEIVE_REMARK_LIST,
        remarkList
    }
}

export function getRemarkList(storeId,level,page) {
    return dispatch => {
        shop.getRemarkList(storeId,level,page,data =>{
            dispatch(receiveRemarkList(data))
        })
    }
}