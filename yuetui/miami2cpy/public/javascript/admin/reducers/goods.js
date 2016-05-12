import {
    RECEIVE_GOOD_LIST
} from '../constants/ActionTypes'

function goods(state = [], action){
    switch (action.type) {
        case RECEIVE_GOOD_LIST:
            return action.goods
        default:
            return state

    }
}

export default goods