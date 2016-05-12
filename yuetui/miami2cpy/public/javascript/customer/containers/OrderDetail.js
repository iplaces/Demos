/**
 * Created by zsh on 2016/3/30.
 */
import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'
import {Link} from 'react-router'
import {getOrderDetail,createComment} from'../actions/order'
import moment from 'moment'

class OrderDetail extends Component{

    constructor(props){
        super(props)
        this.state = {
            rate:0,
            eventNotBond:true
        }
    }
    componentDidMount(){
        const {orderId} = this.props.params
        this.props.dispatch(getOrderDetail(orderId))
    }

    getStarText(num){
        switch(num){
            case 1:
                return '失望'
            case 2:
                return '较差'
            case 3:
                return '一般'
            case 4:
                return '满意'
            case 5:
                return '惊喜'
            default:
                return '点击星星评分'
        }
    }

    componentDidUpdate(){
        const self =this
        console.log("eventNotBond===="+this.state.eventNotBond)
        if(this.props.orderDetail.state==4 && this.state.eventNotBond){
            this.setState({eventNotBond:false})
            $('.rate-star a').mouseenter(function(){
                const preAll = $(this).prevAll();
                const nextAll = $(this).nextAll();
                $('.rate-star-text').text(self.getStarText(preAll.length+1));
                $('.rate-star').attr('class','rate-star level'+(preAll.length+1))
                preAll.addClass('active')
                $(this).addClass('active')
                nextAll.removeClass('active')
            }).click(function(){
                const rate = $(this).prevAll().length +1
                self.setState({rate:rate})
            }).mouseleave(function(){
                $('.rate-star a:eq('+(self.state.rate-1)+')').trigger('mouseenter');
                $('.rate-star-text').text(self.getStarText(self.state.rate));
            })
            $('.unrated-ratelist button').click(function(){
                const comment = $('.rate-content textarea').val();
                const data = {
                    orderId: self.props.orderDetail.orderId,
                    storeId: self.props.orderDetail.store.storeId,
                    dishGrade:self.state.rate,
                    transGrade:5,
                    comment:comment
                }
                self.props.dispatch(createComment(data))
            })
        }else if(this.props.orderDetail.state==5){
            const star = $('.rate-star a:eq('+(self.props.orderDetail.comment.dishGrade-1)+')');
            star.addClass('active')
            star.prevAll().addClass('active')
            star.nextAll().removeClass('active')
            $('.rate-star').attr('class','rate-star level'+(self.props.orderDetail.comment.dishGrade))

        }

    }

