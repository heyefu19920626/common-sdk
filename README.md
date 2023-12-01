# common-sdk
公共SDK，集成一些公共能力，并包含自我练习

## 项目结构

- common-setting: 基础配置模块，用于统一声明该项目用的所有开源软件， 后续所有模块必须由此派生
- base: 基础模块，集成一些基础能力
  - com.heyefu.i18n: 集成国际化能力


## 开发指导


### 国际化

1. 国际化资源文件必须放在resources/i18n目录下，文件名格式必须为message_${module}_${language}.properties, 且文件内容必须以${module}.开头， 其中${module}为模块名，${language}为语言,比如: message_base_zh_CN.properties为base模块的中文资源文件，该文件中的所有code都以base.开头
2. 可以直接调用I18nUtils进行国际化