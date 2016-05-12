/**
 * Created by zsh on 2016/3/15.
 */
import {
    RECEIVE_CART_LIST,
    ADD_TO_CART,
    MINUS_FOOD,
    SET_FOOD_CATEGORY,
    CHECKOUT_SUCCESS
    }from '../actions/ActionTypes'

const initialState = {
    addedIds: [],
    quantityById: {}
}

function addedIds(state = initialState.addedIds, action) {
    //console.log("reducer addedIds===state==="+JSON.stringify(state)+"===action==="+JSON.stringify(action))
    switch (action.type) {
        case ADD_TO_CART:
            if (state.indexOf(action.foodId) !== -1) {
                return state
            }
            return [ ...state, action.foodId ]
        case RECEIVE_CART_LIST:
            return action.cart.addedIds || []
        default:
            return state
    }
}

function quantityById(state = initialState.quantityById, action) {
    //console.log("reducer quantityById===state==="+JSON.stringify(state)+"===action==="+JSON.stringify(action))
    const { foodId } = action
    switch (action.type) {
        case ADD_TO_CART:
            return Object.assign({}, state, {
                [foodId]: (state[foodId] || 0) + 1
            })
        case MINUS_FOOD:
            return Object.assign({}, state, {
                [foodId]: state[foodId] - 1
            })
        case RECEIVE_CART_LIST:
            return action.cart.quantityById || {}
        default:
            return state
    }
}

export function cart(state = initialState,action){
    //console.log("reducer cart===state==="+JSON.stringify(state)+"===action==="+JSON.stringify(action))
    switch (action.type) {
        case CHECKOUT_SUCCESS:
            console.log("initialState========"+initialState)
            return initialState
        case MINUS_FOOD:
            const {foodId} = action
            const index = state.addedIds.indexOf(foodId)
            if(index===-1){
                console.log("index === -1")
                return state
            }else if(state.quantityById[foodId]==1){
                //state.addedIds.splice(index,1);
                //delete state.quantityById[foodId]
                const newState = $.extend(true,{},state)
                newState.addedIds.splice(index,1);
                delete newState.quantityById[foodId]
                console.log("state after minus==="+state)
                return {
                    addedIds: newState.addedIds,
                    quantityById: newState.quantityById
                }
            }else{
                return {
                    addedIds: addedIds(state.addedIds, action),
                    quantityById: quantityById(state.quantityById, action)
                }
            }
        default:
            return {
                addedIds: addedIds(state.addedIds, action),
                quantityById: quantityById(state.quantityById, action)
            }
    }
}

export function getQuantity(state, foodId) {
    return state.quantityById[foodId] || 0
}

export function getAddedIds(state) {
    return state.addedIds
}