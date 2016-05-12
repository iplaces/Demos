/**
 * Created by zsh on 2016/3/30.
 */
import fetch from 'isomorphic-fetch'
import shop from '../api/shop'
import user from '../api/user'
import {
    RECEIVE_FOOD_LIST,
    RECEIVE_FOOD_CATEGORY,
    RECEIVE_FOOD_LIST_WITH_CATE,
    SET_FOOD_CATEGORY,
    RECEIVE_SEARCH_RESULT
} from './ActionTypes'

/*******************storeIndex***************************/
/**
 * 获取foodList
 */
function receiveFoodCategory(foodCategory){
    return{
        type: RECEIVE_FOOD_CATEGORY,
        foodCategory
    }
}

function receiveFoodList(foodList){
    return{
        type: RECEIVE_FOOD_LIST,
        foodList
    }
}

export function getFoodList(storeId,sort) {
    return dispatch => {
        shop.getFoodList(storeId,sort||0,data => {
            dispatch(receiveFoodCategory(data.category))
            dispatch(receiveFoodList(data.good))
        })
    }
}
function receiveFoodListWithCate(foodListWithCate){
    return{
        type: RECEIVE_FOOD_LIST_WITH_CATE,
        foodListWithCate
    }
}
export function getFoodListWithCate(storeId,sort) {
    return dispatch => {
        shop.getFoodListWithCate(storeId,sort||0,data => {
            dispatch(receiveFoodListWithCate(data))
        })
    }
}

export function searchFood(keyword) {
    return dispatch => {
        shop.searchFood(keyword,foodList => {
            dispatch(receiveFoodList(foodList))
        })
    }
}

function receiveSearchResult(searchResult){
    return{
        type: RECEIVE_SEARCH_RESULT,
        searchResult
    }
}

export function search(keyword) {
    return dispatch => {
        shop.searchAll(keyword,data => {
            dispatch(receiveSearchResult(data))
        })
    }
}

export function setFoodCategory(categoryId) {
    return {
        type : SET_FOOD_CATEGORY,
        categoryId
    }
}