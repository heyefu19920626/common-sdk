# common-sdk
公共SDK，集成一些公共能力，并包含自我练习

## 编译

1. 进入项目目录
2. 执行命令`mvn install`
3. 如果有单测不过，一般是环境问题，可以修改环境或者注释掉或跳过单测

## 项目结构

- common-setting: 基础配置模块，用于统一声明该项目用的所有开源软件， 后续所有模块必须由此派生
- base: 基础模块，集成一些基础能力
  - com.tang.i18n: 集成国际化能力
  - com.tang.exception: 集成异常框架
- ssh: ssh连接模块，可以快速用其连接ssh服务器与上传文件


## 开发指导


### 国际化

1. 国际化资源文件必须放在resources/i18n目录下，文件名格式必须为message_${module}_${language}.properties, 且文件内容必须以${module}.开头， 其中${module}为模块名，${language}为语言,比如: message_base_zh_CN.properties为base模块的中文资源文件，该文件中的所有code都以base.开头
2. 可以直接调用I18nUtils进行国际化

### 异常处理

1. base中已提供了REST请求相关全局异常处理机制
2. 所有模块自定义异常，必须从BaseException中派生
3. 如果需要自定义某些异常对前端的响应
   1. 实现IRestExceptionHandler接口, 例如：TestExceptionHandler
   2. 在application.yml中将实现类的包路径赋值给tang.exception.scan，如果有多个，则以英文逗号分隔，例如：tang.exception.scan=com.tang.test
   3. com.tang不需要添加，会默认扫描