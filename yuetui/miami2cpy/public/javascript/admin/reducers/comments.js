import {
    RECEIVE_COMMENT_LIST,
    DEFAULT_COMMENT_LIST
} from '../constants/ActionTypes'


const comments = (state = DEFAULT_COMMENT_LIST, action) => {
    switch (action.type) {
        case RECEIVE_COMMENT_LIST:
            return {list: action.comments, num: action.num}

        default:
            return state
    }
}



export default comments