package com.wq.dao;

import com.wq.pojo.Department;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 部门的Dao接口
 */
@Repository // 交给SpringBoot托管
public class DepartmentDao {
    // 模拟数据库中的数据
    // 静态变量 先加载
    private static Map<Integer, Department> departments = null;
    static{
        // 创建一个部门表
        departments = new HashMap<Integer, Department>();

        departments.put(101,new Department(101,"教学部"));
        departments.put(102,new Department(102,"市场部"));
        departments.put(103,new Department(103,"教研部"));
        departments.put(104,new Department(104,"后勤部"));
    }

    // 获得所有的部门信息 Map的所有值返回一个Collection
    public Collection<Department> getAllDepartments(){
        return departments.values();
    }

    // 通过Id得到部门
    public Department getDepartmentById(Integer id){
        return departments.get(id);
    }

//    public static void main(String[] args) {
//        System.out.println(departments.get(101));
//    }
}
