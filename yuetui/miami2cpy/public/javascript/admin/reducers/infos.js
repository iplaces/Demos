import {
    RECEIVE_STORE_INFO,
    DEFAULT_STORE
} from '../constants/ActionTypes'

const info = (state = DEFAULT_STORE, action) => {
    switch (action.type) {
        case RECEIVE_STORE_INFO:
            return action.info
        default:
            return state
    }
}


export default info