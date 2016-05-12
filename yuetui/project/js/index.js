

var SearchBox = React.createClass({
	render: function() {
		return (
			<div className="search-box">
				<div>
					<section id="search">
						<lable htmlFor="search-input">
							<i className="fa fa-search" aria-hidden="true"></i>
						</lable>
						<input id="search-input" type="text" placeholder="搜索商品" />
					</section>
				</div>
				<a href="#">分享</a>
			</div>
		);
	}
});

/*var SwiperBox = React.createClass({

});*/

var NavigationBox = React.createClass({
	render: function() {
		return (
			<ul className="navi-list">
				<li className="navi-choose">
					<a href="#">
						<img src="images/nopic.jpg" alt=""/>
						<p>签到</p>
					</a>
				</li>
				<li className="navi-choose">
					<a href="#">
						<img src="images/nopic.jpg" alt=""/>
						<p>抽奖</p>
					</a>
				</li>
				<li className="navi-choose">
					<a href="#">
						<img src="images/nopic.jpg" alt=""/>
						<p>购物车</p>
					</a>
				</li>
				<li className="navi-choose">
					<a href="#">
						<img src="images/nopic.jpg" alt=""/>
						<p>我的订单</p>
					</a>
				</li>
				<li className="navi-choose">
					<a href="#">
						<img src="images/nopic.jpg" alt=""/>
						<p>加盟专区</p>
					</a>
				</li>
				<li className="navi-choose">
					<a href="#">
						<img src="images/nopic.jpg" alt=""/>
						<p>常见问题</p>
					</a>
				</li>
			</ul>
		);
	}
});

var RecommendBox = React.createClass({
	render: function() {
		return (
			<div className="recommend-list">
				<p>推荐产品</p>
				<div className="show-list">
					<div className="recommend-thing">
						<a href="#">
							<img src="images/nopic.jpg" alt=""/>
							<p>按摩器</p>
						</a>
						<span>
							<i className="fa fa-usd" aria-hidden="true"></i>
							<span>10积分</span>
						</span>
					</div>
					<div className="recommend-thing">
						<a href="#">
							<img src="images/nopic.jpg" alt=""/>
							<p>按摩器</p>
						</a>
						<span>
							<i className="fa fa-usd" aria-hidden="true"></i>
							<span>10积分</span>
						</span>
					</div>
				</div>
			</div>
		);
	}
});

var HotProductBox = React.createClass({
	render: function() {
		return (
			<div className="hot-list">
				<p className="hello">热门兑换</p>
				<div className="show-list">
					<div className="hot-thing">
						<a href="">
							<img src="images/nopic.jpg" alt=""/>
							<p>按摩器</p>
						</a>
						<span>
							<i className="fa fa-usd" aria-hidden="true"></i>
							<span>10积分</span>
						</span>
					</div>
					<div className="hot-thing">
						<a href="">
							<img src="images/nopic.jpg" alt=""/>
							<p>按摩器</p>
						</a>
						<span>
							<i className="fa fa-usd" aria-hidden="true"></i>
							<span>10积分</span>
						</span>
					</div>
				</div>
			</div>
		);
	}
});

var IndexPage = React.createClass({
	render: function() {
		return (
			<div>
				<SearchBox />
				
				<NavigationBox />
				<RecommendBox />
				<HotProductBox />
			</div>
		);
	}
});

ReactDOM.render(
	<IndexPage />,
	document.getElementById("container")
);