    render(){
        const {orderDetail} = this.props
        const list = orderDetail.goodsDetail?orderDetail.goodsDetail.map(food =>
            <div className="orderprogress-totalrow">
                <span className="cell name">{food.goodsName}</span>
                <span className="cell quantity">{food.num}</span>
                <span className="cell price">￥{(food.num*food.price).toFixed(2)}</span>
            </div>
        ):""

        const unrated = orderDetail.state==4?<div className="unrated-rate">
            <div className="unrated-ratelist">
                <span className="unrated-ratelist-label">
                    评价本单：
                </span>
                <div className="unrated-ratelist-content">
                    <p className="rate-star star level0">
                        <span >
                            <a className="iconfont icon-pingfenxingwu" id="star1"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star2"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star3"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star4"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star5"></a>
                        </span>
                        <span className="rate-star-text">点击星星打分</span>
                    </p>
                    <div className="rate-content">
                        <textarea>
                        </textarea>
                    </div>
                </div>
            </div>
            <div className="unrated-ratelist offset">
                <button className="btn-primary btn-lg">提交评价</button>
            </div>
        </div>:""
        const rated = orderDetail.state==5?<div className="unrated-rate">
            <div className="unrated-ratelist">
                <span className="unrated-ratelist-label">
                    您对本单的评价：
                </span>
                <div className="unrated-ratelist-content">
                    <p className="rate-star star level0">
                        <span >
                            <a className="iconfont icon-pingfenxingwu" id="star1"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star2"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star3"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star4"></a>
                            <a className="iconfont icon-pingfenxingwu" id="star5"></a>
                        </span>
                        <span className="rate-star-text">{this.state.rate}颗星</span>
                    </p>
                    <div className="rate-content">
                        <textarea value={orderDetail.comment.comment || "无文字评论"} disabled="disabled">
                        </textarea>
                    </div>
                </div>
            </div>
        </div>:""
        return(
            orderDetail.goodsDetail?<div className="inner-wrap">
                <ul className="profile-sidebar"></ul>
                <div className="profile-panel">
                    <h3 className="profile-paneltitle">
                        <span >订单详情</span>
                    </h3>
                    <div className="profile-panelcontent">
                        <div className="orderprogress-rstinfo">
                            <Link to={"/storeIndex/"+orderDetail.store.storeId} >
                                <img className="orderprogress-rstimg" src={orderDetail.store.icon} alt={orderDetail.store.name} />
                            </Link>
                            <div className="orderprogress-rstgrid">
                                <h4 className="orderprogress-rstname">
                                    <Link to={"/storeIndex/"+orderDetail.store.storeId} >
                                        {orderDetail.store.name}
                                    </Link>
                                </h4>
                                <div className="orderprogress-rstextra">
                                    <span >订单号：{orderDetail.orderId}</span>
                                    <span >商家电话：{orderDetail.store.contact}</span>
                                </div>
                            </div>
                        </div>
                        <div className="orderprogress-cardtable">
                            <div className="orderprogress-cardcell">
                                <div className="orderprogress-total">
                                    <div className="orderprogress-totalrow orderprogress-totaltitle">
                                        <span className="cell name">菜品</span>
                                        <span className="cell quantity">数量</span>
                                        <span className="cell price">小计（元）</span>
                                    </div>
                                    {list}
                                    <div className="orderprogress-totalrow">
                                        <span className="cell name">配送费</span>
                                        <span className="cell quantity"></span>
                                        <span className="cell price">{orderDetail.packFee.toFixed(2)}</span>
                                    </div>
                                    <div className="orderprogress-totalactual">
                                        实际支付：<span>￥{orderDetail.totalFee.toFixed(2)}</span>
                                    </div>
                                </div>
                            </div>
                            <div className="orderprogress-cardcell">
                                <div className="orderprogress-deliveryinfo">
                                    <h5 className="orderprogress-deliverytitle">配送信息</h5>
                                    <div className="orderprogress-deliverygroup">
                                        <p>
                                            <span className="orderprogress-deliverykey">下单时间：</span>
                                            <span className="">{moment(orderDetail.createTime).locale('zh-cn').format('llll')}</span>
                                        </p>
                                        <p>
                                            <span className="orderprogress-deliverykey">期望送达时间：</span>
                                            <span className="">{moment(orderDetail.arriveTime).locale('zh-cn').format('llll')}</span>
                                        </p>
                                    </div>
                                    <div className="orderprogress-deliverygroup">
                                        <p>
                                            <span className="orderprogress-deliverykey">联系人：</span>
                                            <span className="">{orderDetail.recipient}</span>
                                        </p>
                                        <p>
                                            <span className="orderprogress-deliverykey">联系电话：</span>
                                            <span className="">{orderDetail.contact}</span>
                                        </p>
                                        <p>
                                            <span className="orderprogress-deliverykey">收货地址：</span>
                                            <span className="">{orderDetail.address}</span>
                                        </p>
                                    </div>
                                    <div className="orderprogress-deliverygroup">
                                        <p>
                                            <span className="orderprogress-deliverykey">备注：</span>
                                            <span className="">{orderDetail.remark}</span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        {unrated}
                        {rated}
                    </div>
                </div>
            </div>:<div>请稍候……</div>

        )
    }
}

OrderDetail.propTypes = {
    orderDetail: PropTypes.shape({}).isRequired
}


function mapStateToProps(state) {
    return {
        orderDetail: state.orderDetail
    }
}

export default connect(
    mapStateToProps
)(OrderDetail)