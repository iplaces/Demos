/**
 * Created by zsh on 2016/4/22.
 */
import {connect } from 'react-redux'
import React,{Component,PropTypes} from 'react'
import linkState from 'react-link-state'
import { createComment } from '../actions/order'
import {MiamiModal} from '../util/Modal'

export class Remark extends Component {
    constructor(props){
        super(props)
        this.state = {
            orderId:'',
            storeId:'',
            dishGrade:0,
            transTime:0,
            images:[],
            eventNotBond:true
        }
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
    componentDidMount(){
        const self =this
        $('.rate-star a').mouseenter(function () {
            const preAll = $(this).prevAll();
            const nextAll = $(this).nextAll();
            $(this).parent().siblings('span').text(self.getStarText(preAll.length + 1));
            $(this).parent().parent().attr('class', 'rate-star level' + (preAll.length + 1))
            preAll.addClass('active')
            $(this).addClass('active')
            nextAll.removeClass('active')
        }).mouseleave(function () {
            $(this).siblings(':eq(' + (self.state.dishGrade - 1) + ')').trigger('mouseenter');
            $(this).parent().siblings('span').text(self.getStarText(self.state.dishGrade));
        }).click(function () {
            const dishGrade = $(this).prevAll().length + 1
            self.setState({dishGrade: dishGrade})
        })
    }
    createComment(){
        if(!/[0-9]/.test(this.state.dishGrade)){
            alert("配送速度请输入数字！")
        }
        const data = {
            orderId: this.state.orderId,
            storeId: this.state.storeId,
            dishGrade: this.state.dishGrade,
            transTime: Number(this.state.transTime),
            picUrl: this.state.images,
            content: this.state.comment,
            replyId:0//todo 这个要确认
        }
        console.log("data is===="+JSON.stringify(data))
        this.props.dispatch(createComment(data))
        this.refs.remarkModal.close();
    }
    handleChange(event){
        const self = this
        console.log("file length==="+event.target.files.length)
        const file = event.target.files[0];
        const data = new FormData();
        data.append('image',file);
        $.ajax({
            url:'/miami/customer/comment/uploadPic',
            type:'POST',
            data:data,
            processData:false,
            contentType:false
        }).success(function(res){
            if(res.errCode==0){
                self.state.images.push(res.fileName)
                const images = self.state.images
                self.setState({images:images})
            }
        })
    }

    handleClick(){
        if(this.state.images.length >= 4){
            swal({
                type:'warning',
                title:"图片数量已超过最大限制",
                timer:1000
            });
        }else{
            $(".upload").click();
        }
    }
    open(orderId,storeId){
        this.setState({orderId:orderId,storeId:storeId})
        this.refs.remarkModal.open();
    }
    render(){
        return (
            <MiamiModal ref="remarkModal" title="评价本单" confirm={this.createComment.bind(this)}>
                <div className="unrated-rate">
                    <div className="unrated-ratelist">
                        <div className="form-group">
                            <h5 className="unrated-ratelist-label">菜品：</h5>
                            <div className="unrated-ratelist-content">
                                <p className="rate-star dishGrade star level0">
                                    <span>
                                        <a className="iconfont icon-pingfenxingwu" ></a>
                                        <a className="iconfont icon-pingfenxingwu"></a>
                                        <a className="iconfont icon-pingfenxingwu"></a>
                                        <a className="iconfont icon-pingfenxingwu"></a>
                                        <a className="iconfont icon-pingfenxingwu"></a>
                                    </span>
                                    <span className="rate-star-text">点击星星打分</span>
                                </p>
                            </div>
                        </div>
                        <div className="form-group">
                            <h5 className="unrated-ratelist-label">配送速度：</h5>
                            <input type="text" valueLink={linkState(this,'transTime')} placeholder="配送时间" />分钟
                        </div>
                        <div className="form-group">
                            <h5 className="unrated-ratelist-label">文字评论：</h5>
                            <div className="unrated-ratelist-content">
                                <textarea valueLink={linkState(this,'comment')}/>
                             </div>
                        </div>
                        <div className="form-group">
                            <h5 className="unrated-ratelist-label">上传图片：</h5>
                            {
                                this.state.images.map(function(image){
                                    return(
                                        <img src={image} style={{height:'80px'}}/>
                                    )
                                })
                            }
                            <input  type="file" hidden className="upload" name="image" onChange={this.handleChange.bind(this)}/>
                            <i className="iconfont icon-tianjiatupian"  onClick={this.handleClick.bind(this)}/>
                        </div>
                    </div>
                </div>
            </MiamiModal>
        )
    }
}