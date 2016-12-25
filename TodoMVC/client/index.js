/**
 * Created by iplace on 2016/12/20.
 */
import 'babel-polyfill';
import React from 'react';
import { render } from 'react-dom';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import App from './components/App';
import todoApp from './reducers';
import 'todomvc-app-css/index.css';

const store = createStore(todoApp);

const rootElement = document.getElementById('container');
render(
    <Provider store={store}>
        <App />
    </Provider>,
    rootElement
);
