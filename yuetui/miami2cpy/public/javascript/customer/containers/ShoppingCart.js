/**
 * Created by zsh on 2016/3/15.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {setCartToLocal,addToCart,minusFood,clearCart} from '../actions/cart'
import {getCart} from '../reducers/index'


class ShoppingCart extends Component{

    constructor(props){
        super(props)
    }

    //shouldComponentUpdate(nextProps,nextState){
    //    if(JSON.stringify(this.props.cartList)!==JSON.stringify(nextProps.cartList)) console.log("更新购物车")
    //    return JSON.stringify(this.props.cartList)!==JSON.stringify(nextProps.cartList)
    //}

    componentDidUpdate() {
        const storeId = this.props.storeId
        let cart = this.props.cart
        setCartToLocal(cart,storeId)
    }

    handleAddFood(foodId){
        this.props.dispatch(addToCart(foodId))
    }

    handleMinusFood(foodId){
        this.props.dispatch(minusFood(foodId))
    }

    handleCheckout() {
        const storeId = this.props.storeId
        let path = 'order/checkout/'+storeId
        this.context.router.push(path)
    }
    handleClearCart(){
        const storeId = this.props.storeId
        this.props.dispatch(clearCart(storeId))
    }



    render() {
        const { cartList } = this.props
        const hasProducts = cartList.length > 0
        const getFee = (fee,food) => fee+food.sale_price*food.quantity;
        const fee = cartList.reduce(getFee,0);
        const nodes = !hasProducts ?
            <em>往篮子里添加美食吧~</em> :
            cartList.map(food =>
                    <div className="shop-cartbasket-tablerow" key={food.id}>
                        <div className="cell itemname">{food.name}</div>
                        <div className="cell itemquantity">
                            <img src="/miami/assets/images/customer/storeindex/减小@2x.png" className="ctrl minus" onClick={this.handleMinusFood.bind(this,food.id)} />
                            <span>{food.quantity}</span>
                            <img src="/miami/assets/images/customer/storeindex/加小@2x.png" className="ctrl plus" onClick={this.handleAddFood.bind(this,food.id)} />
                        </div>
                        <div className="cell itemtotal">￥{food.sale_price*food.quantity}</div>
                    </div>
            )
        return (
            <div className="shop-cart">
                <div className="shop-cartbasket">
                    <div className="shop-grouphead-row">
                        <h4>购物车</h4>
                        <div className="cart-clear" onClick={this.handleClearCart.bind(this)}>
                            <img src="/miami/assets/images/customer/storeindex/垃圾箱@2x.png" />
                            <span>清空</span>
                        </div>
                    </div>
                    <div className="shop-cartbasket-tablerow">
                        <div className="cell itemname">菜品</div>
                        <div className="cell itemquantity">份数</div>
                        <div className="cell itemtotal">价格</div>
                    </div>
                    {nodes}
                    <div className="shop-cartbasket-tablerow">
                        <div className="cell itemname">餐盒费</div>
                        <div className="cell itemquantity"></div>
                        <div className="cell itemtotal">￥{this.props.storeInfo.pack_fee}</div>
                    </div>
                </div>
                <div className="shop-cart-total">
                    共计<span>￥{this.props.storeInfo.pack_fee+fee}</span>
                </div>
                <div className="shop-cartfooter">
                    <div className="shop-cartfooter-counter">
                        <img src="/miami/assets/images/customer/storeindex/购物车@2x.png" />
                        <span className="cart-length">{cartList.length}</span>
                        <span>购物车</span>
                    </div>
                    <button className="shop-cartfooter-check" onClick={this.handleCheckout.bind(this)}
                            disabled={hasProducts ? '' : 'disabled'}>
                        {hasProducts ? '选好了' : '空'}
                    </button>
                </div>
            </div>
        )
    }
}

ShoppingCart.contextTypes ={
    router: React.PropTypes.object.isRequired
}

ShoppingCart.propTypes = {
    cart: PropTypes.shape({
        addedIds: PropTypes.arrayOf().isRequired,
        quantityById: PropTypes.shape({}).isRequired
    }).isRequired,
    cartList:PropTypes.arrayOf({}).isRequired,
}

function mapStateToProps(state) {
    return {
        cartList: getCart(state),
        cart:state.cart,
        storeInfo:state.storeInfo
    }
}

export default connect(
    mapStateToProps
)(ShoppingCart)