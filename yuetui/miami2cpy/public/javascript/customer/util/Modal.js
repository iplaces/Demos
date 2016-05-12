/**
 * Created by zsh on 2016/4/21.
 */
import React,{Component} from 'react'
import  ReactDOM  from 'react-dom'
export class MiamiModal extends Component{
    constructor(props){
        super(props)
    }
    open(){
        $(ReactDOM.findDOMNode(this)).show()
    }
    close(){
        console.log("click close==")
        $(ReactDOM.findDOMNode(this)).fadeOut()
    }
    confirm(){
        this.props.confirm()
    }
    render(){
        const top = (window.innerHeight-480)/2
        const left = (window.innerWidth-760)/2
        return(
            <div className="modal-package" >
                <div className="modal fade" style={{left:left,top:top}}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <div className="close" onClick={this.close.bind(this)}>x</div>
                                <h4>{this.props.title}</h4>
                            </div>
                            <div className="modal-body">
                                {this.props.children}
                            </div>
                            <div className="modal-footer">
                                <button onClick={this.confirm.bind(this)} className="btn-confirm">{this.props.confirmText?this.props.confirmText:'确定'}</button>
                                <button onClick={this.close.bind(this)} className="btn-cancel">取消</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="modal-background"></div>
            </div>
        )
    }

}