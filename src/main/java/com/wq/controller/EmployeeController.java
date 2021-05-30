package com.wq.controller;

import com.wq.dao.DepartmentDao;
import com.wq.dao.EmployeeDao;
import com.wq.pojo.Department;
import com.wq.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/***
 * 员工控制器
 * 处理跟员工相关的请求
 */
@Controller
public class EmployeeController {

    // 为了方便测试 直接注入一个Dao对象(本来应该是调用Service层)
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    DepartmentDao departmentDao;

    @RequestMapping("/getEmployeeList")
    public String getEmployeeList(Model model){
        Collection<Employee> allEmployees = employeeDao.getAllEmployees();
        model.addAttribute("emps",allEmployees);
        // 返回template目录下 emp目录里的list页面
        // !!!这里不走WebMVCConfiguration配置的全局视图控制器 走的是thymeleaf的视图解析器
        return "/emp/list";
    }

    // 添加员工
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
        employeeDao.save(e);
        return "redirect:/getEmployeeList";
    }

    // 修改员工
    @GetMapping("/updateEmp/{id}") //这里是默认的get方法
    public String toupdatePage(@PathVariable("id")Integer eId, Model model){
        //查出所有的部门,供添加员工的时候选择员工所在的部门
        Collection<Department> departments = departmentDao.getAllDepartments();
        model.addAttribute("departments",departments);
        Employee e = employeeDao.getEmployeeById(eId);
        model.addAttribute("emp",e);
        return "/emp/updateEmp";
    }
    @PostMapping("/updateEmp") //这里是默认的get方法
    public String updateEmp(Employee e, Model model){
        //查出所有的部门,供添加员工的时候选择员工所在的部门
        System.out.println("修改用户填写的用户:"+e);
        //请求转向获取所有员工的控制器
        employeeDao.save(e);
        return "redirect:/getEmployeeList";
    }
    // 删除员工deleteEmp
    @RequestMapping("/deleteEmp/{id}") //这里是默认的get方法
    public String deleteEmp(@PathVariable("id") Integer eId, Model model){
        //查出所有的部门,供添加员工的时候选择员工所在的部门
        System.out.println("修改用户填写的用户:"+eId);
        //请求转向获取所有员工的控制器
        employeeDao.deleteEmployee(eId);
        return "redirect:/getEmployeeList";
    }
}
