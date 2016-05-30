/**
 * Created by iplace on 2016/5/30.
 */

import React from "react";
import imageLogo from "../images/y18.gif";
import "../styles/NewsHeader.css";

export default class NewsHeader extends React.Component {
    getLogo() {
        /*获取logo*/
        return (
            <div className="newsHeader-logo">
                <a href="http://www.google.com"><img src={imageLogo} /></a>
            </div>
        );
    }
    getTitle() {
      return (
          <div className="newsHeader-title">
              <a className="newsHeader-textLink" href="http://www.google.com">Hacker News</a>
          </div>
      );
    }
    getNav() {
        var navLinks = [
            {
                name: "new",
                url: "newest"
            },
            {
                name: "comments",
                url: "newscomments"
            },
            {
                name: "show",
                url: "show"
            },
            {
                name: 'ask',
                url: 'ask'
            },
            {
                name: 'jobs',
                url: 'jobs'
            },
            {
                name: 'submit',
                url: 'submit'
            }
        ];

        return (
            <div className="newsHeader-nav">
                {
                    navLinks.map(function(navLink) {
                        return (
                            <a key={navLink.url} href="#" className="newsHeader-navLink">
                                {navLink.name}
                            </a>
                        );
                    })
                }
            </div>
        );
    }
    getLogin() {
        return(
            <div className="newsHeader-login">
                <a href="#" className="newsHeader-textLink">login</a>
            </div>
        );
    }

    render() {
        return (
            <div className="newsHeader">
                {this.getLogo()}
                {this.getTitle()}
                {this.getNav()}
                {this.getLogin()}
            </div>
        );
    }
}
