const { HotModuleReplacementPlugin, DefinePlugin } = require("webpack");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const path = require("path");
const WatchExternalFilesPlugin = require("webpack-watch-external-files-plugin");

const config = {
  mode: process.env.NODE_ENV,
  output: {
    publicPath: "/"
  },
  entry: "./src/index.tsx",
  module: {
    rules: [
      {
        test: /\.(css)$/,
        use: ["style-loader", "css-loader"]
      },
      {
        test: /\.xml$/i,
        use: "raw-loader"
      },
      {
        test: /\.(ts|js)x?$/i,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            presets: [
              "@babel/preset-env",
              ["@babel/preset-react", { runtime: "automatic" }],
              "@babel/preset-typescript"
            ]
          }
        }
      }
    ]
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js"]
  },
  plugins: [
    new DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify(process.env.NODE_ENV)
    }),
    new WatchExternalFilesPlugin({
      files: ["../server/src/types/shared.ts"]
    }),
    new HtmlWebpackPlugin({
      template: "public/index.html"
    }),
    new HotModuleReplacementPlugin()
  ],
  devtool: "inline-source-map",
  devServer: {
    static: path.join(__dirname, "build"),
    historyApiFallback: true,
    port: 3000,
    open: true,
    hot: true
  }
};

module.exports = config;
