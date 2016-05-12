/**
 * Created by zsh on 2016/4/23.
 */
import React, { Component, PropTypes } from 'react'


export default class RateStar extends Component{

    constructor(props) {
        super(props);
    }

    componentDidMount(){
        //console.log("grade==="+this.props.grade)
        //const self =this
        //const star = $('.rate-star a:eq('+(this.props.grade-1)+')');
        //star.addClass('active')
        //star.prevAll().addClass('active')
        //star.nextAll().removeClass('active')
        //$('.rate-star').attr('class','rate-star level'+(this.props.grade-1))
        //$('.rate-star-text').text(self.getStarText(self.props.grade));
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
                return ''
        }
    }

    render() {

        return (
            <p className={"rate-star dishGrade star level"+this.props.grade}>
                <span>
                    {
                        [1,2,3,4,5].map( i =>{
                            if(i<=this.props.grade){
                                return <img src="/miami/assets/images/customer/storeindex/star@2x.png"/>
                            }else{
                                return <img src="/miami/assets/images/customer/storeindex/Stars@2x.png"/>
                            }
                        })
                    }
                </span>
                <span className="rate-star-text">{this.props.hasText?this.getStarText(this.props.grade):this.props.grade+" 分"}</span>
            </p>
        )
    }
}
