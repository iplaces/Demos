import {
    RECEIVE_STORE_LIST
} from '../constants/ActionTypes'

const stores = (state = [], action) => {
    switch (action.type) {
        case RECEIVE_STORE_LIST:
            return action.stores

        default:
            return state
    }
}



export default stores