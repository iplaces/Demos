import React, { Component } from 'react'
import StoreCategories from './../containers/StoreCategories'
import StoreContainer from './../containers/StoreList'

export default class Index extends Component{
      render() {
        return (
            <div className="index">
                <StoreCategories />
                <StoreContainer />
            </div>
        )
      }
}
