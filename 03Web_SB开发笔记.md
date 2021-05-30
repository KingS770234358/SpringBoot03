SpringBootWeb管理系统开发笔记

[小知识点:]
1.控制器中一个方法或者一个类实现多个路由映射
@RequestMapping({url1,url2})
删掉首页控制器类,直接在MyMvcConfig中接管它的路由映射
[像这种根目录下的路由映射最好由自定义的MvcConfig接管]

2.静态资源css img等的引入(使用thymeleaf接管)
· pom.xml中加入thymeleaf的启动器依赖(两个)
· html页面上方声明thymeleaf的命名空间
· 基于thymeleaf的页面的路由写法  三个目录 /public /static /resources 还有一个后缀/**
  使用th:href = "@{}" 来获取路由,路径直接从上述三个路径下的文件夹开始写即可
  eg:  
    <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}"/>
    <link rel="stylesheet" th:href="@{/css/signin.css}"/>
    <img class="mb-4" th:src="@{/images/bootstrap-solid.svg}" alt="" width="72" height="72">
· 如果页面不刷新,可能需要在全局配置文件application.properties中关闭thymeleaf模板引擎的缓存

·配置项目的虚拟路径
## 配置项目虚拟目录 localhost:8080/xxxx/urlmapping
server.servlet.context-path: /03manage_sys_sb
配置了虚拟路径之后,客户端浏览器在发送请求的时候,都要在原url的localhost:8080/后面加上这个虚拟路径
###########################################################################
=====>项目的文件都不需要改动,如下,会自动在在url前面加上/xxxx
registry.addViewController("/index").setViewName("index");
registry.addViewController("/index/").setViewName("index");
registry.addViewController("/index.html").setViewName("index");
###########################################################################
=====>thymeleaf类似以下的资源引用
<link th:href="@{/css/bootstrap.min.css}" rel="stylesheet">
url都会自动的被加上/xxxx,           ## 以下是查看页面原网页的结果
<link th:href="/03manage_sys_sb/css/bootstrap.min.css" rel="stylesheet">
###########################################################################
总结下首页配置:
所有的页面都需要用thymeleaf接管,src、href等url的引用都需要加上@{}

3.页面的国际化
· file->setting->file encoding->utf-8
· do在classpath的resource文件夹下建立i18n文件夹(i18n是 internationalization的缩写 i和n之间有18个单词 类似k8s kubernetes)
  创建login.properties配置文件
  创建login_zh_CN.properties(两个文件会被自动合并)=====>[相当于是同一类的配置组]
  右键可以new add Property file to Resource bundle添加en_US
· 点击配置文件左下角的Resource bundle可以可视化配置 可以[增加相同的键 赋不同的值]
· 以上几份配置文件都有相同的键 但是有不同的值 配置好后 双击shift找到[MessageSourceAutoConfiguration类]
  发现其中有个 [MessageSourceProperties 类带有@ConfigurationProperties注解] 
  可以在[全局配置文件]中使用[spring.messages]进行配置
  #################################################################################
  #  就是在这里 将配置文件种设置语言配置组的路径,供MessageSourceAutoConfiguration读取
  #################################################################################
  ----点进[MessageSourceProperties 类]进一步查看 basename"message"; encoding="UTF-8"
      这个basename就是可以设置配置文件的路径 
      ## 在application.yaml文件中 国际化配置文件组路径设置 设置成i18n文件夹下的login配置组(绑定配置文件组)
      spring.messages.basename: i18n.login
· do在页面中使用thymeleaf的消息表达式取出 login配置组 的属性 #{}
  例如:<input type="password" class="form-control" th:placeholder="#{login.password}" required="">
· <html lang="en-US" 打开f12控制台查看请求头中的语言信息
    Accept-Language: zh-CN,zh;q=0.9
· 双击shift搜索 AcceptHeaderLocaleContextResolver 这个类
  双击搜索WebMvcAutoConfiguration 这个类 发现里面有个localeResolver方法(locale地区) 如果用户有设置 就用用户的 没有就用默认的
  这个部分就是实现了 localeResolver的接口 我们可以自己实现这个接口
· 在中文连接里放入语言请求参数[直接在url末尾的括号里放入参数键值对即可]
<a class="btn btn-sm" th:href="@{/03manage_sys_sb/index.html(l='zh_CN')}">中文</a>
· do在config文件夹里 创建自己的localeResolver类 [对照上面WebMvc里的实现进行自己的实现]
  地区解析器 最后就是返回一个 地区locale对象就行了 根据 地区参数进行创建这个地区对象 然后返回即可
  [一定要把写好的这个类 在MyMVCConfiguration中使用@Bean标签注入到spring容器中
  也就是说MyMVCConfiguration实现了WebMvcConfigurer,所以需要在这里面进行注入]
国际化
[总结:1.配置自己的i18n文件 
     2.在全局配置文件中绑定语言配置文件组的信息 spring.messages.basename: i18n.login
     3.自定义一个LocalResolver地区解析器 实现接口 最后一定要在WebMvcConfigurer中使用@Bean注入到Spring容器中
     4.在thymeleaf引擎实现的页面中使用#{key}来获取语言配置文件中的文本信息
     5.关于语言配置文件组的命名 login_en_US 就当他是约定
     6.关于如何在页面中设置请求头里的语言参数:JS实现]
     
4.实现登录功能(登录表单action设置)
<!-- 如果msg为空,则不显示消息
     thymeleaf 强大工具 #strings...
     not 取反
     ${} 取表达式的值
     th:if=""为真则显示
-->
<p style="color: red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}"></p>
######################################################################
·[工具类使用详细见thymeleaf的文档pdf第19节] [#strings.isEmpty(msg)]
######################################################################
·[StringUtils].isEmpty(name) 判断name字符串是否为空
用户名密码验证成功之后 要重定向到 dashboard页面
```首先要在MyMVCConfiguration里视图控制器里面增加dashboard页面的映射
############################################################################################################
#[registry.addViewController("/main.html").setViewName("dashboard");]===>这样映射之后url可以清晰反映页面的信息
############################################################################################################
```然后重定向到这个url [return "redirect:/main.html";]
############################################################################################################
############################## [对于任何连接都能访问主页面的问题要通过过滤器解决] ##############################
############################################################################################################

5.拦截器
在config中实现拦截器HandlerInterceptor接口,有三个方法 preHandler postHandler afterHandler 主要重写第一个方法
登录成功之后 session中应该存储着用户的信息
· 在MyMVCConfiguration中有总的页面视图跳转控制器,比自己写Controller上的RequestMapping映射方便
· request.setAttribute("msg","请先登录!"); 可以直接在request中存储信息
· 不使用response.sendRedirect("/index"); 而是使用request.getRequestDispatcher("/index.html").forward(request, response); 
· 在MyMVCConfiguration添加拦截器才行
  // 3.添加自己定义的拦截器
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
      // /** 拦截所有的请求
      // excludePathPattern 对于登录页面的请求不能被拦截
      //                    由登录页面提交过来的登录验证请求也不能被拦截
      //                    静态资源也不能被拦截
      registry.addInterceptor(new MyHandlerInterceptor())
              .addPathPatterns("/**").
              excludePathPatterns("/","/index","/index/","/index.html","/user/login",\
              "/css/**","/js/**","/img/**");
  }
· 在页面中 从session里获取userName属性 [[ ${session.userName} ]]

6.员工列表展示
· 在dashboard页面中先把 customers 改成 员工
· 这个地方的超链接是页面原来自带的, 要改成我们自己需要的超链接
<li class="nav-item">
    <a class="nav-link" href="http://getbootstrap.com/docs/4.0/examples/dashboard/#">
    ====><a class="nav-link" th:href="@{/getEmployeeList}">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-users">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
        </svg>
        员工列表
    </a>
</li>
· 编写EmployeeController处理员工相关的请求
[关于两个模仿Dao作用的类DepartmentDao EmployeeDao 都需要使用@Repository注解注入到Spring容器中才能使用]
· 给前端传输数据的几种工具 [request request.session model]
· 把跟员工相关的页面 list 单独放入template里的emp文件夹
· RequestMapping一下,然后返回/emp/list

7.[抽取页面的公共部分],然后通过fragment插入/替代的方式使用
· 提取出公共部分的代码,整理好都放入template文件夹下的commons文件夹的commons页面中,commons页面中出了html标签都删除
  [定义片段]在页面中使用①[th:fragment=""]的方式
  <nav class="col-md-2 d-none d-md-block bg-light sidebar" th:fragment="leftSideBar">
  这个片段下有两个标签<a 在这个地方 ②[使用片段引用处传入的参数]
  <a th:class="${active=='main'?'nav-link active':'nav-link'}" th:href="@{/main.html}">
  <a th:class="${active=='list'?'nav-link active':'nav-link'}" th:href="@{/getEmployeeList}">
                              
· [引用片段]在需要使用公共部分的页面中③div th:replace或者insert="~{文件夹路径/页面名称::片段名称(参数名='参数值')}的方式[引用片段]
  通过<div th:replace="~{commons/commons::leftSideBar(active='list')}"></div>
      <div th:replace="~{commons/commons::leftSideBar(active='main')}"></div>
  [replace一定要使用div包括起来]
  或者用th:insert也可以
===>[定义片段处可以取出引用片段时传入的参数 进行判断 设置属性]
    a页面引用片段就相当于把所以用的片段直接放入a页面当中

8.使用thymeleaf引擎的th:each(两种方式显示员工列表)
<tr  th:each="emp:${emps}">
    <td>[[ ${emp.getId()} ]]</td>
    <td>[[ ${emp.getLastName()} ]]</td>
    <td>[[ ${emp.getEmail()} ]]</td>
    <!-- 男女判断 -->
    <td th:text="${emp.getGender()==0?'女':'男'}"/>
    <td th:text="${emp.getDepartment().getDepartmentName()}"/>
    <!-- 日期格式转换 -->
    <td th:text="${#dates.format(emp.getBirth(),'yyyy-MM-dd HH:mm:ss')}"/>
    <!-- 操作部分 -->
    <td>
        <button class="btn btn-sm btn-primary">编辑</button>
        <!-- danger变成红色 -->
        <button class="btn btn-sm btn-danger">删除</button>
    </td>
</tr>

9.添加员工 创建添加员工页面
复制list页面 改main的部分 在里面添加一个表单
(可以去Bootstrap中文网https://v4.bootcss.com/docs/4.0/components/forms/ 里找,这里直接使用狂神写好的)
· 巩固th:each的使用 通过th:value值来向后台提交填写的部门的id -->
<div class="form-group">
    <label>department</label>
    <select class="form-control" name="department">
        <option  th:each="department:${departments}" th:value="${department.getId()}">
            [[ ${department.getDepartmentName()} ]]
        </option>
    </select>
</div>
· 单选框默认选中
<div class="form-group">
    <label>Gender</label><br/>
    <div class="form-check form-check-inline">
        <input class="form-check-input" type="radio" name="gender"  value="1" checked>
        <label class="form-check-label">男</label>
    </div>
    <div class="form-check form-check-inline">
        <input class="form-check-input" type="radio" name="gender"  value="0">
        <label class="form-check-label">女</label>
    </div>
</div>
· 接下来表单提交请求
<!-- 添加员工的表单 -->
<form th:action="@{/addEmp}" method="post"> 
<div class="form-group">
    <label>department</label>
    <select class="form-control" name="department.id">
        <!-- 通过value来提交填写的部门的id -->
        <option  th:each="department:${departments}" th:value="${department.getId()}">
            [[ ${department.getDepartmentName()} ]]
        </option>
    </select>
</div>
#########################################################################
[这里department部分只传了一个id 所以name的地方要写 name="department.id"]
#########################################################################
控制器部分
// 使用RestFul风格, 根据提交请求的方式来判断使用哪个方法
@GetMapping("/addEmp") //这里是默认的get方法
public String toAddPage(Model model){
    //查出所有的部门,供添加员工的时候选择员工所在的部门
    Collection<Department> departments = departmentDao.getAllDepartments();
    model.addAttribute("departments",departments);
    return "/emp/addEmp";
}
@PostMapping("/addEmp")
public String addEmp(Employee e, Model model){
    System.out.println("添加用户填写的用户:"+e);
    //请求转向获取所有员工的控制器
    return "redirect:/getEmployeeList";
}
[这里的forward: 和 redirect: 是thymeleaf视图解析器里定义的]
· html控件里面的placeholder值不会提交到后台!!!
·  <input type="email" 会自动检查输入知否符合邮件格式
· 错误:[在员工Dao中没有注入departmentDao所以 无法根据添加员工的时候传入的部门id获取到整个部门
在员工列表展示页面之所以还能获取到部门的名字,是因为员工对象里面存储的部门是手动创建的,
所以可以使用部门的getDepartmentName方法,但是添加员工的时候,需要直接调用departmentDao的getDepartmentById方法,
因为departmentDao没有注入 所以为空,无法执行方法]
· SpringMVC默认时间格式的问题:spring.mvc.date-format,在WebMvcProperties类中,默认使用dd/MM/yyyy的时间格式,
可以在全局配置文件application.yaml中设置spring.mvc.date-format: yyyy-MM-dd HH:mm:ss
编写自己的时间解析器 DateParser.class 并注册时间解析器
|注册|然后在WebMvcConfigurer中重写addFormatter方法registry.addConverter(new DateParser());

10 修改员工
· list页面上修改部分加上跳转到修改页面的href 在每个href上面都要附加上员工的id 橙色关键
<a th:href=["@{/updateEmp/}+${emp.getId()}"] class="btn btn-sm btn-primary">编辑</a>
· 控制器中编写跳转到添加员工页面的控制器方法
// 修改员工
@GetMapping("/updateEmp/{id}") //这里是默认的get方法 RestFul风格传入员工Id参数
public String toupdatePage(@PathVariable("id")Integer eId, Model model){
    //查出所有的部门,供添加员工的时候选择员工所在的部门
    Collection<Department> departments = departmentDao.getAllDepartments();
    model.addAttribute("departments",departments);
    // 要放入当前要修改的员工信息
    Employee e = employeeDao.getEmployeeById(eId);
    model.addAttribute("emp",e);
    return "/emp/updateEmp";
}
· 在修改员工也面显示员工的原始信息
--普通的设置多使用th:value="${emp.getLastName()}"
--单选框的设置  [th:checked="${emp.getGender()==0}"]
<div class="form-group">
    <label>Gender</label><br/>
    <div class="form-check form-check-inline">
        <input th:checked="${emp.getGender()==0}" class="form-check-input" type="radio" name="gender"  value="1">
        <label class="form-check-label">男</label>
    </div>
    <div class="form-check form-check-inline">
        <input th:checked="${emp.getGender()==1}" class="form-check-input" type="radio" name="gender"  value="0">
        <label class="form-check-label">女</label>
    </div>
</div>
--复选框的设置 [th:selected="${ department.getId() == emp.getDepartment().getId()}"]
<div class="form-group">
    <label>department</label>
    <select class="form-control" name="department.id">
        <!-- 通过value来提交填写的部门的id -->
        <option th:selected="${department.getId()==emp.getDepartment().getId()}" th:each="department:${departments}" th:value="${department.getId()}">
            [[ ${department.getDepartmentName()} ]]
        </option>
    </select>
</div>
--日期的格式化
<div class="form-group">
    <label>Birth</label>
    <input th:value="${#dates.format(emp.getBirth(),'yyyy-MM-dd HH:mm:ss')}" type="text" name="birth" class="form-control" placeholder="kuangstudy" required>
</div>
--要在隐藏域里存储员工Id的信息 防止被认为是新员工Id自增
<!-- 使用隐藏域来存储已有员工的Id 防止后台获得到的员工Id为空被认为是新员工-->
<input th:value="${emp.getId()}" type="hidden" name="id">
--注意性别的数字标识前后要统一

11.删除员工 
logout处写上对应的href
// 注销用户
@RequestMapping("/user/logout")
public String logout(HttpSession session){
    // 这里删除的一定要跟上面设置的时候的键值一样
    session.removeAttribute("userName");
    return "redirect:/index.html";
}
12.错误页面
· thymeleaf的约定:在template目录下创建error页面 存放404 500 等错误需要跳转到的页面
