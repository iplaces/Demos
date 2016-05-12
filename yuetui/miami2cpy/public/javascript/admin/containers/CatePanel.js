import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {getCateList} from '../actions'
import NavBar from './NavBar'
import linkState from 'react-link-state';
import {ajaxJsonPost, ajaxSimpleGet }  from '../util/sk-react'
import moment from 'moment'

class CatePanel extends Component {


    constructor(props) {
        super(props);
        console.log('constructor this.state() ');
        this.initialState()

    }

    initialState() {
        this.state = {
            name: '',
            icon: '',
            rank: '0',
            currentCate: '0'
        };
    }

    componentWillMount() {
        console.log('componentWillMount')
        this.props.dispatch(getCateList())
    }

    componentDidMount() {
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

            var response = data.response;
            var icon = response.fileName
            self.setState({icon: icon})
            console.log(response.fileName);
        });

        console.log('componentDidMount ')


        console.log("componentDidMount categories===")

        console.log(this.props.categories)
    }

    addCategory(e) {
        e.preventDefault()

        var url = "/miami/admin/category/add"
        var successFunc = function (data) {
            this.props.dispatch(getCateList())
            toastr.success("创建分类成功！")
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
        $('#addCategoryModal').modal('hide');
    }

    showEditCateModal(item) {
        this.setState({
            name: item.name,
            icon: item.icon,
            rank: item.rank.toString(),
            currentCate: item.id.toString()
        })
        $('#editCategoryModal').modal('show');
    }

    editCategory(e) {
        e.preventDefault()
        var self = this;
        var url = "/miami/admin/category/edit?id=" + self.state.currentCate
        var successFunc = function (data) {
            self.props.dispatch(getCateList())
            toastr.success("编辑分类成功！")
            self.initialState()
            $("#e_logo").fileinput('refresh', {

                uploadUrl: "/miami/admin/image/upload", // server upload action
                uploadAsync: true,
                maxFileCount: 1,
                allowedFileExtensions: ['jpg', 'png', 'jpeg', 'bmp'],

                previewFileType: "image",
                showCaption: false
            })
        }.bind(self);
        ajaxJsonPost(url, self.state, successFunc)
        $('#editCategoryModal').modal('hide');
    }

    deleteCategory(id) {
        var url = "/miami/admin/category/delete?id=" + id
        var successFunc = function (data) {
            this.props.dispatch(getCateList())
            toastr.success("删除分类成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }



    render() {
        const { categories } = this.props
        console.log("render categories===")
        console.log(JSON.stringify(this.state))
        var pad =   $CONF$.userType == "1" ? '110px' : '70px'

        return (
            <div>
                <NavBar />
                <div  className="container" style={{paddingTop: pad}}>

                    <div className="panel panel-default">
                        <div className="panel-heading">分类管理</div>
                        <div className="panel-body">
                            <button type="button" className="btn btn-default"
                                    data-toggle="modal" data-target="#addCategoryModal">添加分类</button>
                        </div>

                        <table className="table table-border table-hover" style={{marginTop: '10px'}}>
                            <thead>
                            <tr>
                                <th>分类ID</th>
                                <th>图标</th>
                                <th>名称</th>
                                <th>排序</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            {categories.map(item =>
                                <tr key={item.id}>
                                    <td>{item.id}</td>
                                    <td>
                                        <img src={item.icon} height="30" width="30" />
                                    </td>
                                    <td>{item.name}</td>
                                    <td>{item.rank}</td>
                                    <td>
                                        <button onClick={this.showEditCateModal.bind(this, item)}>编辑</button>
                                        <button onClick={this.deleteCategory.bind(this,item.id)}>删除</button>
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>

                    <div  className="modal fade" id="addCategoryModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">添加分类</h4>
                                </div>
                                <div className="modal-body">
                                    <form>
                                        <div className="form-group">
                                            <label className="control-label">分类名称:</label>
                                            <input type="text" className="form-control" id="a_name"  valueLink={linkState(this,'name')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">排序:</label>
                                            <input type="number" className="form-control" id="a_rank" valueLink={linkState(this,'rank')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">图标:</label>
                                            <input id="a_logo" name="image" type="file" className="file" multiple data-preview-file-type="image" />

                                        </div>
                                    </form>

                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.addCategory.bind(this)}>确认创建</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div  className="modal fade" id="editCategoryModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">编辑分类</h4>
                                </div>
                                <div className="modal-body">
                                    <form>
                                        <div className="form-group">
                                            <label className="control-label">分类名称:</label>
                                            <input type="text" className="form-control" id="e_name"  valueLink={linkState(this,'name')} />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">排序:</label>
                                            <input type="number" className="form-control" id="e_rank" valueLink={linkState(this,'rank')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">图标:</label>
                                            <input id="e_logo" name="image" type="file" className="file" multiple data-preview-file-type="image" />

                                        </div>
                                    </form>

                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.editCategory.bind(this)}>确认编辑</button>
                                </div>
                            </div>
                        </div>
                    </div>


                </div>
            </div>

        )
    }
}


CatePanel.propTypes = {
    categories: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
        store_id: PropTypes.number.isRequired,
        icon: PropTypes.string.isRequired,
    })).isRequired
}


function mapStateToProps(state) {
    return {
        categories: state.categories
    }
}

export default connect(
    mapStateToProps
)(CatePanel)


