package com.pinyougou.sellergoods.service.impl;
import java.util.List;

import com.pinyougou.com.pinyougou.pojogroup.SpecificationGroup;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.entity.PageResult;
/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(SpecificationGroup specificationGroup) {
		//插入规格
		specificationMapper.insert(specificationGroup.getSpecification());
		//循环插入规格选项
		for (TbSpecificationOption specificationOption : specificationGroup.getSpecificationOptionList()) {
			//设置插入选项规格的id
			specificationOption.setSpecId(specificationGroup.getSpecification().getId());
			specificationOptionMapper.insert(specificationOption);
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(SpecificationGroup specificationGroup){
		//修改规格
		specificationMapper.updateByPrimaryKey(specificationGroup.getSpecification());
		//删除原有规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specificationGroup.getSpecification().getId());
		specificationOptionMapper.deleteByExample(example);
		//更新现在的数据
		for (TbSpecificationOption specificationOption : specificationGroup.getSpecificationOptionList()) {
			//设置外键ID
			specificationOption.setSpecId(specificationGroup.getSpecification().getId());
			specificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public SpecificationGroup findOne(Long id){
		//查询规格
		TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
		//查询规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		//通过specId擦查询
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
		//创建返回对象
		SpecificationGroup specificationGroup = new SpecificationGroup();
		//设置返回值
		specificationGroup.setSpecification(specification);
		specificationGroup.setSpecificationOptionList(specificationOptionList);
		return specificationGroup;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			//级联删除
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
