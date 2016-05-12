import {
    RECEIVE_CATE_LIST
} from '../constants/ActionTypes'


const categories = (state = [], action) => {
    switch (action.type) {
        case RECEIVE_CATE_LIST:
            return action.categories
        default:
            return state
    }
}

export default categories