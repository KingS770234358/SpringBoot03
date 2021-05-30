package com.wq.dao;

import com.wq.pojo.Department;
import com.wq.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工的Dao接口
 */
@Repository
public class EmployeeDao {
    // 模拟数据库中的数据
    private static Map<Integer, Employee> employees = null;
    // 用于获取员工所属的部门
    @Autowired
    private DepartmentDao departmentDao;

    // 创建一个员工表
    static {
        employees = new HashMap<Integer, Employee>();

        employees.put(10011, new Employee(10011, "张全蛋", "@1", 0,
                new Department(101, "教学部"), new Date()));
        employees.put(10012, new Employee(10012, "周杰伦", "@2", 0,
                new Department(102, "市场部"), new Date()));
        employees.put(10013, new Employee(10013, "李晓华", "@3", 1,
                new Department(103, "教研部"), new Date()));
        employees.put(10014, new Employee(10014, "M", "@4", 1,
                new Department(104, "后勤部"), new Date()));
    }
    // 设置主键自增
    private static Integer baseId = 10014;
    // 增加一个员工
    public void save(Employee e){
        // 当员工不存在ID的时候说明是新员工的添加 所以自增Id
        // 当员工存在ID的时候 说明是老员工的修改信息 不自增Id
        if(e.getId()==null){
            e.setId(++baseId);
        }
        // 这里添加一个员工的时候会接收到一个部门的id 部门的其他字段都是空的 所以要先取得整个部门信息
        System.out.println(e.getDepartment().getId());
        e.setDepartment(departmentDao.getDepartmentById(e.getDepartment().getId()));
        employees.put(e.getId(),e);
    }
    // 查询所有员工信息
    public Collection<Employee> getAllEmployees(){
        return employees.values();
    }
    // 通过Id查询员工X信息
    public Employee getEmployeeById(Integer id){
        return employees.get(id);
    }
    // 通过Id删除员工
    public void deleteEmployee(Integer id){
        employees.remove(id);
    }
}
