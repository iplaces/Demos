/**
 * Created by zsh on 2016/4/23.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import {getRemarkList} from '../actions/remark'
//import {getCartList,addToCart,minusFood} from '../actions/cart'
import { connect } from 'react-redux'
import moment from 'moment'
import RateStar from '../components/RateStar'

class RemarkList extends Component{

    constructor(props){
        super(props)
        const storeId = this.props.params.storeId
        this.props.dispatch(getRemarkList(storeId,0,1))
    }

    componentDidMount(){

        //const storeId = this.props.params.storeId
        //this.props.dispatch(getRemarkList(storeId,0,1))
    }

    render() {
        return(
            <div className="remark-list">
                <hr />
                {this.props.remarkList.map((remark,index) => {
                    return(
                        <div className="remark-li">
                            <div className="remark-header">
                                <span className="remark-id">{remark.userId}</span>
                                <span className="remark-time">{moment(Number(remark.time)).locale('zh-cn').format('YYYY-MM-DD HH:mm:ss')}</span>
                                <span className="remark-trans">送餐时间：{remark.transTime}分钟</span>
                            </div>
                            <div className="rate">
                                <RateStar grade={remark.grade} />
                            </div>
                            <p className="remark-content">{remark.content}</p>
                            <div className="remark-picture">
                                {remark.picUrl.map((imgSrc,index) =>{
                                    return(
                                        <img src={imgSrc} />
                                    )})
                                }
                            </div>
                            <hr />
                        </div>
                    )
                    })
                }
            </div>
        )
    }
}

RemarkList.propTypes = {
    remarkList: PropTypes.arrayOf(PropTypes.shape({})).isRequired
}


function mapStateToProps(state) {
    return {
        remarkList: state.remarkList
    }
}

export default connect(
    mapStateToProps
)(RemarkList)