
export const RECEIVE_STORE_LIST  = 'RECEIVE_STORE_LIST'
export const RECEIVE_STORE_INFO = 'RECEIVE_STORE_INFO'

export const RECEIVE_ORDER_SPANS = 'RECEIVE_ORDER_SPANS'
export const RECEIVE_ORDER_LIST = 'RECEIVE_ORDER_LIST'

export const RECEIVE_GOOD_LIST = 'RECEIVE_GOOD_LIST'

export const RECEIVE_CATE_LIST = 'RECEIVE_CATE_LIST'

export const RECEIVE_COMMENT_LIST = 'RECEIVE_COMMENT_LIST'

export const RECEIVE_CONF_MAP = 'RECEIVE_CONF_MAP'

export const DEFAULT_USER = { id: '', nickName: '', userType: ''}
export const DEFAULT_SPAN = {unAccepts: 0, unArrived: 0, applyRefund: 0}
export const DEFAULT_ORDER_LIST = {list: [], num: 0 }
export const DEFAULT_STORE_LIST = {list: [], num: 0 }
export const DEFAULT_COMMENT_LIST = {list: [], num: 0}

export const DEFAULT_STORE = {
                                id: 0,
                                name: '',
                                description: '',
                                contact: '',
                                address: '',
                                icon: '',
                                open_from: '',
                                open_to: '',
                                base_price: 0,
                                pack_fee: 0,
                                cat_name: '',
                                sales: 0,
                                comments: 0,
                                grades: 0,
                                cost_time: 0,
                                state: 0,
                                create_time: 0,
                                modified_time: 0
                            }