/**
 * Created by zsh on 2016/3/16.
 */
import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'
import {Link} from 'react-router'
import linkState from 'react-link-state'
import moment from 'moment'
import swal from 'sweetalert'
import 'sweetalert/dist/sweetalert.css'
import {getOrderList,confirmReceipt,cancelOrder,reqRefund,createComment} from'../actions/order'
import {MiamiModal} from '../util/Modal'
import {Remark} from './../components/Remark'

class OrderList extends Component{
    constructor(props){
        super(props)
        this.initialState()
    }
    initialState(){
        this.state = {
            eventNotBond:true,
            comment:{
                orderId:'',
                storeId:'',
                dishGrade:0,
                transGrade:0,
                picList:[]
            },
            refund:{
                orderId:'',
                reason:''
            }
        }
    }
    componentDidMount(){
        this.props.dispatch(getOrderList())
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

    componentDidUpdate() {
        const self =this
        if(this.state.eventNotBond) {
            this.setState({eventNotBond: false})
            $('.rate-star a').mouseenter(function () {
                const preAll = $(this).prevAll();
                const nextAll = $(this).nextAll();
                $('.rate-star-text').text(self.getStarText(preAll.length + 1));
                $('.rate-star').attr('class', 'rate-star level' + (preAll.length + 1))
                preAll.addClass('active')
                $(this).addClass('active')
                nextAll.removeClass('active')
            }).click(function () {
                const rate = $(this).prevAll().length + 1
                self.setState({rate: rate})
            }).mouseleave(function () {
                $('.rate-star a:eq(' + (self.state.rate - 1) + ')').trigger('mouseenter');
                $('.rate-star-text').text(self.getStarText(self.state.rate));
            })
            //$('.unrated-ratelist button').click(function () {
            //    const comment = $('.rate-content textarea').val();
            //    const data = {
            //        orderId: self.props.orderDetail.orderId,
            //        storeId: self.props.orderDetail.store.storeId,
            //        dishGrade: self.state.rate,
            //        transGrade: 5,
            //        comment: comment
            //    }
            //    self.props.dispatch(createComment(data))
            //})
        }
    }

    confirmReceipt(orderId,storeId) {
        const self = this
        swal({
            title: "确认送达?",
            text: "请在拿到您的餐品后再确认送达!",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消",
            closeOnConfirm: true
        }, function(){
            self.props.dispatch(confirmReceipt(orderId,storeId))
        })
    }

    cancelOrder(orderId) {
        const self = this
        swal({
            title: "确定取消?",
            text: "取消订单之后您需要重新订餐!",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消",
            closeOnConfirm: true
        }, function(){
            self.props.dispatch(cancelOrder(orderId))
        })
    }

    triggerDialog(id) {
        const  dialog = document.getElementById(id)
        dialog.style.visibility = dialog.style.visibility === 'visible'? 'hidden' : 'visible'
        const  modal = document.getElementById('dialog-overlay')
        modal.style.visibility = modal.style.visibility === 'visible'? 'hidden' : 'visible'
    }

    openRefundDialog(orderId) {
        this.setState({refund:{orderId:orderId}})
        this.triggerDialog("refunddialog")
    }

    reqRefund(){
        this.triggerDialog("refunddialog")
        const data={
            orderId:this.state.refund.orderId,
            customerDesc:this.state.refund.reason
        }
        this.props.dispatch(reqRefund(data))
    }

    openCommentDialog(orderId,storeId) {
        //this.setState({comment:{orderId:orderId,storeId:storeId}})
        //this.triggerDialog("commentdialog")
        this.refs.remarkModal.open(orderId,storeId)
    }

    createComment() {
        const self =this
        const comment = $('.rate-content textarea').val();
        const data = {
            orderId: this.state.comment.orderId,
            storeId: this.state.comment.storeId,
            dishGrade: this.state.dishGrade,
            transGrade: this.state.transGrade,
            comment: comment
        }
        self.props.dispatch(createComment(data))
        this.triggerDialog("commentdialog")
    }

    render() {
        const left = (window.innerWidth - 760)/2
        const top = (window.innerHeight - 480)/2
        const dialogOverlay = <div id="dialog-overlay"></div>;
        const refundDialog =
            <div id="refunddialog" style={{top:top,left:left,width:'760px'}}>
                <div className="addressdialog-close" onClick={this.triggerDialog.bind(this,"refunddialog")}>x</div>
                <div className="addressdialog-header">申请退款</div>
                <div className="addressdialog-content">
                    <div className="addressform">
                        <div>
                            <div className="addressformfield">
                                <label>退款原因</label>
                                <textarea placeholder="请输入退款详细原因" valueLink={linkState(this,'refund.reason')}></textarea>
                            </div>
                        </div>
                        <div className="addressform-buttons">
                            <button onClick={this.reqRefund.bind(this)}>提交</button>
                            <button onClick={this.triggerDialog.bind(this,"refunddialog")}>取消</button>
                        </div>
                    </div>
                </div>
            </div>
        const unrated = <div className="unrated-rate">
            <div className="unrated-ratelist">
                <span className="unrated-ratelist-label">
                    菜品：
                </span>
                <div className="unrated-ratelist-content">
                    <p className="rate-star star level0">
                        <span >
                            <a className="iconfont icon-pingfenxingwu" ></a>
                            <a className="iconfont icon-pingfenxingwu"></a>
                            <a className="iconfont icon-pingfenxingwu"></a>
                            <a className="iconfont icon-pingfenxingwu"></a>
                            <a className="iconfont icon-pingfenxingwu"></a>
                        </span>
                        <span className="rate-star-text">点击星星打分</span>
                    </p>
                    {/*<div className="rate-content">
                        <textarea>
                        </textarea>
                    </div>*/}
                </div>
                <input type="file" id="imgBtn" />
            </div>
        </div>
        const commentDialog =
            <div id="commentdialog" style={{top:top,left:left,width:'760px'}}>
                <div className="addressdialog-close" onClick={this.triggerDialog.bind(this,"commentdialog")}>x</div>
                <div className="addressdialog-header">评价本单</div>
                <div className="addressdialog-content">
                    <div className="addressform">
                        {unrated}
                        <div className="addressform-buttons">
                            <button onClick={this.createComment.bind(this)}>提交</button>
                            <button onClick={this.triggerDialog.bind(this,"commentdialog")}>取消</button>
                        </div>
                    </div>
                </div>
            </div>
        const commentModal = <Remark ref="remarkModal" dispatch={this.props.dispatch}> </Remark>
        const orderList = this.props.orderList
        const noOrders = orderList.length===0
        const getStates = (state,orderId) => {
            switch (state){
                case 0:
                    return <h3>去支付</h3>
                case 1:
                    return <h3>等待商家接单</h3>
                case 2:
                    return <h3>等待商家配送</h3>
                case 3:
                    return <h3>卖家已发货</h3>
                case 4:
                    return <h3>等待评价</h3>
                case 5:
                    return <h3>订单完成</h3>
                case 6:
                    return <h3>支付失败</h3>
                case 7:
                    return <h3>订单撤销</h3>
                case 8:
                    return <h3>拒绝接单</h3>
                case 9:
                    return <h3>申请退款</h3>
                default:
                    return <h3>订单异常</h3>

            }
        }
        const getOperation = (state,orderId,storeId) => {
            switch (state){
                case 0:
                    return <span className="ordertimeline-handle-group">
                        <Link to={"/order/pay/"+orderId}>立即支付</Link>
                        <a onClick={this.cancelOrder.bind(this,orderId)}>取消订单</a>
                    </span>
                case 1:
                    return <span className="ordertimeline-handle-group">
                        <Link to={"/storeIndex/"+storeId}>再来一单</Link>
                        <a onClick={this.cancelOrder.bind(this,orderId)}>取消订单</a>
                    </span>
                case 2:
                    return <span className="ordertimeline-handle-group">
                        <Link to={"/storeIndex/"+storeId}>再来一单</Link>
                        <a onClick={this.cancelOrder.bind(this,orderId)}>取消订单</a>
                    </span>
                case 3:
                    return <span className="ordertimeline-handle-group">
                        <a onClick={this.confirmReceipt.bind(this,orderId,storeId)}>确认收货</a>
                        <a onClick={this.openRefundDialog.bind(this,orderId)}>申请退款</a>
                    </span>
                case 4:
                    return <a onClick={this.openCommentDialog.bind(this,orderId,storeId)}>评价本单</a>
                case 5:
                    return <Link to={"/storeIndex/"+storeId}>再来一单</Link>
                case 6:
                    return <Link to={"/storeIndex/"+storeId}>再来一单</Link>
                case 7:
                    return <Link to={"/storeIndex/"+storeId}>再来一单</Link>
                case 8:
                    return <Link to={"/storeIndex/"+storeId}>再来一单</Link>
                case 9:
                    return <Link to={"/storeIndex/"+storeId}>再来一单</Link>
                default:
                    return <Link to={"/storeIndex/"+storeId}>再来一单</Link>

            }
        }
        const list = noOrders?"":
            orderList.map(order =>
                    <tr className="timeline">
                        <td className="order-timeline-time">
                            <p className="order-timeline-title">{moment(order.createTime).locale('zh-cn').format('MMMM Do YYYY, h:mm:ss a')}</p>
                        </td>
                        <td className="order-timeline-avatar">
                            <Link to={"/storeIndex/"+order.store.storeId}>{order.store.name}</Link>
                            <img src={order.store.icon} width="60px" height="60px" />
                        </td>
                        <td className="order-timeline-info">
                            <p className="order-timeline-info-food">
                                {order.goodsDetail[0].goodsName}等{order.goodsDetail.length}款商品
                            </p>
                        </td>
                        <td className="order-timeline-amount">
                            <h3>￥{order.totalFee.toFixed(2)}</h3></td>
                        <td className="order-timeline-status">
                            {getStates(order.state,order.orderId)}
                        </td>
                        <td className="order-timeline-handle">
                            <Link to={"/order/detail/"+order.orderId} >订单详情</Link>
                            {getOperation(order.state,order.orderId,order.store.storeId)}
                        </td>
                    </tr>
            )

        return (
            <div className="inner-wrap">
                <ul className="profile-sidebar"></ul>
                <div className="profile-panel">
                    <h3 className="profile-panel-title">
                        <span >近三个月订单</span>
                    </h3>
                    <div className="profile-panel-content">
                        <table className="order-list ">
                            <thead>
                            <tr >
                                <th >下单时间</th>
                                <th >店铺名称</th>
                                <th >订单详情</th>
                                <th >支付金额</th>
                                <th >状态</th>
                                <th >操作</th>
                            </tr>
                            </thead>
                            <tbody >
                            {list}
                            </tbody>
                        </table>
                    </div>
                </div>
                {dialogOverlay}
                {refundDialog}
                {commentModal}
            </div>
        )
    }
}

OrderList.propTypes = {
    orderList: PropTypes.arrayOf({}).isRequired
}


function mapStateToProps(state) {
    return {
        orderList: state.orderList
    }
}

export default connect(
    mapStateToProps
)(OrderList)