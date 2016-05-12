/**
 * Created by zsh on 2016/3/17.
 */
import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'
import {Link} from 'react-router'
//import {searchStore} from '../actions/store'
//import {searchFood} from '../actions/food'
import { search} from '../actions/food'

export default class Header extends Component{

    constructor(props) {
        super(props);
        this.initialState()
    }
    initialState(){
        this.state={
            token:'',
            inputFocus:false
        }
    }
    componentDidMount(){
        const self =this
        $("#search").keyup(function(event){
            const token = $(this).val()
            if(token!=""&&token!=self.state.token){
                self.props.dispatch(search(token))
                self.setState({token:token})
            }
        }).focus(function(){
            if(self.props.searchResult.good || self.props.searchResult.store)
                $(".searchbox").show()
        }).blur(function(){
            $(".searchbox").hide()
        })
    }

    handleSearch() {
        let searchKey = $("#search").val();
        this.context.router.push('search/'+searchKey)
    }

    render() {
        const box = $(".searchbox")
        if((this.props.searchResult.good || this.props.searchResult.store) && $('#search').is(':focus')){
            box.show()
        }else{
            box.hide()
        }
        const foodlist = this.props.searchResult.good?this.props.searchResult.good.map((item) =>
            item.good.map(food =>
                <li>
                    <span className="price">￥{food.sale_price}</span>
                    <span className="food-wrapper">
                        <Link to={"/storeIndex/"+food.store_id+"#"+food.id} className="name">{food.name}</Link>
                        <Link to={"/storeIndex/"+food.store_id} className="restaurant">{item.store.name}</Link>
                    </span>
                </li>
            )
        ):""
        const rstlist = this.props.searchResult.store?this.props.searchResult.store.map((store) =>
            <li>
                <Link to={"/storeIndex/"+store.id}>
                    <span className="time">月销{store.sales}份</span>
                    <span className="name">{store.name}</span>
                </Link>
            </li>
        ):""
        const searchBox = <div className="searchbox">
            <div className="searchbox-list searchbox-rstlist show-separator">
                <ul>
                    {rstlist}
                </ul>
            </div>
            <div className="searchbox-list searchbox-foodlist">
                <ul>
                    {foodlist}
                </ul>
            </div>
        </div>
        return (
           <div className="wrapper">
                <div className="logo-search">
                    <div className="logo">
                        <Link to="/"><span className="for-img-middle"/><img src="/miami/assets/images/customer/header/logo@2x.png" alt="国贸外卖" /></Link>
                    </div>
                    <div className="search">
                        <input autoComplete="off" className="search-input" placeholder="搜索商家、美食……" type="text" id="search"/>
                        <button type="submit" className="iconfont icon-sousuo search-btn" onClick={this.handleSearch.bind(this)}/>
                        {searchBox}
                    </div>
                    <Link to={"/order/list"} className="order-entry"><span className="for-img-middle"/><img src="/miami/assets/images/customer/header/8@2x.png" /><span className="text">查看外卖订单</span></Link>
                </div>
               {this.props.children}
           </div>
        )
    }
}

Header.contextTypes = {
    router: React.PropTypes.object.isRequired
}

Header.propTypes = {
    searchResult:PropTypes.shape({}).isRequired
}

function mapStateToProps(state) {
    return {
        searchResult:state.searchResult
    }
}

export default connect(
    mapStateToProps
)(Header)