import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {getStoreList, getCateList} from '../actions'
import NavBar from './NavBar'
import fetch from 'isomorphic-fetch'
import linkState from 'react-link-state';
import {ajaxJsonPost, ajaxSimpleGet }  from '../util/sk-react'
import moment from 'moment'

class StorePanel extends Component {


    constructor(props) {
        super(props);
        this.initialState()
        console.log('constructor this.state() ')
        console.log(this.state)

    }

    initialState() {
        this.state = {
            account: '',
            name: '',
            description: '',
            address: '',
            cat_id: '',
            current_store: '0'
        };
    }

    componentWillMount() {
        console.log('constructor componentWillMount ')
        var self = this;

        this.props.dispatch(getStoreList())
        console.log(this.props)
        console.log("stores===" + this.props.stores)
    }

    componentDidMount() {
        console.log('constructor componentDidMount ')
        var self = this;
        console.log(this.props)
        console.log("stores===" + this.props.stores)
    }

    createStore(e) {
        e.preventDefault()
        var self = this
        console.log(this.state)

        var url = "/miami/admin/store/add"
        var successFunc = function (data) {

            self.props.dispatch(getStoreList())
            toastr.success("创建商户成功！")
            self.initialState()
        }.bind(self)
        ajaxJsonPost(url, this.state, successFunc)
        $('#addStoreModal').modal('hide');
    }

    enableStore(id) {
        var self = this
        var url = "/miami/admin/store/enable?id=" + id
        var successFunc = function (data) {
            self.props.dispatch(getStoreList())
            toastr.success("开店成功！")
        }.bind(self);
        ajaxSimpleGet(url, successFunc)
    }

    disableStore(id) {
        var self = this

        var url = "/miami/admin/store/disable?id=" + id
        var successFunc = function (data) {
            self.props.dispatch(getStoreList())
            toastr.success("闭店成功！")
        }.bind(self);
        ajaxSimpleGet(url, successFunc)
    }

    deleteStore(id) {
        var self = this

        var url = "/miami/admin/store/delete?id=" + id
        var successFunc = function (data) {
            self.props.dispatch(getStoreList())
            toastr.success("删除店铺成功！")
        }.bind(self);
        ajaxSimpleGet(url, successFunc)
    }

    handleTime(picker) {

        $('#' + picker).timepicki({
            step_size_hours:1,
            step_size_minutes:15});

    }

    showEditCateModal(id) {
        this.props.dispatch(getCateList())

        this.setState({current_store: id})
        $('#editCateModal').modal('show');
    }

    editCate() {
        var num = this.refs.cateId.getDOMNode().value
        var url = "/miami/admin/store/editCategory?storeId=" + this.state.current_store + "&cateId=" + num
        console.log(url)
        var successFunc = function (data) {
            this.props.dispatch(getStoreList())
            toastr.success("编辑分类成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
        $('#editCateModal').modal('hide');
        this.setState({current_store: '0'})

    }

    render() {
        const { stores, categories } = this.props
        console.log("render store===" + this.props.stores)
        console.log(this.props)
        console.log('constructor render')
        console.log(this.state)
        return (

            <div>
                <NavBar />
                <div  className="container" style={{paddingTop: '110px' }}>
                    <div className="panel panel-default">
                        <div className="panel-heading">商户管理</div>
                        <div className="panel-body">
                        </div>

                        <table className="table table-border table-hover" style={{marginTop: '10px'}}>
                            <thead>
                            <tr>
                                <th>商户ID</th>
                                <th>图标</th>
                                <th>名称</th>
                                <th>所属分类</th>
                                <th>营业时间</th>
                                <th>创建时间</th>
                                <th>操作</th>

                            </tr>
                            </thead>
                            <tbody>
                            {stores.map(item =>
                                <tr key={item.id}>
                                    <td>{item.id}</td>
                                    <td><img width="40px" height="40px" src={item.icon} /></td>
                                    <td>{item.name}</td>
                                    <td>{item.cat_id}</td>
                                    <td>{item.open_from} - {item.open_to}</td>
                                    <td>{moment(item.create_time).format("YYYY-M-D HH:mm:ss")}</td>
                                    <td>
                                        <button type="button" onClick={this.showEditCateModal.bind(this,item.id)}>编辑分类</button>

                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>


                    <div  className="modal fade" id="addStoreModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">创建商户</h4>
                                </div>
                                <div className="modal-body">
                                    <form>
                                        <div className="form-group">
                                            <label className="control-label">邮箱:</label>
                                            <input type="text" className="form-control" id="a_email" valueLink={linkState(this,'account')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">商户名称:</label>
                                            <input type="text" className="form-control" id="a_name"  valueLink={linkState(this,'name')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">商户类型:</label>
                                            <input type="number" className="form-control" id="a_cat" valueLink={linkState(this,'cat_id')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">商户简介:</label>
                                            <input type="text" className="form-control" id="a_description" valueLink={linkState(this,'description')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">商户地址:</label>
                                            <input type="text" className="form-control" id="a_address" valueLink={linkState(this, 'address')}/>
                                        </div>

                                    </form>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.createStore.bind(this)}>确认创建</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div  className="modal fade" id="editCateModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">编辑分类</h4>
                                </div>
                                <div className="modal-body">
                                    <select id="e_cat" ref="cateId">
                                        {categories.map(cate =>
                                            <option key={cate.id} value={cate.id}>{cate.name}</option>
                                        )}
                                    </select>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.editCate.bind(this)}>确认</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>


        )
    }
}

StorePanel.propTypes = {
    stores: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        contact: PropTypes.string.isRequired,
        address: PropTypes.string.isRequired,
        icon: PropTypes.string.isRequired,
        open_from: PropTypes.string.isRequired,
        open_to: PropTypes.string.isRequired,
        base_price: PropTypes.number.isRequired,
        pack_fee: PropTypes.number.isRequired,
        cat_id: PropTypes.string.isRequired,
        sales: PropTypes.number.isRequired,
        comments: PropTypes.number.isRequired,
        grades: PropTypes.number.isRequired,
        cost_time: PropTypes.number.isRequired,
        state: PropTypes.number.isRequired,
        create_time: PropTypes.number.isRequired,
        modified_time: PropTypes.number.isRequired
    })).isRequired
}

function mapStateToProps(state) {
    return {
        stores: state.stores,
        categories: state.categories
    }
}

export default connect(
    mapStateToProps
)(StorePanel)


