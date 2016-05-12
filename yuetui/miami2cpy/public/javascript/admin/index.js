import 'babel-polyfill'
import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { Router, Route, hashHistory } from 'react-router'
import OrderPanel from './containers/OrderPanel'
import StorePanel from './containers/StorePanel'
import GoodPanel from './containers/GoodPanel'
import CatePanel from './containers/CatePanel'
import InfoPanel from './containers/InfoPanel'
import CommentPanel from './containers/CommentPanel'

import configureStore from './store/configureStore'


const store = configureStore()

render(
  <Provider store={store}>
    <Router history={hashHistory}>
      <Route path="/" component={CatePanel} />
        <Route path="/orders" component={OrderPanel} />
        <Route path="/stores" component={StorePanel} />
      <Route path="/goods" component={GoodPanel} />
        <Route path="/categories" component={CatePanel} />
        <Route path="/info" component={InfoPanel} />
        <Route path="/comments" component={CommentPanel}/>
    </Router>
  </Provider>,
  document.getElementById('root')
)
