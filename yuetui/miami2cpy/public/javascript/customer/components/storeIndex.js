/**
 * Created by zsh on 2016/3/14.
 */
import React, { Component, PropTypes } from 'react'

import StoreInfo  from '../containers/StoreInfo'
import FoodList  from '../containers/FoodList'
import RemarkList  from '../containers/RemarkList'
import ShoppingCart from '../containers/ShoppingCart'

export default class storeIndex extends Component{

    render() {
        const storeId = this.props.params.storeId

        return (
            <div className="shop-index">
                <StoreInfo storeId={storeId} />
                {this.props.children}
                <ShoppingCart storeId={storeId}/>
            </div>
        )
    }
}