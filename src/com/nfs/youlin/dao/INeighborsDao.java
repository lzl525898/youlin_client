package com.nfs.youlin.dao;

import java.util.List;

public interface INeighborsDao {
	//返回所有对象
	public List<Object> findAllObject();
	//返回所有对象
	public List<Object> findPointTypeObject(int type);
	//存储数据到对象
	public void saveObject(Object obj);
	//删除数据通过id
	public void deleteObject(int id);
	//修改数据通过传递的obj
	public void modifyObject(Object obj);
	//释放数据库资源
	public void releaseDatabaseRes();
}
