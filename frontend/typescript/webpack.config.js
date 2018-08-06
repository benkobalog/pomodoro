var path = require('path');

module.exports = {
 entry: './src/main.ts',
 resolve: {
   extensions: ['.webpack.js', '.web.js', '.ts', '.js']
 },
 module: {
   rules: [
     { test: /\.ts$/, loader: 'ts-loader' }
   ]
 },
 output: {
   filename: 'main.js',
   path: path.resolve(__dirname+"/../public", 'javascripts')
 }
}