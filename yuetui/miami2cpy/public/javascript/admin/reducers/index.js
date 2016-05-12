import { combineReducers } from 'redux'
import goods from './goods'
import {orders , spans} from './orders'
import categories from './categories'
import user from './user'
import stores from './stores'
import info from './infos'
import comments from './comments'

const rootReducer = combineReducers({
    goods,
    orders,
    spans,
    categories,
    user,
    stores,
    info,
    comments
})

export default rootReducer
