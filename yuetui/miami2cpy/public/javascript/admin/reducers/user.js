import {
    RECEIVE_CONF_MAP,
    DEFAULT_USER
} from '../constants/ActionTypes'

const user = (state = DEFAULT_USER, action) => {
    switch (action.type) {
        case RECEIVE_CONF_MAP:
            return action.user
        default:
            return state
    }
}


export default user