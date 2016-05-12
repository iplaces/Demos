/**
 * Created by zsh on 2016/3/11.
 */
import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'
import {Link} from 'react-router'
import { getStoreCategories,getStoresByCatId} from '../actions/store'


class StoreCategories extends Component {
    constructor(props) {
        super(props);
        console.log('constructor this.state() ');
        this.initialState()
    }
    initialState() {
        this.state = {
            curCategoryId:0,
            sort:0,
            curPage:1
        }
    }
    componentDidMount() {
        this.props.dispatch(getStoreCategories())
        this.props.dispatch(getStoresByCatId(0,1))
        const sortBar = document.querySelector('.excavator');
        //var pageHeader = document.querySelector('.page-header');
        //var origOffsetY = sortBar.offsetTop-pageHeader.clientHeight;
        //function onScroll(e) {
        //    window.scrollY >= origOffsetY ? sortBar.classList.add('excavator-sticky') :
        //        sortBar.classList.remove('excavator-sticky');
        //
        //    if(window.scrollY+window.innerHeight == document.body.clientHeight){
        //        const curPage = ++this.state.curPage;
        //        this.setState({curPage});
        //        this.props.dispatch(getStoresByCatId(this.state.curCategoryId,this.state.sort,curPage))
        //    }
        //}
        //document.addEventListener('scroll', onScroll.bind(this));

    }

    handleSearch() {
        let searchKey = $("#search").val();
        this.context.router.push('search/'+searchKey)
    }

    handleGetStores(_catId,_sort) {
        const catId = _catId == 0?0:(_catId || this.state.curCategoryId)
        const sort = _sort || this.state.sort
        const self = this
        $(self.refs['category_'+catId].getDOMNode()).addClass('active').siblings().removeClass('active')
        $(self.refs['sort_'+sort].getDOMNode()).addClass('active').siblings().removeClass('active')
        this.setState({curCategoryId:catId})
        this.setState({sort:sort})
        this.props.dispatch(getStoresByCatId(catId,sort))
    }

    render() {
        const { storeCategories } = this.props

        return (
            <div className="excavator" >
                <div className="excavator-filter" >
                    <span className="excavator-filter-name ">分类：</span>
                    <a href="#" className="excavator-filter-item active"  ref={"category_0"}onClick={this.handleGetStores.bind(this,0,null)}>全部品牌</a>
                    {storeCategories.map((cat,index) =>
                        <a href="#" className="excavator-filter-item" ref={"category_"+cat.id} onClick={this.handleGetStores.bind(this,cat.id,null)}>{cat.name}</a>
                    )}
                    {/*<div className="search">
                        <input autoComplete="off" className="search-input" placeholder="搜索商家、美食……" type="text" id="search"/>
                        <button type="submit" className="iconfont icon-sousuo search-btn" onClick={this.handleSearch.bind(this)}></button>
                        {searchBox}
                    </div>*/}

                </div>
                <hr />
                <div className="excavator-bgbar">
                    <div className="excavator-sort">
                        <span className="excavator-sort-name">排序：</span>
                        <a className="excavator-sort-item active" ref="sort_0" onClick={this.handleGetStores.bind(this,null,0)}>默认</a>
                        <a className="excavator-sort-item" ref="sort_1" onClick={this.handleGetStores.bind(this,null,1)}>销量高</a>
                        <a className="excavator-sort-item" ref="sort_2" onClick={this.handleGetStores.bind(this,null,2)}>评价好</a>
                        <a className="excavator-sort-item" ref="sort_3" onClick={this.handleGetStores.bind(this,null,3)}>配送速度快</a>
                        <a className="excavator-sort-item" ref="sort_4" onClick={this.handleGetStores.bind(this,null,4)}>起送价</a>
                    </div>
                </div>
                <hr />
            </div>


        )
    }
}

StoreCategories.contextTypes = {
    router: React.PropTypes.object.isRequired
}
StoreCategories.propTypes = {
    storeCategories: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
        store_id: PropTypes.number.isRequired,
        icon: PropTypes.string.isRequired,
        rank: PropTypes.number.isRequired
    })).isRequired,
    addToCart: PropTypes.func.isRequired,
    searchResult:PropTypes.shape({}).isRequired
}

function mapStateToProps(state) {
    return {
        storeCategories: state.storeCategories,
        searchResult:state.searchResult

    }
}

export default connect(
    mapStateToProps
)(StoreCategories)
