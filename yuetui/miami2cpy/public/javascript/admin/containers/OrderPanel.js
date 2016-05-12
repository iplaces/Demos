import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {
    getOrderList,
    getOrderSpans
} from '../actions'
import NavBar from './NavBar'
import {ajaxJsonPost, ajaxSimpleGet }  from '../util/sk-react'
import moment from 'moment'

class OrderPanel extends Component {
    constructor(props) {

        super(props);
        this.initialState()

    }

    initialState() {
        this.state = {
            currentState: '',
            currentPage: 1 ,
            currentRefund: 0,
            currentOrderId: 0,
            store_desp: '',
            num: 0
        };
    }

    componentWillMount() {
        console.log('componentWillMount')

        this.props.dispatch(getOrderList(this.state.currentState, this.state.currentPage))
        this.props.dispatch(getOrderSpans())
    }

    componentWillReceiveProps(nextProps) {
        console.log('componentWillReceiveProps')
        const { orders } = nextProps

        this.setState({
            num: orders.num
        });

    }

    componentDidMount() {
        console.log('componentDidMount')
        this.tuisong()
    }

    componentWillUnmount() {
        console.log('componentWillUnmount')
        this.initialState()
    }

    tuisong() {
        console.log('tuisong')
        var self = this

        if (window.WebSocket) {
            console.log("this support websocket");
        } else {
            console.log("does not support websocket!");
        }


        function appendTweet(text) {
            var text=JSON.parse(text);
            var errcode =text.errCode;
            var errmsg = text.msg;
            console.log(text)
            //if (errcode != 0) {
            //    console.log('errCode=' + errcode + ', errMsg=' + errmsg);
            //    alert('错误: ' + errmsg);
            //} else {
            //    alert(errmsg);
            //}
            toastr.info('新订单来了!')

            self.props.dispatch(getOrderList(self.state.currentState, self.state.currentPage))
            self.props.dispatch(getOrderSpans())

        }


        function connect(attempt) {
            var connectionAttempt = attempt;
            var tweetSocket = new WebSocket(
                "ws://" + $CONF$.url + "/miami/customer/order/socket?storeId=" + $CONF$.id);
            tweetSocket.onmessage = function (event) {
                console.log(event);
                appendTweet(event.data);
            };
            tweetSocket.onopen = function () {
                connectionAttempt = 1;
            };
            tweetSocket.onclose = function () {
                if (connectionAttempt <= 3) {
                    appendTweet("WARNING: Connection with the server lost, attempting to reconnect. Attempt number " + connectionAttempt);
                    setTimeout(function () {
                        connect(connectionAttempt + 1);
                    }, 5000);
                } else {
                    toastr.error("The connection with the server was lost. Please try again later. Sorry about that.");
                }
            };
        }
        connect(1);

    }


    selectOrdersByState(state) {
        console.log('selectOrdersByState')
        $(".active").attr('class', '')
        $("#page" + state).attr('class', 'active')
        this.setState({
            currentState: state,
            currentPage: 1
        })
        this.props.dispatch(getOrderList(state, 1))
        this.props.dispatch(getOrderSpans())
    }

