import 'babel-polyfill'
import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { Router, Route, hashHistory, IndexRoute } from 'react-router'

import Header from './containers/Header.js'
import Index from './components/Index.js'
import storeIndex from './components/storeIndex'
import FoodList from './containers/FoodList'
import remarkList from './containers/remarkList'
import CheckOut from './components/CheckOut'
import OrderList from './containers/OrderList'
import OrderDetail from './containers/OrderDetail'
import SearchResult from './containers/SearchResult'
import configureStore from './store/configureStore'

import '../../css/customer/styles.scss'

const store = configureStore()

render(
    <Provider store={store}>
    <Router history={hashHistory}>
        <Route path="/" component={Header} >
            <Route path="/storeIndex/:storeId" component={storeIndex} >
                <IndexRoute  component={FoodList} />
                <Route name="remark" path="/storeIndex/:storeId/remark" component={remarkList} />
            </Route>
            <Route path="order/checkout/:storeId" component={CheckOut} />
            <Route path="order/list" component={OrderList} />
            <Route path="order/detail/:orderId" component={OrderDetail} />
            <Route path="search/:searchKey" component={SearchResult} />
            <IndexRoute component={Index} />
        </Route>
    </Router>
    </Provider>    ,
  document.getElementById('root')
)
