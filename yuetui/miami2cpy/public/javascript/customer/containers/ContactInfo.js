/**
 * Created by zsh on 2016/3/16.
 */
import React, { Component, PropTypes } from 'react/addons.js'
import { connect } from 'react-redux'
import {Link} from 'react-router'
import linkState from 'react-link-state'
import {getCart} from '../reducers'
import {createOrder,checkout,getAddress,addAddress,editAddress,deleteAddress} from '../actions/checkout'
import moment from 'moment'

class ContactInfo extends Component{

    constructor(props) {
        super(props)
        this.initialState()
    }
    initialState() {
        this.state = {
            addressChoice:0,
            dialogAddress:{
                addressid:0,
                name:'',
                gender:'',
                address:'',
                phone:''
            }
        };
    }
    componentDidMount(){
        console.log("addressList==="+this.props.addressList)
        const storeId = this.props.storeId
        this.props.dispatch(getAddress(storeId))
    }
    componentDidUpdate(){
        $("li.checkout-address").click(function(){
            $(this).addClass("active").siblings().removeClass("active");
        })
    }
    triggerAddressDialog(title) {
        const  dialog = document.getElementById('addressdialog'+title)
        dialog.style.visibility = dialog.style.visibility === 'visible'? 'hidden' : 'visible'
        const  modal = document.getElementById('dialog-overlay')
        modal.style.visibility = modal.style.visibility === 'visible'? 'hidden' : 'visible'
    }
    /*提交地址按钮，添加和修改共用一个*/
    submitAddress(title) {
        this.triggerAddressDialog(title);
        const data = {
            name:this.state.dialogAddress.name,
            phone:this.state.dialogAddress.phone,
            address:this.state.dialogAddress.address
        }
        if (title===1){
            data['addressid'] = this.state.dialogAddress.addressid
            console.log("data is ======="+JSON.stringify(data))
            this.props.dispatch(editAddress(data))
        }else {
            console.log("data is ======="+JSON.stringify(data))
            this.props.dispatch(addAddress(data))
        }
    }
    /*设置下单时选定的地址*/
    setAddress(index){
        this.setState({addressChoice:index})
    }
    deleteAddress(index){
        const addressid = this.props.addressList[index].addressid
        if(confirm('确定删除该地址?')) this.props.dispatch(deleteAddress(addressid))
    }
    openAddDialog(){
        this.setState({dialogAddress:{
            addressid:0,
            name:'',
            address:'',
            phone:''
        }})
        this.triggerAddressDialog(0)
    }
    openEditDialog(index) {
        this.setState({dialogAddress:{
            addressid:this.props.addressList[index].addressid,
            name:this.props.addressList[index].name,
            address:this.props.addressList[index].address,
            phone:this.props.addressList[index].phone
        }})
        this.triggerAddressDialog(1)
    }
    handleConfirm(){
        const storeId = this.props.storeId
        const {cart} = this.props
        const index = this.state.addressChoice;
        const address = this.props.addressList[index].address
        const recipient = this.props.addressList[index].name
        const phone = this.props.addressList[index].phone
        const remark = this.refs.remark.value
        const arriveTime = this.refs.arriveTime.value
        const getArriveTime= (value) =>{
            switch (value){
                case "1":
                    return moment().add(30,'minutes').format('x')
                case "2":
                    return moment().add(60,'minutes').format('x')
                case "0":
                default:
                    return moment().format('x');
            }
        }
        const data = {
            storeId: Number(storeId),
            recipient: recipient,
            address: address,
            contact: phone,
            remark: remark,
            payStatus: 0,
            arriveTime: Number(getArriveTime(arriveTime)),
            goodsDetail:cart.quantityById
        }
        this.props.dispatch(createOrder(data,storeId))
    }

