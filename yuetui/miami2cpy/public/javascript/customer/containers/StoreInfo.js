/**
 * Created by zsh on 2016/4/1.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { getStoreInfo } from '../actions/store'
import { connect } from 'react-redux'
import RateStar from '../components/RateStar'

class StoreInfo extends Component{

    componentDidMount(){
        const storeId = this.props.storeId
        this.props.dispatch(getStoreInfo(storeId))
    }

    render() {
        const storeId = this.props.storeId
        const storeInfo = this.props.storeInfo
        return (
            <div className="shopguide ">
                <div className="shopguide-info">
                    <img src={storeInfo.icon}/>
                    <div className="shopguide-info-wrapper" >
                        <div>
                            <h1><Link to={"/storeIndex/"+storeInfo.id} >{storeInfo.name}</Link></h1>
                        </div>
                        <div className="shopguide-info-rate">
                            <span>店铺评价：<RateStar grade={storeInfo.grades} /></span>
                        </div>
                        <p className="shopguide-info-rate">
                            <img src="/miami/assets/images/customer/storeindex/phone@2x.png" /> {storeInfo.contact?storeInfo.contact:" 该商家未填写联系电话"}
                        </p>
                        <p className="shopguide-info-rate">
                            <img src="/miami/assets/images/customer/storeindex/address@2x.png" /> {storeInfo.address?storeInfo.address:" 该商家未填写店铺地址"}
                        </p>
                    </div>
                    <div className="shopguide-server">
                        <span>
                            <p>起送价</p>
                            <p className="shopguide-server-value">￥{storeInfo.base_price}</p>
                        </span>
                        <span>
                            <p>配送费</p>
                            <p className="shopguide-server-value">￥{storeInfo.pack_fee}</p>
                        </span>
                        <span>
                            <p>送达时间</p>
                            <p className="shopguide-server-value">{storeInfo.cost_time}分钟</p>
                        </span>
                    </div>
                </div>
                <div className="shopguide-tab">
                    <div className="shopguide-tab-item">
                        <Link to={"/storeIndex/"+storeId} >菜单</Link>
                    </div>
                    <div className="shopguide-tab-item">
                        <Link to={"/storeIndex/"+storeId+"/remark"} >评价</Link>
                    </div>
                </div>
            </div>
        )
    }
}
StoreInfo.propTypes = {
    storeInfo: PropTypes.shape({}).isRequired
}


function mapStateToProps(state) {
    return {
        storeInfo: state.storeInfo
    }
}

export default connect(
    mapStateToProps
)(StoreInfo)