const { HotModuleReplacementPlugin, DefinePlugin } = require("webpack");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const path = require("path");
const WatchExternalFilesPlugin = require("webpack-watch-external-files-plugin");
const chalk = require("chalk");
const ProgressBarPlugin = require("progress-bar-webpack-plugin");
const BundleAnalyzerPlugin =
  require("webpack-bundle-analyzer").BundleAnalyzerPlugin;
const SpeedMeasurePlugin = require("speed-measure-webpack-plugin");
const smp = new SpeedMeasurePlugin();
const TerserPlugin = require("terser-webpack-plugin");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");

const isProduction = JSON.stringify(process.env.NODE_ENV) === "production";
const NODE_ENV = JSON.stringify(isProduction ? "production" : "development");
const IS_DOCKER = process.env.IS_DOCKER ? JSON.stringify(true) : undefined;
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const PurgeCSSPlugin = require("purgecss-webpack-plugin");
const glob = require("glob");

const config = {
  mode: "development",
  output: {
    filename: isProduction
      ? "[name].[contenthash].bundle.js"
      : "[name].bundle.js"
  },
  entry: "./src/index.tsx",
  devServer: {
    static: path.join(__dirname, "build"),
    historyApiFallback: true,
    port: 3000,
    open: true,
    hot: true
  },
  module: {
    rules: [
      {
        test: /\.(css)$/,
        use: [
          isProduction ? MiniCssExtractPlugin.loader : "style-loader",
          "css-loader"
        ]
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
    new ProgressBarPlugin({
      format: `  :msg [:bar] ${chalk.green.bold(":percent")} (:elapsed s)`
    }),
    new MiniCssExtractPlugin(),
    new DefinePlugin({
      "process.env.NODE_ENV": NODE_ENV,
      "process.env.IS_DOCKER": IS_DOCKER
    }),
    new WatchExternalFilesPlugin({
      files: ["../server/src/types/shared.ts"]
    }),
    new HtmlWebpackPlugin({
      template: "public/index.html",
      favicon: "src/style/icon192x192.png"
    }),
    new HotModuleReplacementPlugin()
  ],
  devtool: "inline-source-map"
};

module.exports = smp.wrap(
  isProduction
    ? {
        ...config,
        mode: "production",
        plugins: [
          ...config.plugins,
          new BundleAnalyzerPlugin(),
          new MiniCssExtractPlugin({
            filename: "[name].css"
          }),
          new PurgeCSSPlugin({
            paths: glob.sync(`${path.resolve(__dirname, "src")}/**/*`, {
              nodir: true
            })
          })
        ],
        splitChunks: {
          chunks: "all",
          cacheGroups: {
            vendors: {
              test: /[\\/]node_modules[\\/]/,
              chunks: "all",
              Priority: 10,
              enforce: true
            }
          }
        },
        optimization: {
          moduleIds: "deterministic",
          runtimeChunk: true,
          minimizer: [
            new CssMinimizerPlugin({
              parallel: 4
            }),
            new TerserPlugin({
              parallel: 4,
              terserOptions: {
                parse: {
                  ecma: 8
                },
                compress: {
                  ecma: 5,
                  warnings: false,
                  comparisons: false,
                  inline: 2
                },
                mangle: {
                  safari10: true
                },
                output: {
                  ecma: 5,
                  comments: false,
                  ascii_only: true
                }
              }
            })
          ]
        }
      }
    : config
);
