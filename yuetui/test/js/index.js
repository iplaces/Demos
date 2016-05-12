var SearchBox = React.createClass({
	render: function() {
		return (
			<div>
				<div>
					<section id="search">
						<lable htmlFor="search-input">
							<i className="fa fa-search" aria-hidden="true"></i>
							<span>搜索商品</span>
						</lable>
					</section>
					<input id="search-input" type="text"/>
				</div>
				<a href="#">分享</a>
			</div>
		);
	}
});



var NavigationBox = React.createClass({
	render: function() {
		return (
			<ul>
				<li>
					<a href="#">
						<img src="" alt=""/>
					</a>
					<p>签到</p>
				</li>
				<li>
					<a href="#">
						<img src="" alt=""/>
					</a>
					<p>抽奖</p>
				</li>
				<li>
					<a href="#">
						<img src="" alt=""/>
					</a>
					<p>购物车</p>
				</li>
				<li>
					<a href="#">
						<img src="" alt=""/>
					</a>
					<p>我的订单</p>
				</li>
				<li>
					<a href="#">
						<img src="" alt=""/>
					</a>
					<p>加盟专区</p>
				</li>
				<li>
					<a href="#">
						<img src="" alt=""/>
					</a>
					<p>常见问题</p>
				</li>
			</ul>
		);
	}
});

var RecommendBox = React.createClass({
	render: function() {
		return (
			<div>
				<p>推荐产品</p>
				<div>
					<a href="#">
						<img src="" alt=""/>
						<p></p>
					</a>
					<span>
						<i></i>
						<p></p>
					</span>
				</div>
				<div>
					<a href="#">
						<img src="" alt=""/>
						<p></p>
					</a>
					<span>
						<i></i>
						<p></p>
					</span>
				</div>
			</div>
		);
	}
});

var HotProductBox = React.createClass({
	render: function() {
		return (
			<div>
				<p>热门兑换</p>
				<div>
					<a href="">
						<img src="" alt=""/>
						<p></p>
					</a>
					<span>
						<i></i>
						<p></p>
					</span>
				</div>
				<div>
					<a href="">
						<img src="" alt=""/>
						<p></p>
					</a>
					<span>
						<i></i>
						<p></p>
					</span>
				</div>
			</div>
		);
	}
});

var IndexPage = React.createClass({
	render: function() {
		return (
			<div>
				123
			</div>
		);
	}
});

ReactDOM.render(
	<IndexPage />,
	document.getElementById("container")
);