    acceptOrder(id) {
        var self = this
        console.log('acceptOrder')
        var url = "/miami/admin/order/accept?id=" + id
        var successFunc = function (data) {
            self.props.dispatch(getOrderList(this.state.currentState, this.state.currentPage))
            self.props.dispatch(getOrderSpans())
            toastr.success("接单成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)

    }

    refuseOrder(id) {
        var self = this
        console.log('refuseOrder')
        var url = "/miami/admin/order/refuse?id=" + id
        var successFunc = function (data) {
            self.props.dispatch(getOrderList(this.state.currentState, this.state.currentPage))
            self.props.dispatch(getOrderSpans())
            toastr.success("拒绝接单成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    deliverOrder(id) {
        var self = this
        console.log('deliverOrder')
        var url = "/miami/admin/order/deliver?id=" + id
        var successFunc = function (data) {
            self.props.dispatch(getOrderList(this.state.currentState, this.state.currentPage))
            self.props.dispatch(getOrderSpans())
            toastr.success("确认送达成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    acceptRefund(oId, rId) {
        var self = this
        console.log('acceptRefund')
        var url = "/miami/admin/refund/accept?orderId=" + oId +
            "&refundId=" + rId
        var successFunc = function (data) {
            self.props.dispatch(getOrderList(this.state.currentState, this.state.currentPage))
            self.props.dispatch(getOrderSpans())
            toastr.success("同意退款成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)

    }

    showRefuseRefundModal(oId, rId) {
        this.state.currentOrderId = oId;
        this.state.currentRefund = rId;
        $('#refuseRefundModal').modal('show');

    }

    refuseRefund() {
        var self = this
        console.log('acceptRefund')
        var msg = this.refs.store_desp.getDOMNode().value

        var url = "/miami/admin/refund/refuse?orderId=" + this.state.currentOrderId +
            "&refundId=" + this.state.currentRefund + "&msg=" + msg
        var successFunc = function (data) {
            self.props.dispatch(getOrderList(this.state.currentState, this.state.currentPage))
            self.props.dispatch(getOrderSpans())
            $('#refuseRefundModal').modal('hide');
            toastr.success("拒绝退款成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)

    }

    listByPageNo(pageNo) {
        var self = this
        self.setState({currentPage: pageNo})
        self.props.dispatch(getOrderList(this.state.currentState, pageNo))
        self.props.dispatch(getOrderSpans())
    }

    lastPage() {
        var pageNo = parseInt(this.state.currentPage) - 1;
        this.listByPageNo(pageNo);
    }

    nextPage() {
        var pageNo = parseInt(this.state.currentPage) + 1;
        this.listByPageNo(pageNo);
    }

    firstPage() {
        var pageNo = 1;
        this.listByPageNo(pageNo);
    }

    endPage() {
        var pageNo = parseInt(this.state.num);
        this.listByPageNo(pageNo);
    }

    jump() {
        var pageNo = parseInt(this.refs.jump.getDOMNode().value.trim());
        if (pageNo > this.state.num) {
            toastr.error("只有" + this.state.num + "页哦");
        } else {
            this.listByPageNo(pageNo);
        }
    }

    render() {
        console.log('render')
        console.log(this.props)
        const { orders, spans } = this.props
        var pageNums = parseInt(orders.num);
        var pageArr=[];

        for(var i =1;i<pageNums+1;i++){
            if(Math.abs(i - this.state.currentPage) < 5){
                pageArr.push(<li className={this.state.currentPage == i ? "active" : ""}><a onClick={this.listByPageNo.bind(this,i)}>{i}</a></li>);
            }
        }
        var before = [
            <li><a href="javascript:;" onClick={this.firstPage.bind(this)}>&laquo;</a></li>,
            <li id="last"><a href="javascript:;" onClick={this.lastPage.bind(this)}>&lt;</a></li>
        ];
        var after = [
            <li id="next"><a href="javascript:;" onClick={this.nextPage.bind(this)}>&gt;</a></li>,
            <li><a href="javascript:;" onClick={this.endPage.bind(this)}>&raquo;</a></li>
        ];
        var p = <ul className="pagination pagination-sm no-margin pull-right">
            {this.state.currentPage==1?"":before}
            {pageArr}
            {this.state.currentPage==pageNums?"":after}
            &nbsp;&nbsp;&nbsp;&nbsp;<span>到第<input type="number" ref="jump" style={{maxWidth:"80px"}}/>页</span>
            <button className="btn btn-default" onClick={this.jump.bind(this)}>跳转</button>
        </ul>;

        return (
            <div>
                <NavBar />
                <div className="container" style={{paddingTop: '70px' }}>
                    <div className="panel panel-default">
                        <div className="panel-heading">订单管理</div>
                        <div className="panel-body">
                            <ul className="nav nav-tabs">
                                <li role="presentation" id="page" className="active">
                                    <a onClick={this.selectOrdersByState.bind(this,'')}> 全部订单 </a>
                                </li>
                                <li role="presentation" id="page1">
                                    <a onClick={this.selectOrdersByState.bind(this,'1')}> 未接单 <span
                                        className="badge">{spans.unAccepts}</span> </a>
                                </li>
                                <li role="presentation" id="page2">
                                    <a onClick={this.selectOrdersByState.bind(this,'2')}> 未送出 <span
                                        className="badge">{spans.unArrived}</span> </a>
                                </li>
                                <li role="presentation" id="page9">
                                    <a onClick={this.selectOrdersByState.bind(this,'9')}> 退款订单 <span
                                        className="badge">{spans.applyRefund}</span> </a>
                                </li>
                            </ul>
                            <table className="table table-bordered table-hover" style={{marginTop: '10px'}}>
                                <thead>

                                {this.state.currentState != 9 ?
                                    <tr>
                                        <th>订单ID</th>
                                        <th>商品</th>
                                        <th>联系方式</th>
                                        <th>备注</th>
                                        <th>总价</th>
                                        <th>状态</th>
                                        <th>预计送达时间</th>
                                        <th>创建时间</th>
                                        <th>操作</th>
                                    </tr>
                                    :
                                    <tr>
                                        <th>订单ID</th>
                                        <th>商品</th>
                                        <th>联系方式</th>
                                        <th>退款理由</th>
                                        <th>总价</th>
                                        <th>申请退款金额</th>
                                        <th>创建时间</th>
                                        <th>申请退款时间</th>
                                        <th>状态</th>
                                        <th>操作</th>
                                    </tr>
                                }

                                </thead>
                                <tbody>
                                {console.log(orders.list)}
                                {this.state.currentState != '9' ?
                                    orders.list.map(item =>

                                        <tr key={item.id}>
                                            <td>{item.id}</td>
                                            <td>
                                                {item.goods.map(g =><p key={item.id+g.good_name}>{g.good_name} * {g.good_num}</p>)}
                                            </td>
                                            <td>{item.recipient}，{item.contact}，{item.address}</td>
                                            <td>{item.remark}</td>
                                            <td>{item.total_fee}</td>
                                            <td>{item.state}</td>
                                            <td>{moment(item.arrive_time).format("YYYY-M-D HH:mm")}</td>
                                            <td>{moment(item.create_time).format("YYYY-M-D HH:mm")}</td>
                                            <td>
                                                {item.state == "等待接单" ?
                                                    <div className="btn-group" role="group" aria-label="...">
                                                        <button className="" type="button"
                                                                onClick={this.acceptOrder.bind(this,item.id)}>接单
                                                        </button>
                                                        <button className="" type="button"
                                                                onClick={this.refuseOrder.bind(this,item.id)}>拒绝接单
                                                        </button>
                                                    </div> : undefined
                                                }
                                                {item.state == "等待发货" ?
                                                    <button className="" onClick={this.deliverOrder.bind(this, item.id)}>
                                                        确认送达</button> : undefined
                                                }
                                            </td>
                                        </tr>
                                    )
                                    :
                                    orders.list.map(item => {
                                        console.log('render *******88')
                                        console.log(item.refunds)
                                        if(item.refunds.length == 0)
                                            var currRefund = {customer_desp: '',
                                            amount: 0}
                                        else currRefund = item.refunds[0]
                                        console.log(currRefund)



                                        return <tr key={item.id}>
                                            <td>{item.id}</td>
                                            <td>
                                                {item.goods.map(g =><p key={item.id+g.good_name}>{g.good_name}
                                                    * {g.good_num}</p>)}
                                            </td>
                                            <td>{item.recipient}，{item.contact}，{item.address}</td>
                                            <td>{currRefund.customer_desp}</td>
                                            <td>{item.total_fee}</td>
                                            <td>{currRefund.amount}</td>
                                            <td>{moment(item.create_time).format("YYYY-M-D HH:mm")}</td>
                                            <td>{moment(item.timestamp).format("YYYY-M-D HH:mm")}</td>
                                            <td>{currRefund.status}</td>
                                            <td>
                                                {currRefund.status == "申请退款" ?
                                                    <div className="btn-group" role="group" aria-label="...">
                                                        <button className="" type="button"
                                                                onClick={this.acceptRefund.bind(this, item.id, currRefund.refund_id)}>同意退款
                                                        </button>
                                                        <button className="" type="button"
                                                                onClick={this.showRefuseRefundModal.bind(this, item.id, currRefund.refund_id)}>拒绝退款
                                                        </button>
                                                    </div> : undefined
                                                }
                                            </td>
                                        </tr>
                                    })

                                }

                                </tbody>
                            </table>
                        </div>
                        <div className="panel-footer">
                            {pageNums>1?p:""}
                        </div>
                    </div>
                </div>
                <div  className="modal fade" id="refuseRefundModal" tabIndex="-1" role="dialog">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                <h4 className="modal-title">拒绝退款</h4>
                            </div>
                            <div className="modal-body">
                                <form>
                                    <div className="form-group">
                                        <label className="control-label">理由:</label>
                                        { /*<select id="store_desp" valueLink={linkState(this,'store_desp')}>
                                            <option key="reason0" value="货物已经送出">货物已经送出</option>
                                            <option key="reason1" value="退款金额不符">退款金额不符</option>
                                            <option key="reason2" value="其他">其他</option>
                                        </select>*/}

                                        <input type="text" className="form-control" id="store_desp" ref="store_desp" />
                                    </div>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                <button type="button" className="btn btn-primary" onClick={this.refuseRefund.bind(this)}>确认</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        )
    }
}

OrderPanel.propTypes = {
    orders: PropTypes.shape({
        num: PropTypes.number.isRequired,
        list: PropTypes.arrayOf(PropTypes.shape({
            id: PropTypes.number.isRequired,
            store_id: PropTypes.number.isRequired,
            customer_id: PropTypes.number.isRequired,
            recipient: PropTypes.string.isRequired,
            address: PropTypes.string.isRequired,
            contact: PropTypes.string.isRequired,
            remark: PropTypes.string.isRequired,
            pack_fee: PropTypes.number.isRequired,
            total_fee: PropTypes.number.isRequired,
            state: PropTypes.string.isRequired,
            pay_status: PropTypes.number.isRequired,
            trade_no: PropTypes.number.isRequired,
            arrive_time: PropTypes.number.isRequired,
            create_time: PropTypes.number.isRequired,
            goods: PropTypes.arrayOf(PropTypes.shape({
                good_name: PropTypes.string.isRequired,
                good_num: PropTypes.number.isRequired
            })),
            refunds: PropTypes.array
        }))
    }).isRequired,
    spans: PropTypes.shape({
        unAccepts: PropTypes.number.isRequired,
        unArrived: PropTypes.number.isRequired,
        applyRefund: PropTypes.number.isRequired
    })
}

function mapStateToProps(state) {
    return {
        orders: state.orders,
        spans: state.spans
    }
}

export default connect(
    mapStateToProps
)(OrderPanel)


