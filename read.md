# wutiao广告指标计算及定义细节

### 后端计算的指标    

    - view                          展示数    
    - click	                        点击数    
    - activation	                非邀请激活数（设备激活）    
    - invite_act	                邀请激活（设备激活）    
    - register                      注册数 （不含邀请注册）    
    - invite_reg	                邀请注册
    - verification	                真人验证数 ， 非实时             
    - sumwb	                        wb总数 ， 非实时
    - active_day_dau                去新dau （设备留存），非实时
    - active_day	                1日留存，非实时      
    - active_nday	                n日留存，非实时
    
    
    
### 前端计算的指标

    - 名称            来源方式            计算方式            数据源
    - 总成本           读表                                  腾讯 | mg_ios_adver.admobile_cost
    - 广告展现         读表                                   腾讯 | mg_ios_adver.admobile_cost
   
   
   
   
   
   
   -广告展现         读表
   腾讯 | mg_ios_adver.admobile_cost
   广告点击
   读表
   -
   腾讯 | mg_ios_adver.admobile_cost
   点击价格
   
   总成本/广告点击
   
   ecMP
   
   点击价格×广告点击率×1000
   
   广告点击率
   
   广告点击/广告展现
   
   展示数
   读表
   
   wutiao_data_{year}{month}
   点击数
   读表
   
   wutiao_data_{year}{month}
   点击率
   
   点击数/展示数
   
   激活数
   读表
   
   
   点击激活率
   
   激活数/广告点击
   wutiao_data_{year}{month} | mg_ios_adver.admobile_cost
   下载激活率
   
   激活数/页面点击
   wutiao_data_{year}{month} | mg_ios_adver.admobile_cost
   激活成本
   
   总成本/邀请激活数
   wutiao_data_{year}{month} | mg_ios_adver.admobile_cost
   总激活成本
   
   总成本/总激活
   wutiao_data_{year}{month} | mg_ios_adver.admobile_cost
   注册率
   
   邀请注册/邀请激活
   wutiao_data_{year}{month}
   注册成本
   
   总成本/邀请注册
   wutiao_data_{year}{month} | mg_ios_adver.admobile_cost
   总注册成本
   
   总成本/注册数
   wutiao_data_{year}{month} | mg_ios_adver.admobile_cost
   去新活跃率
   
   去新dau / 渠道总激活
   wutiao_data_{year}{month}
   人均邀请注册
   
   被邀请注册数/注册数
   wutiao_data_{year}{month}
   真人验证率
   
   真人验证数/激活数
   wutiao_data_{year}{month} | wutiao_user_portrait
   次日留存率
   
   次日留存数/当日非邀请激活设备数
   wutiao_data_{year}{month}
   3日留存率
   
   3日留存数/次日非邀请激活设备数
   
   
	    