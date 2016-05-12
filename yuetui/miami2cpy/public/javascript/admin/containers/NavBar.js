import React, { Component, PropTypes } from 'react'
import {Link} from 'react-router'
import { connect } from 'react-redux'
import {
    getOrderList,
    getConfMap
} from '../actions'

class NavBar extends Component {
    componentDidMount() {

        this.props.dispatch(getConfMap())
        console.log("NavBar user")
        console.log(this.props)
    }



    render() {
        const { user } = this.props
        console.log(user)
        return (
            <div>
                {user.userType == 1 ?
                    <div>

                        <nav className="navbar navbar-default navbar-fixed-top" style={{paddingTop: '50px' }}>
                            <div className="container">
                                <div className="navbar-header">
                                    <button type="button" className="navbar-toggle collapsed" data-toggle="collapse"
                                            data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                                        <span className="sr-only">Toggle navigation</span>
                                        <span className="icon-bar"></span>
                                        <span className="icon-bar"></span>
                                        <span className="icon-bar"></span>
                                    </button>
                                    <a className="navbar-brand" href="#">外卖</a>
                                </div>
                                <div className="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                                    <ul className="nav navbar-nav">

                                        <li><Link to="/categories">分类管理</Link></li>
                                        <li><Link to="/comments">评论管理</Link></li>

                                        {user.userType == 1 ?
                                            <li><Link to="/stores">商户管理</Link></li>
                                            :
                                            <li><Link to="/orders">订单管理</Link>
                                            </li>
                                        }
                                        {
                                            user.userType != 1 ?
                                                <li><Link to="/goods">菜品管理</Link></li> : <div></div>
                                        }
                                        {
                                            user.userType != 1 ?
                                                <li><Link to="/info">商户信息管理</Link></li> : <div></div>
                                        }
                                    </ul>
                                    <ul className="nav navbar-nav navbar-right">
                                        <li className="dropdown">
                                            <a href="#" className="dropdown-toggle" data-toggle="dropdown" role="button"
                                               aria-haspopup="true" aria-expanded="false">{user.nickName}<span
                                                className="caret"></span></a>
                                            <ul className="dropdown-menu">
                                                <li><a href="#">注销</a></li>
                                            </ul>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </nav>
                        <nav className="navbar navbar-default navbar-fixed-top">
                            <div className="container">
                                <div className="navbar-header">
                                    <button type="button" className="navbar-toggle collapsed" data-toggle="collapse"
                                            data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                                        <span className="sr-only">Toggle navigation</span>
                                        <span className="icon-bar"></span>
                                        <span className="icon-bar"></span>
                                        <span className="icon-bar"></span>
                                    </button>
                                    <a className="navbar-brand" href="#">国贸</a>
                                </div>
                                <div className="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                                    <ul className="nav nav-tabs">
                                        <li role="presentation"><a
                                            href="http://buscome.neoap.com/facew/manage ">首页管理</a></li>
                                        <li role="presentation"><a href="http://buscome.neoap.com/bazaar/superAdmin">店铺管理</a>
                                        </li>
                                        <li role="presentation"><a href="http://buscome.neoap.com/couponz/systemAdmin">团购管理</a>
                                        </li>
                                        <li role="presentation" className="active"><a
                                            href="http://buscome.neoap.com/miami/admin">外卖管理</a></li>
                                        <li role="presentation"><a href="http://buscome.neoap.com/bmall/admin">超市管理</a>
                                        </li>
                                        <li role="presentation"><a href="http://buscome.neoap.com/terra/admin/account">账号管理</a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </nav>

                    </div>
                    :
                    <nav className="navbar navbar-default navbar-fixed-top">
                        <div className="container">
                            <div className="navbar-header">
                                <button type="button" className="navbar-toggle collapsed" data-toggle="collapse"
                                        data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                                    <span className="sr-only">Toggle navigation</span>
                                    <span className="icon-bar"></span>
                                    <span className="icon-bar"></span>
                                    <span className="icon-bar"></span>
                                </button>
                                <a className="navbar-brand" href="#">外卖</a>
                            </div>
                            <div className="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                                <ul className="nav navbar-nav">
                                    <li><Link to="/categories">分类管理</Link></li>
                                    <li><Link to="/comments">评论管理</Link></li>
                                    {user.userType == 1 ?
                                        <li><Link to="/stores">商户管理</Link></li>
                                        :
                                        <li><Link to="/orders">订单管理</Link>
                                        </li>
                                    }
                                    {
                                        user.userType != 1 ?
                                            <li><Link to="/goods">菜品管理</Link></li> : <div></div>
                                    }
                                    {
                                        user.userType != 1 ?
                                            <li><Link to="/info">商户信息管理</Link></li> : <div></div>
                                    }
                                </ul>
                                <ul className="nav navbar-nav navbar-right">
                                    <li className="dropdown">
                                        <a href="#" className="dropdown-toggle" data-toggle="dropdown" role="button"
                                           aria-haspopup="true" aria-expanded="false">{user.nickName}<span
                                            className="caret"></span></a>
                                        <ul className="dropdown-menu">
                                            <li><a href="#">注销</a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </nav>

                }

            </div>





        )
    }
}

NavBar.propTypes = {
    user: PropTypes.shape({
        id: PropTypes.string.isRequired,
        userType: PropTypes.string.isRequired,
        nickName: PropTypes.string.isRequired
    }).isRequired

}


function mapStateToProps(state) {
    return {
        user: state.user
    }
}

export default connect(
    mapStateToProps
)(NavBar)







