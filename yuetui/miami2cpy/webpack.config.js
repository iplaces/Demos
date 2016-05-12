var path = require('path')
var webpack = require('webpack')
var CUSTOMER_PATH = path.join(__dirname,'public/javascript/customer')
var ADMIN_PATH = path.join(__dirname,'public/javascript/admin')
module.exports = {
  devtool: false,
  //devtool: 'cheap-module-eval-source-map',
  entry: {
    //customer: path.resolve(CUSTOMER_PATH, 'index.js'),
    admin: path.resolve(ADMIN_PATH, 'index.js'),
    //vendors: ['react', 'moment']
  },
  output: {
    path: path.join(__dirname, 'public/javascript/dist'),
    filename: '[name].js',
    publicPath: '/static/'
  },
  plugins: [
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.HotModuleReplacementPlugin(),
    new webpack.DefinePlugin({
      'process.env':{
        'NODE_ENV': JSON.stringify('production')
      }
    }),
    new webpack.optimize.UglifyJsPlugin({
      compress:{
        warnings: true
      }
    })
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        loaders: ['babel'],
        exclude: /node_modules/,
        include: __dirname
      },
      {
        test: /\.s?css$/,
        loader: 'style!css!sass'
      }
    ]
  },
  //externals: {
  //  moment: true
  //},
  resolve: {
    alias: {
      moment: "moment/min/moment-with-locales.min.js"
    }
  }
}


// When inside Redux repo, prefer src to compiled version.
// You can safely delete these lines in your project.
var reduxSrc = path.join(__dirname, '..', '..', 'src')
var reduxNodeModules = path.join(__dirname, '..', '..', 'node_modules')
var fs = require('fs')
if (fs.existsSync(reduxSrc) && fs.existsSync(reduxNodeModules)) {
  // Resolve Redux to source
  module.exports.resolve = { alias: { 'redux': reduxSrc } }
  // Our root .babelrc needs this flag for CommonJS output
  process.env.BABEL_ENV = 'commonjs'
  // Compile Redux from source
  module.exports.module.loaders.push({
    test: /\.js$/,
    loaders: ['babel'],
    include: reduxSrc
  })
}
