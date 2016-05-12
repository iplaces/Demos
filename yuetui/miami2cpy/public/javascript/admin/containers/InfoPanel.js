/**
 * Created by ZYQ on 2016/3/30.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {getStoreInfo} from '../actions'
import NavBar from './NavBar'
import fetch from 'isomorphic-fetch'
import linkState from 'react-link-state';
import {ajaxJsonPost, ajaxSimpleGet }  from '../util/sk-react'
import moment from 'moment'

class InfoPanel extends Component {


    constructor(props) {
        super(props);
        this.initialState()
        console.log('constructor this.state() ')
        console.log(this.state)

    }

    initialState() {
        console.log('initialState')
        this.state = {
            description: '',
            contact: '',
            icon: '',
            open_from: '',
            open_to: '',
            base_price: '',
            pack_fee: '',
            cost_time: ''
        };
    }



    componentWillMount() {
        this.props.dispatch(getStoreInfo())
        //this.initialState()
        console.log('componentWillMount')
    }

    componentWillReceiveProps(nextProps) {
        console.log('componentWillReceiveProps')
        const { info } = nextProps

        this.setState({
            description: info.description,
            contact: info.contact,
            icon: info.icon,
            open_from: info.open_from,
            open_to: info.open_to,
            base_price: info.base_price.toString(),
            pack_fee: info.pack_fee.toString(),
            cost_time: info.cost_time.toString()
        });

    }

    componentDidMount() {
        console.log('constructor componentDidMount ')
        var self = this;

        $("#a_logo").fileinput({

            uploadUrl: "/miami/admin/image/upload",
            uploadAsync: true,
            maxFileCount: 1,
            allowedFileExtensions: ['jpg', 'png', 'jpeg', 'bmp'],
            previewFileType: "image",
            showCaption: false
        }).on('fileuploaded', function (event, data, id, index) {
            var response = data.response;
            var icon = response.fileName
            console.log(icon)
            self.setState({icon: icon})

        });

        //this.props.dispatch(getStoreInfo())
        //console.log("info===" )
        //console.log(self.props.info)


    }

    showEditModal() {

        $('#editStoreModal').modal('show');
    }

    editStore() {
        var self = this

        self.state.open_from = self.refs.open_from.getDOMNode().value
        self.state.open_to = self.refs.open_to.getDOMNode().value

        var url = "/miami/admin/store/edit"
        var successFunc = function (data) {
            self.props.dispatch(getStoreInfo())
            toastr.success("编辑商户资料成功！")
            $("#a_logo").fileinput('refresh',{

                uploadUrl: "/miami/admin/image/upload",
                uploadAsync: true,
                maxFileCount: 1,
                allowedFileExtensions: ['jpg', 'png', 'jpeg', 'bmp'],

                previewFileType: "image",
                showCaption: false
            })
        }.bind(this)
        ajaxJsonPost(url, this.state, successFunc)
        $('#editStoreModal').modal('hide');
    }

    openStore() {
        var self = this
        var url = "/miami/admin/store/open"
        var successFunc = function (data) {
            self.props.dispatch(getStoreInfo())
            toastr.success("开店成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    closeStore() {
        var self = this

        var url = "/miami/admin/store/close"
        var successFunc = function (data) {
            self.props.dispatch(getStoreInfo())
            toastr.success("闭店成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    handleTime(picker) {

        $('#' + picker).timepicki({
            step_size_hours:1,
            step_size_minutes:15});

    }

    render() {
        console.log('infoPanel render')
        const { info } = this.props;
        //console.log('this.state')
        //console.log(this.state)

        return (
            <div>
                <NavBar />
                <div  className="container" style={{paddingTop: '70px' }}>

                    <div className="panel panel-default">
                        <div className="panel-heading">信息管理</div>
                        <div className="panel-body">
                            <dl className="dl-horizontal">

                                <dt>店铺名称</dt>
                                <dd>
                                    {info.name}
                                </dd>
                                <dt>店铺状态</dt>
                                <dd>
                                    {
                                        info.state == 0 ?
                                            <p>休息中</p>
                                            :
                                            <p>营业中</p>
                                    }
                                    {
                                        info.state == 0 ?
                                            <button type="button" onClick={this.openStore.bind(this)}>开店</button>
                                            :
                                            <button type="button" onClick={this.closeStore.bind(this)}>闭店</button>
                                    }
                                </dd>
                                <dt>店铺类型</dt>
                                <dd>{info.cat_name}</dd>

                                <dt>店铺地址</dt>
                                <dd>{info.address}</dd>
                                <hr />

                                <dt>图标</dt>
                                <dd>
                                    <img width="60px" height="60px" src={info.icon} />
                                </dd>
                                <dt>店铺简介</dt>
                                <dd>{info.description}</dd>
                                <dt>联系电话</dt>
                                <dd>{info.contact}</dd>
                                <dt>营业时间</dt>
                                <dd>{info.open_from}-{info.open_to}</dd>
                                <dt>配送费</dt>
                                <dd>{info.pack_fee}</dd>
                                <dt>起送价</dt>
                                <dd>{info.base_price}</dd>
                                <dt>预计送达时间</dt>
                                <dd>{info.cost_time}</dd>
                                <center>
                                    <button type="button" className="btn btn-default "
                                            data-toggle="modal" onClick={this.showEditModal.bind(this)}>编辑商户资料</button>
                                </center>

                                <hr />
                                <dt>上次编辑时间</dt>
                                <dd>{moment(info.modified_time).format("YYYY-M-D HH:mm:ss")}</dd>
                                <dt>创建时间</dt>
                                <dd>{moment(info.create_time).format("YYYY-M-D HH:mm:ss")}</dd>
                                <dt>销量</dt>
                                <dd>{info.sales}</dd>
                                <dt>评论数</dt>
                                <dd>{info.comments}</dd>
                                <dt>评分</dt>
                                <dd>{info.grades}</dd>
                            </dl>

                        </div>



                    </div>

                    <div  className="modal fade" id="editStoreModal" tabIndex="-1" role="dialog">
                        <div className="modal-dialog" role="document">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 className="modal-title">编辑详细资料</h4>
                                </div>
                                <div className="modal-body">
                                    <form>
                                        <div className="form-group">
                                            <label className="control-label">店铺简介:</label>
                                            <input type="text" className="form-control" id="a_description" valueLink={linkState(this, 'description')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">联系电话:</label>
                                            <input type="text" className="form-control" id="a_contact" valueLink={linkState(this, 'contact')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">起送价:</label>
                                            <input type="number" className="form-control" id="a_basePrice" valueLink={linkState(this,'base_price')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">配送费:</label>
                                            <input type="number" className="form-control" id="a_packFee" valueLink={linkState(this,'pack_fee')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">预计送达时长(分钟):</label>
                                            <input type="number" className="form-control" id="a_cost" valueLink={linkState(this,'cost_time')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">图标:</label>
                                            <input id="a_logo" name="image" type="file" className="file" multiple data-preview-file-type="image" />
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">营业开始时间:</label>
                                            <input onClick={this.handleTime.bind(this, 'timepicker1')}  className="form-control" ref='open_from' id='timepicker1' type='text' name='timepicker1' valueLink={linkState(this,'open_from')}/>
                                        </div>
                                        <div className="form-group">
                                            <label className="control-label">营业结束时间:</label>
                                            <input onClick={this.handleTime.bind(this, 'timepicker2')} className="form-control" ref='open_to' id='timepicker2' type='text' name='timepicker2' valueLink={linkState(this,'open_to')}/>
                                        </div>
                                    </form>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                    <button type="button" className="btn btn-primary" onClick={this.editStore.bind(this)}>确认修改</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        )
    }
}

InfoPanel.propTypes = {
    info: PropTypes.shape({
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
        cat_name: PropTypes.string.isRequired,
        sales: PropTypes.number.isRequired,
        comments: PropTypes.number.isRequired,
        grades: PropTypes.number.isRequired,
        cost_time: PropTypes.number.isRequired,
        state: PropTypes.number.isRequired,
        create_time: PropTypes.number.isRequired,
        modified_time: PropTypes.number.isRequired
    })
}

function mapStateToProps(state) {
    return {
        info: state.info
    }
}

export default connect(
    mapStateToProps
)(InfoPanel)


