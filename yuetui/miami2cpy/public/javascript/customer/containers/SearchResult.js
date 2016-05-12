/**
 * Created by zsh on 2016/3/17.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
//import {searchStore} from '../actions/store'
//import {searchFood} from '../actions/food'
import {search} from '../actions/food'

class SearchResult extends Component{

    componentDidMount(){
        //const searchType = this.props.params.searchType
        const searchKey = this.props.params.searchKey
        //if(searchType=='商家'){
        //    this.props.dispatch(searchStore(searchKey))
        //}else{
        //    this.props.dispatch(searchFood(searchKey))
        //}
        this.props.dispatch(search(searchKey))
    }
    //shouldComponentUpdate(nextProps,nextState){//todo 发出两次请求的问题
    //    if(nextProps.params!==this.props.params){
    //        if(nextProps.params.searchType=='商家'){
    //            this.props.dispatch(searchStore(nextProps.params.searchKey))
    //        }else{
    //            this.props.dispatch(searchFood(nextProps.params.searchKey))
    //        }
    //        return false
    //    }else{
    //        return true
    //    }
    //}

    render() {
        //const searchType = this.props.params.searchType
        const searchKey = this.props.params.searchKey
        const foodList = this.props.searchResult.good
        const storeList = this.props.searchResult.store
        const storeResult = storeList.map((store,index) =>{
            if(store.state==1){
                return(
                    <Link to={"/storeIndex/"+store.id} >
                        <div className="rest-li" key = {index} >
                            <div className="rest-img">
                                <img src={store.icon} className="restImg" />
                            </div>
                            <div className="rest-des">
                                <h4>{store.name}</h4>
                                <p>
                                    <span>{store.base_price}元起送</span>
                                    <span>{store.pack_fee}元配送费</span>
                                    <span>{store.cost_time}分钟送达</span>
                                </p>
                                <p>销量{store.sales}</p>
                            </div>
                        </div>
                    </Link>
                )
            }
        })
        //店中饭模式
        const foodResult = foodList.map((item,index) =>{
            const foodItems = item.good.map((food,index) => {
                return(
                    <tr key={index}>
                        <td>
                            <Link to={"/storeIndex/"+(item.store && item.store.id)}>
                                {food.name}
                            </Link>
                        </td>
                        <td className="search-col2">
                            <Link to={"/storeIndex/"+(item.store && item.store.id)}>
                                {food.sale_price}
                            </Link>
                        </td>
                        <td className="search-col3">
                            <Link className='btn ' to={"/storeIndex/"+(item.store && item.store.id)}>
                                去购买
                            </Link>
                        </td>
                        <td className="search-col4">
                            <span>月售{food.sales}份</span>
                        </td>
                    </tr>
                    )
            })
            return(
                <table className='search-foodtable'>
                    <tbody>
                    <tr>
                        <th colspan="4">
                            <h4>
                                <Link to={"/storeIndex/"+(item.store && item.store.id)}>
                                    {item.store && item.store.name}
                                </Link>
                            </h4>
                            <small>月售{item.store.sales}份</small>
                            <small>起送价{item.store.base_price}元</small>
                            <small>配送费{item.store.pack_fee}元</small>
                        </th>
                    </tr>
                    {foodItems}
                    </tbody>
                </table>
            )
        })
        return (
            <div className="container" >
                <div className="inner-wrap">
                    <div className="rest-list">
                        <p>搜索“{searchKey}”的结果：</p>
                        {storeResult}
                        {foodResult}
                    </div>
                </div>
            </div>
        )
    }
}

SearchResult.propTypes = {
    //storeList: PropTypes.arrayOf(PropTypes.shape({
    //    id: PropTypes.number.isRequired,
    //    name: PropTypes.string.isRequired,
    //    store_id: PropTypes.number.isRequired,
    //    icon: PropTypes.string.isRequired,
    //    rank: PropTypes.number.isRequired
    //})).isRequired,
    //foodList: PropTypes.arrayOf(PropTypes.shape({})).isRequired
    searchResult:PropTypes.shape({}).isRequired
}

function mapStateToProps(state) {
    return {
        //storeList: state.storeList,
        //foodList: state.foodList
        searchResult:state.searchResult
    }
}

export default connect(
    mapStateToProps
)(SearchResult)
