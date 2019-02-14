## 结合modeler编辑器运行方式

* 想办法初始化activiti需要的基础信息数据表，配置jdbc信息
* 通过act/model/create接口创建一个新的流程资源图，没有编辑的前提下全都是空的，需要到act_re_model表里面去查找ID
* 然后通过activi/modeler.html?modelId=**访问刚刚新建的流程图，目前的保存功能是完全可行的，/{modelId}/save接口