    render() {
        const left = (window.innerWidth - 760)/2
        const top = (window.innerHeight - 480)/2
        const addressDiv = this.props.addressList?this.props.addressList.map(function(address,index){
            return(
                <li className="checkout-address" onClick={this.setAddress.bind(this,index)}>
                    <i className="checkout-address-icon icon-location-line" />
                    <div className="checkout-address-info">
                        <p><span className="addressName">{address.name} </span><span className="addressPhone">{address.phone}</span></p>
                        <p><span className="addressAddress">{address.address}</span></p>
                    </div>
                    <div className="checkout-address-edit">
                        <a onClick={this.openEditDialog.bind(this,index)}>修改</a>
                        <a onClick={this.deleteAddress.bind(this,index)}>删除</a>
                    </div>
                </li>
            )
        }.bind(this)):""
        /*模态框背景*/
        const dialogOverlay = <div id="dialog-overlay"></div>;
        /*可复用模态框，title:0:添加新地址，1:修改地址*/
        const addressDialog = (title) =>
            <div id={"addressdialog"+title} style={{top:top,left:left,width:'760px'}}>
                <div className="addressdialog-close" onClick={this.triggerAddressDialog.bind(this,title)}>x</div>
                <div className="addressdialog-header">{title===0?'添加新地址':'修改地址'}</div>
                <div className="addressdialog-content">
                    <div className="addressform">
                        <div>
                            <div className="addressformfield">
                                <label>姓名</label>
                                <input placeholder="请输入姓名" valueLink={linkState(this,'dialogAddress.name')}/>
                            </div>
                            {/*<div className="addressformfield sexfield">
                                <label>性别</label>
                                <div>
                                    <input id="sexfield-1-male" type="radio" name="sex" value="1" />
                                    <label for="sexfield-1-male">先生</label>
                                    <input id="sexfield-1-female" type="radio" name="sex" value="2" />
                                    <label for="sexfield-1-female">女士</label>
                                </div>
                            </div>*/}
                            <div className="addressformfield addressfield">
                                <label>详细地址</label>
                                <input placeholder="请输入小区、大厦或学校和单元、门牌号" valueLink={linkState(this,'dialogAddress.address')}/>
                            </div>
                            {/*<div className="addressformfield addressfield">
                                <label>详细地址</label>
                                <input placeholder="单元、门牌号" valueLink={linkState(this,'dialogAddress.addressDetail')}/>
                            </div>*/}
                            <div className="addressformfield phonefield">
                                <label>手机号</label>
                                <input placeholder="请输入手机号" valueLink={linkState(this,'dialogAddress.phone')}/>
                            </div>
                        </div>
                        <div className="addressform-buttons">
                            <button onClick={this.submitAddress.bind(this,title)}>保存</button>
                            <button onClick={this.triggerAddressDialog.bind(this,title)}>取消</button>
                        </div>
                    </div>
                </div>
            </div>
        return (
            <div className="checkout-content">
                <div className="checkout-select">
                    <h2 className="checkout-title">
                        收货地址
                        <a className="checkout-addaddress" onClick={this.openAddDialog.bind(this)}>添加新地址</a>
                    </h2>
                    <ul className="checkout-address-list">
                        {addressDiv}
                    </ul>
                </div>
                <div className="checkout-select">
                    <h2 className="checkout-title">付款方式</h2>
                    <ul className="clearfix">
                        <li className="checkout-pay">
                            <p>在线支付</p>
                            <p>支持微信、支付宝、QQ钱包及大部分银行卡</p>
                        </li>
                        <li className="checkout-pay">
                            <p>货到付款</p>
                            <p>送货上门后再付款</p>
                        </li>
                    </ul>
                </div>
                <div className="checkout-select">
                    <h2 className="checkout-title">其它信息</h2>
                    <div className="checkout-info">
                        <span className="checkout-info-label">送达时间</span>
                        <select ref="arriveTime">
                            <option value="0">立即送出</option>
                            <option value="1">半小时后</option>
                            <option value="2">一小时后</option>
                        </select>
                        {/*<span><input className="checkout-input" /></span>*/}

                    </div>
                    <div className="checkout-info">
                        <span className="checkout-info-label">订单备注</span>
                        <span><input className="checkout-input" ref="remark"/></span>
                    </div>
                </div>
                <div>
                    <button className="btn-stress btn-lg" onClick={this.handleConfirm.bind(this)} >
                        确认下单
                    </button>
                </div>
                {dialogOverlay}
                {addressDialog(0)}
                {addressDialog(1)}
            </div>
        )
    }
}
ContactInfo.propTypes = {
    cart: PropTypes.shape({
        addedIds: PropTypes.arrayOf().isRequired,
        quantityById: PropTypes.shape({}).isRequired
    }).isRequired,
    cartList:PropTypes.arrayOf({}).isRequired,
    addressList: PropTypes.arrayOf({}).isRequired
}


function mapStateToProps(state) {
    return {
        cartList: getCart(state),
        cart:state.cart,
        addressList: state.addressList
    }
}

export default connect(
    mapStateToProps
)(ContactInfo)