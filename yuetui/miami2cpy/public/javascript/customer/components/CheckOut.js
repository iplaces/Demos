/**
 * Created by zsh on 2016/3/16.
 */
import React, { Component, PropTypes } from 'react'
import CheckList from '../containers/CheckList.js'
import ContactInfo from '../containers/ContactInfo.js'

export default class CheckOut extends Component{

    render() {
        const storeId = this.props.params.storeId
        return (
            <div className="container">
                <div className="inner-wrap">
                    <CheckList storeId={storeId}/>
                    <ContactInfo storeId={storeId}/>
                </div>
            </div>
        )
    }
}