# marmot
[![All Contributors](https://img.shields.io/badge/all_contributors-3-orange.svg?style=flat-square)](#contributors)
marmot主要是统一多业务线的ID生成方式
# ID模式
marmot目前支持三种不同的ID模型：全局唯一ID、随机规划ID、顺序分片ID
# 配置
由于需要存放业务系统标识、ID定义之类的，故需要配置一个redis,详见application.properties
# 实现原理
### 规划算法
##### 1、正常获得ID步骤
  业务系统获得随机ID，通过dubbo接口调用。
  ID已经生成好，放在Seg队列中，直接从Seg队列头获取随机ID，返回给服务。
##### 2、加载新Seg步骤
  如果Seg队列用尽，先装载Seg2为新的队列，直接获得ID，返回服务。
  异步开始生成新Seg，利用Redis控制全局锁，保证全局只有一个服务生成ID。
  规划算法预生成一组ID，生成新Seg，用于下次Seg用尽时使用。
  
![规划算法](https://github.com/kaishustory/marmot/blob/master/plan.png)
### 全局顺序ID
##### 1、获得ID步骤
  Redis inc 原子加，返回顺序ID。
  
![全局顺序ID](https://github.com/kaishustory/marmot/blob/master/global.png)
# 使用方式
配置文件配置zookeeper、redis,按照按照标准的springboot启动方式启动service即可
对应的接口服务如下:<br/>
  全局唯一ID：IGlobalService::get<br/>
  随机规划ID：IPlanService::get<br/>
  顺序分片ID：ISegmentService::get

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/liaoshiwei"><img src="https://avatars1.githubusercontent.com/u/55678628?v=4" width="100px;" alt="liaoshiwei"/><br /><sub><b>liaoshiwei</b></sub></a><br /><a href="https://github.com/kaishustory/marmot/commits?author=liaoshiwei" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/liguoyangik"><img src="https://avatars3.githubusercontent.com/u/55503412?v=4" width="100px;" alt="李国洋"/><br /><sub><b>李国洋</b></sub></a><br /><a href="#design-liguoyangik" title="Design">🎨</a></td>
    <td align="center"><a href="http://shelltea.github.io"><img src="https://avatars2.githubusercontent.com/u/864375?v=4" width="100px;" alt="shelltea"/><br /><sub><b>shelltea</b></sub></a><br /><a href="https://github.com/kaishustory/marmot/commits?author=shelltea" title="Code">💻</a></td>
  </tr>
</table>

<!-- ALL-CONTRIBUTORS-LIST:END -->

