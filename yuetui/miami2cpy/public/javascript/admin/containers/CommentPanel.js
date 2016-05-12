/**
 * Created by ZYQ on 2016/3/30.
 */
import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {getCommentList, getStoreList} from '../actions'
import NavBar from './NavBar'
import fetch from 'isomorphic-fetch'
import linkState from 'react-link-state';
import {ajaxJsonPost, ajaxSimpleGet }  from '../util/sk-react'
import moment from 'moment'

class CommentPanel extends Component {


    constructor(props) {
        super(props);
        this.initialState()
        console.log('constructor this.state() ')
        console.log(this.state)

    }

    initialState() {
        console.log('initialState')
        this.state = {
            currPageNum: 1,
            currStore: 0,
            storeList: [],
            num: 0,
            currComment: 0,
            currOrder: 0
        };
    }


    componentWillMount() {
        console.log('componentWillMount')
        if($CONF$.userType == "1") {
            this.getStoreNames()
        } else {
            this.props.dispatch(getCommentList(this.state.currStore, this.state.currPageNum))
        }
    }

    componentWillReceiveProps(nextProps) {
        console.log('componentWillReceiveProps')
        const { comments } = nextProps

        this.setState({
            num: comments.num
        });
    }

    componentDidMount() {
        console.log('constructor componentDidMount ')
        var self = this;

    }

    getStoreNames() {
        var self = this
        var url = "/miami/admin/store/listNames"
        var successFunc = function (data) {
            console.log('getStoreNames')
            console.log(data)
            self.setState({storeList: data.list})
        }.bind(this)
        ajaxSimpleGet(url, successFunc)
    }

    selectStore() {
        var self = this
        var cStore = this.refs.stores.getDOMNode().value

        self.setState({
            currStore: cStore
        })
        this.props.dispatch(getCommentList(cStore, self.state.currPageNum))
    }

    deleteComment(id) {
        var self = this
        var url = "/miami/admin/comment/delete?id=" + id
        var successFunc = function (data) {
            self.props.dispatch(getCommentList(self.state.currStore, self.state.currPageNum))
            toastr.success("删除评论成功！")
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    showCommentModal(id, orderId) {
        var self = this
        self.setState({
            currComment: id,
            currOrder: orderId
        })
        $('#commentModal').modal('show');
    }

    replyComment() {
        var self = this
        var content = this.refs.reply.getDOMNode().value
        var url = "/miami/admin/comment/reply?id=" + self.state.currComment + "&orderId=" + self.state.currOrder + "&content=" + content
        var successFunc = function () {
            self.props.dispatch(getCommentList(self.state.currStore, self.state.currPageNum))
            $('#commentModal').modal('hide');
            toastr.success("回复评论成功！")
            this.setState({
                currComment: 0,
                currOrder: 0
            })
        }.bind(this);
        ajaxSimpleGet(url, successFunc)
    }

    listByPageNo(pageNo) {
        var self = this
        self.setState({currPageNum: pageNo})
        self.props.dispatch(getCommentList(self.state.currStore, pageNo))
    }

    lastPage() {
        var pageNo = parseInt(this.state.currPageNum) - 1;
        this.listByPageNo(pageNo);
    }

    nextPage() {
        var pageNo = parseInt(this.state.currPageNum) + 1;
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
        console.log('infoPanel render')
        const { comments } = this.props;
        var stores = this.state.storeList
        var pageNums = parseInt(comments.num);
        var pageArr=[];

        for(var i =1;i<pageNums+1;i++){
            if(Math.abs(i - this.state.currPageNum) < 5){
                pageArr.push(<li className={this.state.currPageNum == i ? "active" : ""}><a onClick={this.listByPageNo.bind(this,i)}>{i}</a></li>);
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
            {this.state.currPageNum==1?"":before}
            {pageArr}
            {this.state.currPageNum==pageNums?"":after}
            &nbsp;&nbsp;&nbsp;&nbsp;<span>到第<input type="number" ref="jump" style={{maxWidth:"80px"}}/>页</span>
            <button className="btn btn-default" onClick={this.jump.bind(this)}>跳转</button>
        </ul>;
        var pad =   $CONF$.userType == "1" ? '110px' : '70px'

        return (
            <div>
                <NavBar />
                <div  className="container" style={{paddingTop: pad }}>

                    <div className="panel panel-default">
                        <div className="panel-heading">评论管理</div>
                        <div className="panel-body">
                            { $CONF$.userType == "1"?
                                <select id="stores" ref="stores" onChange={this.selectStore.bind(this)}>
                                    {stores.map(s =>
                                        <option key={s.id} value={s.id}>{s.name}</option>
                                    )}
                                </select>
                                :
                                undefined
                            }


                            <table className="table table-border table-hover" style={{marginTop: '10px'}}>
                                <thead>
                                <tr>
                                    <th>评论ID</th>
                                    <th>用户ID</th>
                                    <th>订单号</th>
                                    <th>评分</th>
                                    <th>内容</th>
                                    <th>图片</th>
                                    <th>评论时间</th>
                                    <th>操作</th>



                                </tr>
                                </thead>
                                <tbody>
                                {comments.list.map(item =>
                                    <tr key={item.id}>
                                        <td>{item.id}</td>
                                        <td>{item.userId}</td>
                                        <td>{item.itemId}</td>
                                        <td>{item.grade}</td>
                                        <td>{item.content}</td>
                                        <td>{
                                            item.picUrl.split("#").map( (i, index) =>
                                            index == (item.picUrl.split("#").length - 1) ? undefined :
                                            <img width="40px" height="40px" src={i} />
                                        )}
                                        </td>
                                        <td>{moment(item.time, "x").format("YYYY-M-D HH:mm:ss")}</td>
                                        <td>


                                        {$CONF$.userType == "1" ?

                                                <button type="button" onClick={this.deleteComment.bind(this,item.id)}>删除评论</button>
                                            : <div>
                                            {$.isEmptyObject(item.replyComment)?
                                                <button type="button" onClick={this.showCommentModal.bind(this, item.id, item.itemId)}>回复评论</button>
                                                :
                                                <p>已回复</p>
                                            }
                                            </div>
                                        }
                                        </td>

                                    </tr>
                                )}
                                </tbody>
                            </table>


                        </div>
                        <div className="panel-footer">
                            {pageNums>1?p:""}
                        </div>



                    </div>


                </div>
                <div  className="modal fade" id="commentModal" tabIndex="-1" role="dialog">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                <h4 className="modal-title">回复评论</h4>
                            </div>
                            <div className="modal-body">
                                <form>
                                    <div className="form-group">
                                        <label className="control-label">内容:</label>
                                        <input type="text" className="form-control" id="reply" ref="reply" />
                                    </div>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-default" data-dismiss="modal">关闭</button>
                                <button type="button" className="btn btn-primary" onClick={this.replyComment.bind(this)}>确认</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        )
    }
}

CommentPanel.propTypes = {
    comments: PropTypes.shape({

        list: PropTypes.arrayOf(PropTypes.shape({
            id: PropTypes.number.isRequired,
            grade: PropTypes.string.isRequired,
            content: PropTypes.string.isRequired,
            picUrl: PropTypes.string.isRequired,
            time: PropTypes.string.isRequired,
            userId: PropTypes.number.isRequired,
            itemId: PropTypes.string.isRequired
        })),
        num: PropTypes.number.isRequired
    })
}

function mapStateToProps(state) {
    return {
        comments: state.comments
    }
}

export default connect(
    mapStateToProps
)(CommentPanel)


