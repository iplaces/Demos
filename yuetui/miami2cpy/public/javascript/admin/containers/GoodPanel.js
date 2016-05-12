import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {getGoodList, getCateList} from '../actions'
import NavBar from './NavBar'
import linkState from 'react-link-state';
import {ajaxJsonPost, ajaxSimpleGet }  from '../util/sk-react'
import moment from 'moment'

class GoodPanel extends Component {

    constructor(props) {
        super(props);
        console.log('constructor this.state() ');
        this.initialState()
    }

    initialState() {
        this.state = {
            cat_id: '0',
            name: '',
            price: '0',
            sale_price: '0',
            description: '',
            icon: '',
            stock: '0',
            currentGood: '0'
        };
    }

    componentWillMount() {
        console.log('componentWillMount')
        this.props.dispatch(getGoodList())
        this.props.dispatch(getCateList())
    }

    componentDidMount() {
        console.log('componentDidMount ')
        var self = this;
        $("#a_logo").fileinput({

            uploadUrl: "/miami/admin/image/upload", // server upload action
            uploadAsync: true,
            maxFileCount: 1,
            allowedFileExtensions: ['jpg', 'png', 'jpeg', 'bmp'],

            previewFileType: "image",
            showCaption: false
        }).on('fileuploaded', function (event, data, id, index) {
            console.log(event)
            console.log(data)
            console.log(id)
            console.log(index)

            var response = data.response;
            var icon = response.fileName
            self.setState({icon: icon})
            console.log(response.fileName);
        });

        $("#e_logo").fileinput({

            uploadUrl: "/miami/admin/image/upload", // server upload action
            uploadAsync: true,
            maxFileCount: 1,
            allowedFileExtensions: ['jpg', 'png', 'jpeg', 'bmp'],

            previewFileType: "image",
            showCaption: false
        }).on('fileuploaded', function (event, data, id, index) {
            console.log(event)
            console.log(data)
            console.log(id)
            console.log(index)

            var response = data.response;
            var icon = response.fileName
            self.setState({icon: icon})
            console.log(response.fileName);
        });

        console.log("componentDidMount goods===")

        console.log(this.props.goods)
    }

    addGood(e) {
        e.preventDefault()

        var url = "/miami/admin/good/add"
        var successFunc = function (data) {
            this.props.dispatch(getGoodList())
            toastr.success("创建菜品成功！")
            this.initialState()
            $("#a_logo").fileinput('refresh', {

                uploadUrl: "/miami/admin/image/upload", // server upload action
                uploadAsync: true,
                maxFileCount: 1,
                allowedFileExtensions: ['jpg', 'png', 'jpeg', 'bmp'],

                previewFileType: "image",
                showCaption: false
            })
        }.bind(this);
        ajaxJsonPost(url, this.state, successFunc)
        $('#addGoodModal').modal('hide');
    }

    showEditGoodModal(item) {
        this.props.dispatch(getCateList())
        this.setState({
            cat_id: item.cat_id.toString(),
            name: item.name,
            price: item.price.toString(),
            sale_price: item.sale_price.toString(),
            description: item.description,
            icon: item.icon,
            stock: item.stock.toString(),
            currentGood: item.id.toString()
        })
        $('#editGoodModal').modal('show');
    }

    editGood(e) {
        e.preventDefault()

        var url = "/miami/admin/good/edit?id=" + this.state.currentGood
        var successFunc = function (data) {
            this.props.dispatch(getGoodList())
            toastr.success("编辑菜品成功！")
            this.initialState()
            $("#e_logo").fileinput('refresh', {

                uploadUrl: "/miami/admin/image/upload", // server upload action
                uploadAsync: true,
                maxFileCount: 1,
                allowedFileExtensions: ['jpg', 'png', 'jpeg', 'bmp'],
                previewFileType: "image",
                showCaption: false
            })
        }.bind(this);
        ajaxJsonPost(url, this.state, successFunc)
        $('#editGoodModal').modal('hide');
    }

