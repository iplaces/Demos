/**
 * Created by zsh on 2016/3/11.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'

class StoreList extends Component{
    componentDidMount(){
        console.log("storeList==="+this.props.storeList)
    }
    render() {
        const { storeList } = this.props
        return (
            <div className="rest-list">
                {storeList.map((store,index) =>{
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
                                            <span>{'起送：￥'+store.base_price}</span>
                                            <span>{'配送：￥'+store.pack_fee}</span>
                                            <span>{store.cost_time+'分钟'}</span>
                                        </p>
                                        <p>月售{store.sales}份</p>
                                    </div>
                                </div>
                            </Link>
                        )
                    }
                }
                )}
            </div>
        )
    }
}

StoreList.propTypes = {
    StoreList: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
        store_id: PropTypes.number.isRequired,
        icon: PropTypes.string.isRequired,
        rank: PropTypes.number.isRequired
    })).isRequired,
    addToCart: PropTypes.func.isRequired
}

function mapStateToProps(state) {
    return {
        storeList: state.storeList
    }
}

export default connect(
    mapStateToProps
)(StoreList)
