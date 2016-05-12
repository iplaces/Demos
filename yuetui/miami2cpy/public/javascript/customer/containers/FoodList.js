/**
 * Created by zsh on 2016/3/14.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import {getFoodListWithCate} from '../actions/food'
import {getCartList,addToCart,minusFood} from '../actions/cart'
import { connect } from 'react-redux'
import FoodCategories from './FoodCategories'

class FoodList extends Component{
    constructor(props){
        super(props)
        const storeId = this.props.params.storeId
        this.props.dispatch(getFoodListWithCate(storeId))
        this.props.dispatch(getCartList(storeId))
    }
    componentDidMount() {

    }


    //shouldComponentUpdate(nextProps,nextState){
    //    const storeId = this.props.params.storeId
    //    if(JSON.stringify(this.props.foodListWithCate)!==JSON.stringify(nextProps.foodListWithCate)){
    //        this.props.dispatch(getCartList(storeId))
    //    }
    //    return JSON.stringify(this.props.foodListWithCate)!==JSON.stringify(nextProps.foodListWithCate) ||
    //
    //}

    handleAddFood(foodId){
      this.props.dispatch(addToCart(foodId))
    }

    handleMinusFood(foodId){
      this.props.dispatch(minusFood(foodId))
    }

    render() {
        const quantityById = this.props.cart.quantityById
        return (
            <div className="shopmenu">
                <hr />
                <div className="food-category">
                    {this.props.foodListWithCate.map((item,index) =>
                        <div className="food-category-item">
                            <a  key = {index} >{item.category.name}</a>
                        </div>
                    )}
                </div>
                <hr />
                <div className="food-list">
                    {this.props.foodListWithCate.map((item,index) => {
                        return(
                               <div className="category-item">
                                   <div className="category-name">{item.category.name}</div>
                                   {item.goods.map((food,foodindex) => {
                                       const cartCtrl = <div className="cart-ctrl">
                                           <img src="/miami/assets/images/customer/storeindex/加@2x.png" className="ctrl plus" onClick={this.handleAddFood.bind(this,food.id)} />
                                           <span>{quantityById[food.id]}</span>
                                           <img src="/miami/assets/images/customer/storeindex/减@2x.png" className="ctrl minus" onClick={this.handleMinusFood.bind(this,food.id)} />
                                       </div>
                                       const addButton = <div className="cart-ctrl">
                                           <img src="/miami/assets/images/customer/storeindex/加@2x.png" className="addToCartButton"onClick={this.handleAddFood.bind(this,food.id)} />
                                       </div>
                                       const addIcon = <div className="cart-ctrl">
                                           <button className="iconfont icon-jiarugouwuche addIcon"onClick={this.handleAddFood.bind(this,food.id)}></button>
                                       </div>
                                       return(
                                           <div className="food-li" key = {index} >
                                               <div className="food-img">
                                                   <img src={food.icon} className="foodImg" />
                                               </div>
                                               <div className="food-detail">
                                                   <h4>{food.name}</h4>
                                                   <p className="food-text">月售<span> {food.sales} 份</span></p>
                                                   <p className="food-price">￥{food.sale_price}</p>
                                               </div>
                                               {quantityById[food.id]?cartCtrl:addButton}
                                           </div>
                                       )
                                    })}
                               </div>
                            )

                        }
                    )}
                </div>
            </div>
        )
    }
}

FoodList.propTypes = {
    cart: PropTypes.shape({
        addedIds: PropTypes.arrayOf().isRequired,
        quantityById: PropTypes.shape({}).isRequired
    }).isRequired,
    foodListWithCate:PropTypes.arrayOf(PropTypes.shape({})).isRequired
}

function getFoodByCategory(fl,categoryId) {
    if(categoryId=='ALL'){
        return fl
    }else{
        return fl.filter(t => t.cat_id === categoryId)
    }
}

function mapStateToProps(state) {
    return {
        foodListWithCate:state.foodListWithCate,
        cart:state.cart
    }
}

export default connect(
    mapStateToProps
)(FoodList)