/**
 * Created by zsh on 2016/3/16.
 */
import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'
import {Link} from 'react-router'
import {getFoodList} from '../actions/food'
import {getCartList} from '../actions/cart'
import {getStoreInfo} from '../actions/store'
import {getCart} from '../reducers/index'


class CheckList extends Component{

    componentDidMount(){
        const storeId = this.props.storeId
        const storeInfo = this.props.storeInfo
        let hasStoreInfo
        for(var name in storeInfo){
            hasStoreInfo=true
            break;
        }
        if(!hasStoreInfo){
            this.props.dispatch(getStoreInfo(storeId))
        }
        this.props.dispatch(getFoodList(storeId))
        let cartList = this.props.cartList
        if(cartList.length==0){
            this.props.dispatch(getCartList(storeId))
        }
    }

    render() {
        const storeId = this.props.storeId
        const { cartList } = this.props
        const { storeInfo } = this.props
        const dlist = cartList.map(food =>
                    <dd key={food.id}>
                        <div className="checkout-cart-table-row">
                            <div className="cell item-name">{food.name}</div>
                            <div className="cell item-quantity">{food.quantity}</div>
                            <div className="cell item-total">￥{food.sale_price*food.quantity}</div>
                        </div>
                    </dd>
            )
        const getFee = (fee,food) => fee+food.sale_price*food.quantity;
        const fee = cartList.reduce(getFee,0);
        return (
            <div className="checkout-cart">
                <div className="checkout-cart-title">
                    <h2>订单详情</h2>
                    <Link to={"/storeIndex/"+storeId}>返回店铺修改</Link>
                </div>
                <div className="checkout-cart-table-row table-head">
                    <div className="cell item-name">商品</div>
                    <div className="cell item-quantity">数量</div>
                    <div className="cell item-total">小计（元）</div>
                </div>
                <div className="checkout-cart-group">
                    <dl>
                        {dlist}
                        <dd>
                            <div className="checkout-cart-table-row">
                                <div className="cell item-name">配送费</div>
                                <div className="cell item-quantity"></div>
                                <div className="cell item-total">￥{storeInfo.pack_fee}</div>
                            </div>
                        </dd>
                    </dl>
                </div>
                <div className="checkout-cart-total color-stress">
                    ￥<span>{fee+storeInfo.pack_fee}</span>
                </div>

            </div>
        )
    }
}

CheckList.propTypes = {
    cartList:PropTypes.arrayOf({}).isRequired,
    getCart :PropTypes.func.isRequired,

}

function mapStateToProps(state) {
    return {
        cartList: getCart(state),
        cart:state.cart,
        getCart: getCart,
        storeInfo:state.storeInfo
    }
}
export default connect(
    mapStateToProps
)(CheckList)