/**
 * Created by zsh on 2016/3/14.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { setFoodCategory } from '../actions/food'
import { connect } from 'react-redux'

class FoodCategories extends Component{

    setCategory(categoryId){
        this.props.dispatch(setFoodCategory(categoryId))
    }

    render() {
        return (
            <div className="food-category">
                {/*<a className="food-category-item" onClick={this.setCategory.bind(this,'ALL')}>全部</a>*/}
                {this.props.foodCategory.map((category,index) =>
                    <div className="food-category-item">
                        <a  key = {index} onClick={this.setCategory.bind(this,category.id)}>{category.name}</a>
                    </div>
                 )}
            </div>
        )
    }
}
FoodCategories.propTypes = {
    foodCategory: PropTypes.arrayOf(PropTypes.shape({})).isRequired
}


function mapStateToProps(state) {
    return {
        foodCategory: state.foodCategory,
    }
}

export default connect(
    mapStateToProps
)(FoodCategories)