    upGood(id) {
        var url = "/miami/admin/good/up?id=" + id
        var successFunc = function (data) {
            this.props.dispatch(getGoodList())
            toastr.success("上架菜品成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    offGood(id) {
        var url = "/miami/admin/good/off?id=" + id
        var successFunc = function (data) {
            this.props.dispatch(getGoodList())
            toastr.success("下架菜品成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    deleteGood(id) {
        var url = "/miami/admin/good/delete?id=" + id
        var successFunc = function (data) {
            this.props.dispatch(getGoodList())
            toastr.success("删除菜品成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    showAddStockModal(id) {
        this.setState({currentGood: id})
        $('#addStockModal').modal('show');
    }

    addStock() {
        var num = this.refs.stock.getDOMNode().value
        var url = "/miami/admin/stock/add?id=" + this.state.currentGood + "&num=" + num
        console.log(url)
        var successFunc = function (data) {
            this.props.dispatch(getGoodList())
            toastr.success("增加库存成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
        $('#addStockModal').modal('hide');
        this.setState({currentGood: '0'})

    }

    render() {
        const { goods, categories } = this.props
        console.log("render goods===")
        console.log(JSON.stringify(this.state))


        return (
            <div>
                <NavBar />
                <div  className="container" style={{paddingTop: '70px' }}>

                    <div className="panel panel-default">
                        <div className="panel-heading">菜品管理</div>
                        <div className="panel-body">
                            <button type="button" className="btn btn-default"
                                    data-toggle="modal" data-target="#addGoodModal">添加菜品</button>
                        </div>

                        <table className="table table-border table-hover" style={{marginTop: '10px'}}>
                            <thead>
                            <tr>
                                <th>菜品ID</th>
                                <th>图标</th>
                                <th>名称</th>
                                <th>所属分类</th>
                                <th>原价</th>
                                <th>优惠价格</th>
                                <th>创建时间</th>
                                <th>销量</th>
                                <th>库存</th>
                                <th>操作</th>

                            </tr>
                            </thead>
                            <tbody>
                            {goods.map(item =>
                                <tr key={item.id}>
                                    <td>{item.id}</td>
                                    <td> <img src={item.icon} height="40" width="40" /></td>
                                    <td>{item.name}</td>
                                    <td>{item.cat_name}</td>
                                    <td>{item.price}</td>
                                    <td>{item.sale_price}</td>
                                    <td>{moment(item.create_time).format("YYYY-M-D HH:mm:ss")}</td>
                                    <td>{item.sales}</td>
                                    <td>{item.stock}</td>
                                    <td>
                                        <button onClick={this.showAddStockModal.bind(this, item.id.toString())}>增加库存</button>
                                        <button onClick={this.showEditGoodModal.bind(this, item)}>编辑</button>
                                        { item.state == 0 ?
                                            <button onClick={this.offGood.bind(this, item.id)}>下架</button>
                                            :
                                            <button onClick={this.upGood.bind(this, item.id)}>上架</button>
                                        }
                                        <button  onClick={this.deleteGood.bind(this, item.id)}>删除</button>

                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>

                    <div  className="modal fade" id="addGoodModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">添加菜品</h4>
                                </div>
                                <div className="modal-body">
                                    <form>
                                        <div className="form-group">
                                            <label className="control-label">菜品名称:</label>
                                            <input type="text" className="form-control" id="a_name"  valueLink={linkState(this,'name')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">菜品分类:</label>
                                            <select id="a_cat" valueLink={linkState(this,'cat_id')}>
                                                {categories.map(cate =>
                                                    <option key={cate.id} value={cate.id}>{cate.name}</option>
                                                )}
                                            </select>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">菜品简介:</label>
                                            <input type="text" className="form-control" id="a_description" valueLink={linkState(this,'description')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">价格:</label>
                                            <input type="number" className="form-control" id="a_price" valueLink={linkState(this,'price')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">优惠价格:</label>
                                            <input type="number" className="form-control" id="a_salePrice" valueLink={linkState(this,'sale_price')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">库存:</label>
                                            <input type="number" className="form-control" id="a_stock" valueLink={linkState(this,'stock')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">图标:</label>
                                            <input id="a_logo" name="image" type="file" className="file" multiple data-preview-file-type="image" />
                                        </div>
                                    </form>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.addGood.bind(this)}>确认创建</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div  className="modal fade" id="addStockModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">增加库存</h4>
                                </div>
                                <div className="modal-body">
                                    <form>
                                        <div className="form-group">
                                            <label className="control-label">数量:</label>
                                            <input type="text" className="form-control" id="stock" ref="stock" />
                                        </div>
                                    </form>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.addStock.bind(this)}>确认</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="modal fade" id="editGoodModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">编辑菜品</h4>
                                </div>
                                <div className="modal-body">
                                    <form>
                                        <div className="form-group">
                                            <label className="control-label">菜品名称:</label>
                                            <input type="text" className="form-control" id="e_name"  valueLink={linkState(this,'name')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">菜品分类:</label>
                                            <select id="e_cat" valueLink={linkState(this,'cat_id')}>
                                                {categories.map(cate =>
                                                    <option key={cate.id} value={cate.id}>{cate.name}</option>
                                                )}
                                            </select>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">菜品简介:</label>
                                            <input type="text" className="form-control" id="e_description" valueLink={linkState(this,'description')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">价格:</label>
                                            <input type="number" className="form-control" id="e_price" valueLink={linkState(this,'price')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">优惠价格:</label>
                                            <input type="number" className="form-control" id="e_salePrice" valueLink={linkState(this,'sale_price')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">图标:</label>
                                            <input id="e_logo" name="image" type="file" className="file" multiple data-preview-file-type="image" />
                                        </div>
                                    </form>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.editGood.bind(this)}>确认编辑</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        )
    }
}


GoodPanel.propTypes = {
    goods: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        store_id: PropTypes.number.isRequired,
        icon: PropTypes.string.isRequired,
        price: PropTypes.number.isRequired,
        sale_price: PropTypes.number.isRequired,
        cat_id: PropTypes.number.isRequired,
        cat_name: PropTypes.string.isRequired,
        sales: PropTypes.number.isRequired,
        stock: PropTypes.number.isRequired,
        state: PropTypes.number.isRequired,
        create_time: PropTypes.number.isRequired,
    })).isRequired
}

function mapStateToProps(state) {
    return {
        goods: state.goods,
        categories: state.categories
    }
}

export default connect(
    mapStateToProps
)(GoodPanel)


